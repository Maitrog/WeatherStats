package com.maitrog.controllers;

import com.maitrog.models.DbWeather;
import com.maitrog.models.SiteType;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
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
    private void plotMistakeChart() {
        mistakeLineChart.getData().clear();
        addData();
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
        mistakeComboBox.getItems().add("All");
        mistakeComboBox.getItems().add("Yandex");
        mistakeComboBox.getItems().add("Rambler");
        mistakeComboBox.getItems().add("WorldWeather");
    }
}
