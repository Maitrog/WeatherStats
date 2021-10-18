package com.maitrog.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import org.jfree.chart.JFreeChart;

public class GraphicsController implements Initializable {

    @FXML
    private JFXComboBox comboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
