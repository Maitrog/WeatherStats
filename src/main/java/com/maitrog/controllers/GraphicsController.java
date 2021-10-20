package com.maitrog.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXHamburger;
import com.maitrog.models.*;
import javafx.event.EventHandler;
import javafx.fxml.*;

import java.io.Console;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;

public class GraphicsController implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private JFXButton loadButton;

    @FXML
    private JFXComboBox<String> comboBox;

    @FXML
    private TextField textField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private void handleButtonAction(ActionEvent e) {
        lineChart.getData().clear();
        addData();

        //addData(series);
        /*
        series.getData().add(new XYChart.Data<String, Number>("1", 2));
        series.getData().add(new XYChart.Data<String, Number>("2", 5));

        series1.getData().add(new XYChart.Data<String, Number>("1", 7));
        series1.getData().add(new XYChart.Data<String, Number>("3", 1));

        series.setName(comboBox.getValue() + ": " + datePicker.getValue());

        lineChart.getData().add(addData());
        */
    }

    private void addData() {
        XYChart.Series<String, Number> series = new XYChart.Series();
        List<Weather> targetDateWeather = new ArrayList<Weather>();
        try {//add function to get weather with target date, SiteType, city (string, sitetype, date)
            switch (comboBox.getValue()) {
                case "Rambler":
                    targetDateWeather = DbWeather.getInstance().getAllWeathers(textField.getText(), SiteType.Rambler,
                            datePicker.getValue());
                    break;
                case "Yandex":
                    targetDateWeather = DbWeather.getInstance().getAllWeathers(textField.getText(), SiteType.Yandex,
                            datePicker.getValue());
                    break;
                case "WorldWeather":
                    targetDateWeather = DbWeather.getInstance().getAllWeathers(textField.getText(),
                            SiteType.WorldWeather, datePicker.getValue());
                    break;
                case "All":
                    //for all sources at the same time
                    List<Weather> ramblerDateWeather = new ArrayList<Weather>();
                    List<Weather> yandexDateWeather = new ArrayList<Weather>();
                    List<Weather> worldDateWeather = new ArrayList<Weather>();

                    XYChart.Series<String, Number> ramblerSeries = new XYChart.Series();
                    XYChart.Series<String, Number> yandexSeries = new XYChart.Series();
                    XYChart.Series<String, Number> worldSeries = new XYChart.Series();

                    ramblerDateWeather = DbWeather.getInstance().getAllWeathers(textField.getText(), SiteType.Rambler,
                            datePicker.getValue());
                    yandexDateWeather = DbWeather.getInstance().getAllWeathers(textField.getText(), SiteType.Yandex,
                            datePicker.getValue());
                    worldDateWeather = DbWeather.getInstance().getAllWeathers(textField.getText(), SiteType.Yandex,
                            datePicker.getValue());

                    for (int i = 0; i < ramblerDateWeather.size(); i++) {
                        ramblerSeries.getData().add(new XYChart.Data<String, Number>(ramblerDateWeather.get(i).getCheckedDate().toString(),
                                (ramblerDateWeather.get(i).getMaxTemperature() - ramblerDateWeather.get(i).getMinTemperature()) / 2));
                    }
                    lineChart.getData().add(ramblerSeries);

                    for (int i = 0; i < yandexDateWeather.size(); i++) {
                        yandexSeries.getData().add(new XYChart.Data<String, Number>(yandexDateWeather.get(i).getCheckedDate().toString(),
                                (yandexDateWeather.get(i).getMaxTemperature() - yandexDateWeather.get(i).getMinTemperature()) / 2));
                    }
                    lineChart.getData().add(yandexSeries);

                    for (int i = 0; i < worldDateWeather.size(); i++) {
                        worldSeries.getData().add(new XYChart.Data<String, Number>(worldDateWeather.get(i).getCheckedDate().toString(),
                                (worldDateWeather.get(i).getMaxTemperature() - worldDateWeather.get(i).getMinTemperature()) / 2));
                    }
                    lineChart.getData().add(worldSeries);

                    return;
            }

            for (int i = 0; i < targetDateWeather.size(); i++) {
                    series.getData().add(new XYChart.Data<String, Number>(targetDateWeather.get(i).getCheckedDate().toString(),
                            (targetDateWeather.get(i).getMaxTemperature() - targetDateWeather.get(i).getMinTemperature()) / 2));
                }

            }
          catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add("All");
        comboBox.getItems().add("Yandex");
        comboBox.getItems().add("Rambler");
        comboBox.getItems().add("WorldWeather");
    }
}
