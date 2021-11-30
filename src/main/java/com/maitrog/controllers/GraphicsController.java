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
    private void plotChart(ActionEvent e) {
        lineChart.getData().clear();
        addData();
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
