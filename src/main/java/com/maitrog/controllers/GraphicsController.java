package com.maitrog.controllers;

import com.maitrog.models.*;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class GraphicsController implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private MFXComboBox<String> comboBox;

    @FXML
    private MFXTextField textField;


    @FXML
    private MFXDatePicker datePicker;

    @FXML
    private MFXButton plotChartButton;

    @FXML
    private MFXButton plotMedianButton;

    @FXML
    private void plotMedian() {
        lineChart.getData().clear();
        addMedian();
    }

    @FXML
    private void plotChart(ActionEvent e) {
        lineChart.getData().clear();
        addData();
    }

    private void addMedian() {
        XYChart.Series<String, Number> series = new XYChart.Series();
        List<Weather> weathers = new ArrayList<>();
        try {
            switch (comboBox.getSelectedValue()) {
                case "Rambler" -> {
                    weathers = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Rambler");
                }
                case "Yandex" -> {
                    weathers = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Yandex");
                }
                case "WorldWeather" -> {
                    weathers = DbWeather.getInstance().getWeathers(textField.getText(),
                            SiteType.WorldWeather, Date.valueOf(datePicker.getDate()));
                    series.setName("WorldWeather");
                }
                case "All" -> {
                    List<Weather> ramblerDateWeather;
                    List<Weather> yandexDateWeather;
                    List<Weather> worldDateWeather;
                    XYChart.Series<String, Number> ramblerSeries = new XYChart.Series();
                    XYChart.Series<String, Number> yandexSeries = new XYChart.Series();
                    XYChart.Series<String, Number> worldSeries = new XYChart.Series();
                    ramblerDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    yandexDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    worldDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.WorldWeather,
                            Date.valueOf(datePicker.getDate()));
                    List<Double> parsedRambler = new ArrayList<>();
                    List<Double> parsedYandex = new ArrayList<>();
                    List<Double> parsedWorld = new ArrayList<>();
                    double median;
                    for (Weather weather : ramblerDateWeather) {
                        double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                        temperature /= 2;
                        temperature += weather.getMinTemperature();
                        parsedRambler.add(temperature);
                    }
                    Collections.sort(parsedRambler);
                    if (parsedRambler.size() % 2 == 0) median = (parsedRambler.get(parsedRambler.size() / 2) + parsedRambler.get(parsedRambler.size() / 2 - 1)) / 2;
                    else median = parsedRambler.get(parsedRambler.size() / 2);
                    ramblerSeries.getData().add(new XYChart.Data<>(datePicker.getDate().toString(), median));
                    lineChart.getData().add(ramblerSeries);
                    ramblerSeries.setName("Rambler");
                    for (Weather weather : yandexDateWeather) {
                        double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                        temperature /= 2;
                        temperature += weather.getMinTemperature();
                        parsedYandex.add(temperature);
                    }
                    Collections.sort(parsedYandex);
                    if (parsedYandex.size() % 2 == 0) median = (parsedYandex.get(parsedYandex.size() / 2) + parsedYandex.get(parsedYandex.size() / 2 - 1)) / 2;
                    else median = parsedYandex.get(parsedYandex.size() / 2);
                    yandexSeries.getData().add(new XYChart.Data<>(datePicker.getDate().toString(), median));
                    lineChart.getData().add(yandexSeries);
                    yandexSeries.setName("Yandex");
                    for (Weather weather : worldDateWeather) {
                        double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                        temperature /= 2;
                        temperature += weather.getMinTemperature();
                        parsedWorld.add(temperature);
                    }
                    Collections.sort(parsedWorld);
                    if (parsedWorld.size() % 2 == 0) median = (parsedWorld.get(parsedWorld.size() / 2) + parsedWorld.get(parsedWorld.size() / 2 - 1)) / 2;
                    else median = parsedWorld.get(parsedWorld.size() / 2);
                    worldSeries.getData().add(new XYChart.Data<>(datePicker.getDate().toString(), median));
                    lineChart.getData().add(worldSeries);
                    worldSeries.setName("WorldWeather");
                    return;
                }
            }
            List<Double> parsedWeather = new ArrayList<>();
            for (Weather weather : weathers) {
                double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                temperature /= 2;
                temperature += weather.getMinTemperature();
                parsedWeather.add(temperature);
            }
            Collections.sort(parsedWeather);
            double median;
            if (parsedWeather.size() % 2 == 0) median = (parsedWeather.get(parsedWeather.size() / 2) + parsedWeather.get(parsedWeather.size() / 2 - 1)) / 2;
            else median = parsedWeather.get(parsedWeather.size() / 2);
            series.getData().add(new XYChart.Data<>(datePicker.getDate().toString(), median));
            lineChart.getData().add(series);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addData() {
        XYChart.Series<String, Number> series = new XYChart.Series();
        List<Weather> targetDateWeather = new ArrayList<>();
        try {
            switch (comboBox.getSelectedValue()) {
                case "Rambler" -> {
                    targetDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Rambler");
                }
                case "Yandex" -> {
                    targetDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Yandex");
                }
                case "WorldWeather" -> {
                    targetDateWeather = DbWeather.getInstance().getWeathers(textField.getText(),
                            SiteType.WorldWeather, Date.valueOf(datePicker.getDate()));
                    series.setName("WorldWeather");
                }
                case "All" -> {
                    List<Weather> ramblerDateWeather;
                    List<Weather> yandexDateWeather;
                    List<Weather> worldDateWeather;
                    XYChart.Series<String, Number> ramblerSeries = new XYChart.Series();
                    XYChart.Series<String, Number> yandexSeries = new XYChart.Series();
                    XYChart.Series<String, Number> worldSeries = new XYChart.Series();
                    ramblerDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    yandexDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    worldDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.WorldWeather,
                            Date.valueOf(datePicker.getDate()));
                    for (Weather weather : ramblerDateWeather) {
                        ramblerSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
                    }
                    lineChart.getData().add(ramblerSeries);
                    ramblerSeries.setName("Rambler");
                    for (Weather weather : yandexDateWeather) {
                        yandexSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
                    }
                    lineChart.getData().add(yandexSeries);
                    yandexSeries.setName("Yandex");
                    for (Weather weather : worldDateWeather) {
                        worldSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
                    }
                    lineChart.getData().add(worldSeries);
                    worldSeries.setName("WorldWeather");
                    return;
                }
            }

            for (Weather weather : targetDateWeather) {
                series.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                        (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
            }
            lineChart.getData().add(series);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add("All");
        comboBox.getItems().add("Yandex");
        comboBox.getItems().add("Rambler");
        comboBox.getItems().add("WorldWeather");

    }
}
