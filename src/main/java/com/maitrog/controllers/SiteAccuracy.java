package com.maitrog.controllers;

import com.maitrog.models.City;
import com.maitrog.models.DbWeather;
import com.maitrog.models.SiteType;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class SiteAccuracy implements Initializable {

    @FXML
    MFXComboBox comboBox;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private void createChart(ActionEvent e) throws InterruptedException, ExecutionException {
        lineChart.getData().clear();
        addData();
    }

    private void addData() throws InterruptedException, ExecutionException {
        XYChart.Series<String, Number> series = new XYChart.Series();
        SiteType siteType;
        switch (comboBox.getSelectedValue().toString()) {
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
        AtomicReference<List<City>> atomicCities = new AtomicReference<>();
        Thread loadCities = new Thread(() -> {
            try {
                atomicCities.set(DbWeather.getInstance().getAllCities());
            } catch (SQLException | ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });
        loadCities.setDaemon(true);
        loadCities.start();
        loadCities.join();

        List<double[]> allSumAvgTemperatures = new ArrayList<>();
        List<int[]> allCountAvgTemperatures = new ArrayList<>();
        List<Future> futures = new ArrayList<>();
        List<City> cities = atomicCities.get();
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            for (City city : cities) {
                futures.add(service.submit(new Callable<Void>() {
                    @Override
                    public Void call() {
                        try {
                            long startTime = System.currentTimeMillis();
                            double[] sumAvgTemp = new double[30];
                            int[] countAvgTemp = new int[30];
                            List<Date> dates = DbWeather.getInstance().getTargetDate(city.getId(), new Date(System.currentTimeMillis()));
                            for (Date date : dates) {

                                List<Weather> yandexWeather = DbWeather.getInstance().getWeathers(city.getNameRu(), siteType, date);
                                double realAvgTemp = DbWeather.getInstance().getAvgTemperature(city.getId(), date);

                                if (realAvgTemp != -9999) {
                                    for (Weather weather : yandexWeather) {
                                        long day = (weather.getTargetDate().getTime() - weather.getCheckedDate().getTime()) / 1000 / 60 / 60 / 24;
                                        double avgTemp = (weather.getMaxTemperature() - weather.getMinTemperature()) / 2.0 + weather.getMinTemperature();
                                        double mistake = Math.abs(realAvgTemp - avgTemp);
                                        sumAvgTemp[(int) day] += mistake;
                                        countAvgTemp[(int) day]++;
                                    }
                                }
                            }
                            allSumAvgTemperatures.add(sumAvgTemp);
                            allCountAvgTemperatures.add(countAvgTemp);

                            long endTime = System.currentTimeMillis();
                            Main.logger.log(Level.INFO, "Total execution time: " + (endTime - startTime) + "ms");
                        } catch (SQLException | ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }));
            }
        } catch (Exception e) {
            Main.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            service.shutdown();
        }

        for (Future future : futures) {
            future.get();
        }

        int[] totalCountAvg = new int[30];
        double[] totalSumAvgTemp = new double[30];
        for (int i = 0; i < allSumAvgTemperatures.size(); i++) {
            for (int j = 0; j < totalSumAvgTemp.length; j++) {
                totalSumAvgTemp[j] += allSumAvgTemperatures.get(i)[j];
                totalCountAvg[j] += allCountAvgTemperatures.get(i)[j];
            }
        }

        double[] totalAvgTemperature = new double[30];
        for (int i = 0; i < totalCountAvg.length; i++) {
            totalAvgTemperature[i] = totalSumAvgTemp[i] / totalCountAvg[i];
        }

        for (int i = 0; i < 30; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), totalAvgTemperature[i]));
        }
        lineChart.getData().add(series);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add("Yandex");
        comboBox.getItems().add("Rambler");
        comboBox.getItems().add("WorldWeather");
    }
}
