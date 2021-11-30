package com.maitrog.controllers;

import com.maitrog.models.City;
import com.maitrog.models.DbWeather;
import com.maitrog.models.SiteType;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class SiteAccuracy implements Initializable {

    @FXML
    MFXComboBox<String> comboBox;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private MFXButton loadButton;

    @FXML
    private void createChart() {
        lineChart.getData().clear();
        addData();
    }

    private void addData() {
        loadButton.setDisable(true);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        SiteType siteType;
        switch (comboBox.getSelectedValue()) {
            case "Rambler" -> {
                siteType = SiteType.Rambler;
                series.setName("Rambler");
            }
            case "WorldWeather" -> {
                siteType = SiteType.WorldWeather;
                series.setName("World Weather");
            }
            default -> {
                siteType = SiteType.Yandex;
                series.setName("Yandex");
            }
        }
        Thread plot = new Thread(() -> {
            AtomicReference<List<City>> atomicCities = new AtomicReference<>();
            try {
                atomicCities.set(DbWeather.getInstance().getAllCities());
            } catch (SQLException | ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

            List<double[]> allSumAvgTemperatures = new ArrayList<>();
            List<int[]> allCountAvgTemperatures = new ArrayList<>();
            List<Future<Void>> futures = new ArrayList<>();
            List<City> cities = atomicCities.get();
            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            try {
                for (City city : cities) {
                    futures.add(service.submit(() -> {
                        try {
                            List<Double> realAvgTemp = DbWeather.getInstance().getAllAvgTemperature(city.getId());
                            List<List<Weather>> sortedWeather = DbWeather.getInstance().getSortedWeathers(city.getId(), siteType);
                            int datesSize = sortedWeather.size();
                            double[] sumAvgTemp = new double[31];
                            int[] countAvgTemp = new int[31];
                            for (int i = 0; i < datesSize; i++) {

                                List<Weather> weathers = sortedWeather.get(i);
                                for (Weather weather : weathers) {
                                    long day = (weather.getTargetDate().getTime() - weather.getCheckedDate().getTime()) / 1000 / 60 / 60 / 24;
                                    double avgTemp = (weather.getMaxTemperature() - weather.getMinTemperature()) / 2.0 + weather.getMinTemperature();
                                    double mistake = Math.abs(realAvgTemp.get(i) - avgTemp);
                                    sumAvgTemp[(int) day] += mistake;
                                    countAvgTemp[(int) day]++;
                                }
                            }
                            allSumAvgTemperatures.add(sumAvgTemp);
                            allCountAvgTemperatures.add(countAvgTemp);

                        } catch (SQLException | ClassNotFoundException | IOException e) {
                            Main.logger.log(Level.SEVERE, e.getMessage());
                        }
                        return null;
                    }));
                }
            } catch (Exception e) {
                Main.logger.log(Level.SEVERE, e.getMessage());
            } finally {
                service.shutdown();
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            int[] totalCountAvg = new int[31];
            double[] totalSumAvgTemp = new double[31];
            for (int i = 0; i < allSumAvgTemperatures.size(); i++) {
                for (int j = 0; j < totalSumAvgTemp.length; j++) {
                    totalSumAvgTemp[j] += allSumAvgTemperatures.get(i)[j];
                    totalCountAvg[j] += allCountAvgTemperatures.get(i)[j];
                }
            }

            double[] totalAvgTemperature = new double[31];
            IntStream.range(0, totalCountAvg.length).forEach(i -> totalAvgTemperature[i] = totalSumAvgTemp[i] / totalCountAvg[i]);

            IntStream.range(0, 15).forEach(i -> series.getData().add(new XYChart.Data<>(String.valueOf(i), totalAvgTemperature[i])));
            Platform.runLater(() -> {
                lineChart.getData().add(series);
                loadButton.setDisable(false);
            });
        });
        plot.setDaemon(true);
        plot.start();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add("Yandex");
        comboBox.getItems().add("Rambler");
        comboBox.getItems().add("WorldWeather");
    }
}
