package com.maitrog.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXHamburger;
import javafx.event.EventHandler;
import javafx.fxml.*;

import java.io.Console;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.maitrog.models.City;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Parser;
import com.maitrog.models.Weather;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private void handleButtonAction(ActionEvent e) {
        lineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series();
        series.getData().add(new XYChart.Data<String, Number>("1", 2));
        series.getData().add(new XYChart.Data<String, Number>("2", 1));
        series.getData().add(new XYChart.Data<String, Number>("3", 4));

        series.setName(comboBox.getValue() + ": " + textField.getText());
        lineChart.getData().add(series);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add("Yandex");
        comboBox.getItems().add("Rambler");
        comboBox.getItems().add("WorldWeather");
    }
}
