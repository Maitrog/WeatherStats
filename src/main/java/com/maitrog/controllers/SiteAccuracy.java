package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.models.City;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Locale;
import com.maitrog.models.SiteType;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
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
    private ImageView loadGif;

    @FXML
    private CategoryAxis daysSinceText;

    @FXML
    private NumberAxis mistakeText;

    @FXML
    private void createChart() {
        lineChart.getData().clear();
        switch (comboBox.getSelectedValue()) {
            case "Rambler" -> {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                addData(SiteType.Rambler, series);
                series.setName("Rambler");
            }
            case "WorldWeather" -> {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                addData(SiteType.WorldWeather, series);
                series.setName("World Weather");
            }
            case "Yandex" -> {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                addData(SiteType.Yandex, series);
                series.setName("Yandex");
            }
            default -> {
                XYChart.Series<String, Number> seriesYandex = new XYChart.Series<>();
                addData(SiteType.Yandex, seriesYandex);
                seriesYandex.setName("Yandex");
                XYChart.Series<String, Number> seriesWorldWeather = new XYChart.Series<>();
                addData(SiteType.WorldWeather, seriesWorldWeather);
                seriesWorldWeather.setName("WorldWeather");
                XYChart.Series<String, Number> seriesRambler = new XYChart.Series<>();
                addData(SiteType.Rambler, seriesRambler);
                seriesRambler.setName("Rambler");
            }
        }
    }

    private void addData(SiteType siteType, XYChart.Series<String, Number> series) {
        loadButton.setDisable(true);
        loadGif.setVisible(true);

        Thread plot = new Thread(() -> {
            long startTime = System.currentTimeMillis();

            List<double[]> allSumAvgTemperatures = new ArrayList<>();
            List<int[]> allCountAvgTemperatures = new ArrayList<>();
            List<Future<Void>> futures = new ArrayList<>();
            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            try {
                List<List<Double>> allAvgTemp = DbWeather.getInstance().getAllAvgTemperature();
                List<List<List<Weather>>> allSortedWeather = DbWeather.getInstance().getAllSortedWeather(siteType);
                for (int j = 0, citiesSize = allAvgTemp.size(); j < citiesSize; j++) {
                    int finalJ = j;
                    futures.add(service.submit(() -> {
                        List<Double> realAvgTemp = allAvgTemp.get(finalJ);
                        List<List<Weather>> sortedWeather = allSortedWeather.get(finalJ);
                        int datesSize = realAvgTemp.size();
                        double[] sumAvgTemp = new double[31];
                        int[] countAvgTemp = new int[31];
                        IntStream.range(0, datesSize).forEach(i -> {
                            List<Weather> weathers = sortedWeather.get(i);
                            for (Weather weather : weathers) {
                                long day = (weather.getTargetDate().getTime() - weather.getCheckedDate().getTime()) / 1000 / 60 / 60 / 24;
                                double avgTemp = (weather.getMaxTemperature() - weather.getMinTemperature()) / 2.0 + weather.getMinTemperature();
                                double mistake = Math.abs(realAvgTemp.get(i) - avgTemp);
                                sumAvgTemp[(int) day] += mistake;
                                countAvgTemp[(int) day]++;
                            }
                        });
                        allSumAvgTemperatures.add(sumAvgTemp);
                        allCountAvgTemperatures.add(countAvgTemp);

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

            IntStream.range(0, 14).forEach(i -> series.getData().add(new XYChart.Data<>(String.valueOf(i), totalAvgTemperature[i])));
            Platform.runLater(() -> {
                loadGif.setVisible(false);
                lineChart.getData().add(series);
                loadButton.setDisable(false);
            });
            long endTime = System.currentTimeMillis();
            Main.logger.log(Level.INFO, "Time: " + (endTime - startTime) + " ms");
        });
        plot.setDaemon(true);
        plot.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add("Все");
        localize();
        comboBox.getItems().add("Yandex");
        comboBox.getItems().add("Rambler");
        comboBox.getItems().add("WorldWeather");
    }

    public void localize()
    {
        ObjectMapper mapper = new ObjectMapper();
        List<com.maitrog.models.Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(Main.user.getLanguage())
        {
            case "ru":
                lineChart.setTitle(locale.get(23).getRu());
                comboBox.setPromptText(locale.get(17).getRu());
                daysSinceText.setLabel(locale.get(24).getRu());
                mistakeText.setLabel(locale.get(21).getRu());
                comboBox.setPromptText(locale.get(17).getRu());
                loadButton.setText(locale.get(22).getRu());
                break;
            case "en":
                lineChart.setTitle(locale.get(23).getEn());
                comboBox.setPromptText(locale.get(17).getEn());
                daysSinceText.setLabel(locale.get(24).getEn());
                mistakeText.setLabel(locale.get(21).getEn());
                comboBox.setPromptText(locale.get(17).getEn());
                loadButton.setText(locale.get(22).getEn());
                break;
            default:
                break;
        }
    }
}
