package com.maitrog.controllers;

import com.maitrog.models.*;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
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
import javafx.fxml.LoadListener;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class MistakeGraphicsController implements Initializable {

    @FXML
    CategoryAxis xAxis = new CategoryAxis();

    @FXML
    NumberAxis yAxis = new NumberAxis();

    @FXML
    private LineChart<String, Number> mistakeLineChart = new LineChart<String, Number>(xAxis, yAxis);;

    @FXML
    private MFXComboBox<String> mistakeComboBox;

    @FXML
    private MFXTextField mistakeTextField;

    @FXML
    private MFXDatePicker mistakeDatePicker;

    @FXML
    private MFXButton plotMistakeChartButton;

    @FXML
    private void plotMistakeChart(ActionEvent e) {
        mistakeLineChart.getData().clear();
        addData();
    }

    private void addData() {
        if (mistakeDatePicker.getDate().isAfter(LocalDate.now())) return;
        try {
            List<Weather> ramblerDateWeather;
            List<Weather> yandexDateWeather;
            List<Weather> worldDateWeather;
            XYChart.Series<String, Number> ramblerSeries = new XYChart.Series();
            XYChart.Series<String, Number> yandexSeries = new XYChart.Series();
            XYChart.Series<String, Number> worldSeries = new XYChart.Series();
            ramblerDateWeather = DbWeather.getInstance().getWeathers(mistakeTextField.getText(), SiteType.Rambler,
                    Date.valueOf(mistakeDatePicker.getDate()));
            yandexDateWeather = DbWeather.getInstance().getWeathers(mistakeTextField.getText(), SiteType.Yandex,
                    Date.valueOf(mistakeDatePicker.getDate()));
            worldDateWeather = DbWeather.getInstance().getWeathers(mistakeTextField.getText(), SiteType.WorldWeather,
                    Date.valueOf(mistakeDatePicker.getDate()));

            Weather ramblerWeather = ramblerDateWeather.get(ramblerDateWeather.size() - 1);
            Weather yandexWeather = yandexDateWeather.get(yandexDateWeather.size() - 1);
            Weather worldWeather = worldDateWeather.get(worldDateWeather.size() - 1);
            double ramblerTemp = (ramblerWeather.getMaxTemperature() - ramblerWeather.getMinTemperature()) / 2
                    + ramblerWeather.getMinTemperature();
            double yandexTemp = (yandexWeather.getMaxTemperature() - yandexWeather.getMinTemperature()) / 2
                    + yandexWeather.getMinTemperature();;
            double worldTemp = (worldWeather.getMaxTemperature() - worldWeather.getMinTemperature()) / 2
                    + worldWeather.getMinTemperature();
            double currentWeather = (ramblerTemp + yandexTemp + worldTemp) / 3;

            switch (mistakeComboBox.getSelectedValue()) {
                case "Rambler" -> {
                    for (Weather weather : ramblerDateWeather) {
                        ramblerSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2
                                        + weather.getMinTemperature() - currentWeather)));
                    }
                    mistakeLineChart.getData().add(ramblerSeries);
                    ramblerSeries.setName("Rambler");
                }
                case "Yandex" -> {
                    for (Weather weather : yandexDateWeather) {
                        yandexSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2
                                        + weather.getMinTemperature() - currentWeather)));
                    }
                    mistakeLineChart.getData().add(yandexSeries);
                    yandexSeries.setName("Yandex");
                }
                case "WorldWeather" -> {
                    for (Weather weather : worldDateWeather) {
                        worldSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2
                                        + weather.getMinTemperature() - currentWeather)));
                    }
                    mistakeLineChart.getData().add(worldSeries);
                    worldSeries.setName("WorldWeather");
                }
                default -> {
                    for (Weather weather : ramblerDateWeather) {
                        ramblerSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2
                                        + weather.getMinTemperature() - currentWeather)));
                    }
                    mistakeLineChart.getData().add(ramblerSeries);
                    ramblerSeries.setName("Rambler");

                    for (Weather weather : yandexDateWeather) {
                        yandexSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2
                                        + weather.getMinTemperature() - currentWeather)));
                    }
                    mistakeLineChart.getData().add(yandexSeries);
                    yandexSeries.setName("Yandex");

                    for (Weather weather : worldDateWeather) {
                        worldSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                Math.abs((weather.getMaxTemperature() - weather.getMinTemperature()) / 2
                                        + weather.getMinTemperature() - currentWeather)));
                    }
                    mistakeLineChart.getData().add(worldSeries);
                    worldSeries.setName("WorldWeather");
                }
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mistakeComboBox.getItems().add("All");
        mistakeComboBox.getItems().add("Yandex");
        mistakeComboBox.getItems().add("Rambler");
        mistakeComboBox.getItems().add("WorldWeather");
    }
}
