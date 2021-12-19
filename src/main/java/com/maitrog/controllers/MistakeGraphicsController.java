package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Locale;
import com.maitrog.models.SiteType;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

public class MistakeGraphicsController implements Initializable {

    @FXML
    CategoryAxis xAxis = new CategoryAxis();

    @FXML
    NumberAxis yAxis = new NumberAxis();

    @FXML
    private LineChart<String, Number> mistakeLineChart = new LineChart<>(xAxis, yAxis);

    @FXML
    private MFXComboBox<String> mistakeComboBox;

    @FXML
    private MFXTextField mistakeTextField;

    @FXML
    private MFXDatePicker mistakeDatePicker;

    @FXML
    private MFXButton plotMistakeChartButton;

    @FXML
    private MFXButton plotDistributionLawButton;

    @FXML
    private MFXTextField dateDiffField;

    @FXML
    private void plotMistakeChart() {
        mistakeLineChart.getData().clear();
        xAxis.setLabel("Дата проверки");
        yAxis.setLabel("Дельта температур");
        addData();
    }

    private void fixRequest(List<Weather> weather) {
        while (weather.size() >= 15) weather.remove(0);
    }

    @FXML
    private void plotDistributionLaw() {
        mistakeLineChart.getData().clear();
        xAxis.setLabel("Отклонение температуры");
        yAxis.setLabel("Количество таких отклонений");
        makeDistributionLaw(30, Integer.parseInt(dateDiffField.getText()));//считывание с источника
    }

    private void makeDistributionLaw(int minusDays, int dateDiff) {
        if (mistakeDatePicker.getDate().isAfter(LocalDate.now())) return;
        Thread plot = new Thread(() -> {
            try {
                XYChart.Series<String, Number> distributionSeries = new XYChart.Series<>();

                List<Weather> ramblerDateWeather = getWeathers(SiteType.Rambler);
                List<Weather> yandexDateWeather = getWeathers(SiteType.Yandex);
                List<Weather> worldDateWeather = getWeathers(SiteType.WorldWeather);

                fixRequest(ramblerDateWeather);
                fixRequest(yandexDateWeather);
                fixRequest(worldDateWeather);

                Weather ramblerWeather = ramblerDateWeather.get(ramblerDateWeather.size() - 1);
                Weather yandexWeather = yandexDateWeather.get(yandexDateWeather.size() - 1);
                Weather worldWeather = worldDateWeather.get(worldDateWeather.size() - 1);
                double ramblerTemp = (ramblerWeather.getMaxTemperature() - ramblerWeather.getMinTemperature()) / 2.0
                        + ramblerWeather.getMinTemperature();
                double yandexTemp = (yandexWeather.getMaxTemperature() - yandexWeather.getMinTemperature()) / 2.0
                        + yandexWeather.getMinTemperature();
                double worldTemp = (worldWeather.getMaxTemperature() - worldWeather.getMinTemperature()) / 2.0
                        + worldWeather.getMinTemperature();
                double tmp = (ramblerTemp + yandexTemp + worldTemp) / 3;
                Platform.runLater(() -> {
                    try {
                int currentWeather;
                Date lowestDate = Date.valueOf(mistakeDatePicker.getDate().minusDays(minusDays));
                    List<Pair<Date, Double>> currentWeathers = null;
                        currentWeathers = DbWeather.getInstance().getAvgTemperature(mistakeTextField.getText(),
                                lowestDate, Date.valueOf(mistakeDatePicker.getDate()));

                    List<Weather> distributionWeather = DbWeather.getInstance().getDistributionData(mistakeTextField.getText(),
                        Date.valueOf(mistakeDatePicker.getDate()), lowestDate, dateDiff);
                List<Integer> tempsMistake = new ArrayList<>();

                HashMap<Integer, Integer> countMistake = new HashMap<>();

                    for (Weather weather : distributionWeather) {
                        Optional<Pair<Date, Double>> currentPair = currentWeathers.stream().filter(x-> x.getKey().equals(weather.getTargetDate())).findFirst();
                        if(currentPair.isEmpty()) continue;
                        currentWeather = (int)Math.round(currentPair.get().getValue());
                        double temp = (weather.getMaxTemperature() - weather.getMinTemperature()) / 2.0 + weather.getMinTemperature();
                        tempsMistake.add(currentWeather - (int)Math.round(temp));
                    }

                    for (int mistake : tempsMistake) {
                        if (countMistake.containsKey(mistake)) countMistake.put(mistake, countMistake.get(mistake) + 1);
                        else countMistake.put(mistake, 1);
                    }

                    for(Map.Entry<Integer, Integer> entry : countMistake.entrySet()) {
                        distributionSeries.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
                    }

                    Collections.sort(distributionSeries.getData(), new Comparator<XYChart.Data>() {
                        @Override
                        public int compare(XYChart.Data o1, XYChart.Data o2) {
                            Integer.parseInt(o1.getXValue().toString());
                            Number xValue1 = Integer.parseInt(o1.getXValue().toString());
                            Number xValue2 = Integer.parseInt(o2.getXValue().toString());
                            return new BigDecimal(xValue1.toString()).compareTo(new BigDecimal(xValue2.toString()));
                        }
                    });

                    mistakeLineChart.getData().add(distributionSeries);
                    distributionSeries.setName("Distribution Law");
                    } catch (SQLException | ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (SQLException | ClassNotFoundException |
                    IOException e) {
                Main.logger.log(Level.SEVERE, e.getMessage());
            }
        });
        plot.setDaemon(true);
        plot.start();
    }

    private void addData() {
        if (mistakeDatePicker.getDate().isAfter(LocalDate.now())) return;
        Thread plot = new Thread(() -> {
            try {
                XYChart.Series<String, Number> ramblerSeries = new XYChart.Series<>();
                XYChart.Series<String, Number> yandexSeries = new XYChart.Series<>();
                XYChart.Series<String, Number> worldSeries = new XYChart.Series<>();
                List<Weather> ramblerDateWeather = getWeathers(SiteType.Rambler);
                List<Weather> yandexDateWeather = getWeathers(SiteType.Yandex);
                List<Weather> worldDateWeather = getWeathers(SiteType.WorldWeather);
              
                fixRequest(ramblerDateWeather);
                fixRequest(yandexDateWeather);
                fixRequest(worldDateWeather);

                Weather ramblerWeather = ramblerDateWeather.get(ramblerDateWeather.size() - 1);
                Weather yandexWeather = yandexDateWeather.get(yandexDateWeather.size() - 1);
                Weather worldWeather = worldDateWeather.get(worldDateWeather.size() - 1);
                double ramblerTemp = (ramblerWeather.getMaxTemperature() - ramblerWeather.getMinTemperature()) / 2.0
                        + ramblerWeather.getMinTemperature();
                double yandexTemp = (yandexWeather.getMaxTemperature() - yandexWeather.getMinTemperature()) / 2.0
                        + yandexWeather.getMinTemperature();
                double worldTemp = (worldWeather.getMaxTemperature() - worldWeather.getMinTemperature()) / 2.0
                        + worldWeather.getMinTemperature();
                double currentWeather = (ramblerTemp + yandexTemp + worldTemp) / 3;

                Platform.runLater(() -> {
                    switch (mistakeComboBox.getSelectedValue()) {
                        case "Rambler" -> {
                            AddData(ramblerDateWeather, ramblerSeries, currentWeather);
                            mistakeLineChart.getData().add(ramblerSeries);
                            ramblerSeries.setName("Rambler");
                        }
                        case "Yandex" -> {
                            AddData(yandexDateWeather, yandexSeries, currentWeather);
                            mistakeLineChart.getData().add(yandexSeries);
                            yandexSeries.setName("Yandex");
                        }
                        case "WorldWeather" -> {
                            AddData(worldDateWeather, worldSeries, currentWeather);
                            mistakeLineChart.getData().add(worldSeries);
                            worldSeries.setName("WorldWeather");
                        }
                        default -> {
                            AddData(ramblerDateWeather, ramblerSeries, currentWeather);
                            mistakeLineChart.getData().add(ramblerSeries);
                            ramblerSeries.setName("Rambler");

                            AddData(yandexDateWeather, yandexSeries, currentWeather);
                            mistakeLineChart.getData().add(yandexSeries);
                            yandexSeries.setName("Yandex");

                            AddData(worldDateWeather, worldSeries, currentWeather);
                            mistakeLineChart.getData().add(worldSeries);
                            worldSeries.setName("WorldWeather");
                        }
                    }
                });
            } catch (SQLException | ClassNotFoundException |
                    IOException e) {
                Main.logger.log(Level.SEVERE, e.getMessage());
            }
        });
        plot.setDaemon(true);
        plot.start();
    }

    private List<Weather> getWeathers(SiteType rambler) throws SQLException, ClassNotFoundException, IOException {
        List<Weather> ramblerDateWeather;
        ramblerDateWeather = DbWeather.getInstance().getWeathers(mistakeTextField.getText(), rambler,
                Date.valueOf(mistakeDatePicker.getDate()));
        return ramblerDateWeather;
    }

    private void AddData(List<Weather> ramblerDateWeather, XYChart.Series<String, Number> ramblerSeries, double currentWeather) {
        for (Weather weather : ramblerDateWeather) {
            ramblerSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                    Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2.0
                            + weather.getMinTemperature() - currentWeather)));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localize();
        if(Main.user.getLanguage().equals("en")) {
            mistakeComboBox.getItems().add("All");
            mistakeComboBox.getItems().add("Yandex");
            mistakeComboBox.getItems().add("Rambler");
            mistakeComboBox.getItems().add("WorldWeather");
        } else
        {
            mistakeComboBox.getItems().add("Все");
            mistakeComboBox.getItems().add("Yandex");
            mistakeComboBox.getItems().add("Rambler");
            mistakeComboBox.getItems().add("WorldWeather");
        }
    }

    public void localize()
    {
        ObjectMapper mapper = new ObjectMapper();
        List<Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(Main.user.getLanguage())
        {
            case "ru":
                mistakeLineChart.setTitle(locale.get(20).getRu());
                xAxis.setLabel(locale.get(19).getRu());
                yAxis.setLabel(locale.get(21).getRu());
                mistakeComboBox.setPromptText(locale.get(17).getRu());
                plotMistakeChartButton.setText(locale.get(22).getRu());
                plotDistributionLawButton.setText(locale.get(30).getRu());
                break;
            case "en":
                mistakeLineChart.setTitle(locale.get(20).getEn());
                xAxis.setLabel(locale.get(19).getEn());
                yAxis.setLabel(locale.get(21).getEn());
                mistakeComboBox.setPromptText(locale.get(17).getEn());
                plotMistakeChartButton.setText(locale.get(22).getEn());
                plotDistributionLawButton.setText(locale.get(30).getEn());
                break;
            default:
                break;
        }
    }
}
