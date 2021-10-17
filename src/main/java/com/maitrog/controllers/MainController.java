package com.maitrog.controllers;

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


public class MainController implements Initializable {
    private Timer animTimer;
    private TimerTask task;
    private boolean isOpen = false;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXButton updateButton;

    @FXML
    private ProgressBar progressBar;

    protected void openT() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                if (i < 14 && gridPane.getWidth() < 250) {
                    gridPane.setPrefWidth(gridPane.getWidth() + 15);
                } else {
                    this.cancel();
                }
                i++;
            }
        };

        updateButton.setText("Обновить данные");
        updateButton.setTextAlignment(TextAlignment.LEFT);
        Thread open = new Thread(() -> {
            animTimer = new Timer();
            animTimer.schedule(task, 0, 16);
        });
        open.setDaemon(true);
        open.start();
    }

    protected void closeT() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                if (i < 14 && gridPane.getWidth() > 50) {
                    gridPane.setPrefWidth(gridPane.getWidth() - 15);
                } else {
                    this.cancel();
                }
                i++;
            }
        };

        Thread close = new Thread(() -> {
            animTimer = new Timer();
            animTimer.schedule(task, 0, 16);
        });
        close.setDaemon(true);
        close.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) ->
        {
            if (animTimer != null) {
                animTimer.cancel();
            }
            if (!isOpen) {
                isOpen = true;
                openT();
            } else {
                isOpen = false;
                closeT();
            }
        });

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/refresh.png")));

        updateButton.setGraphic(new ImageView(image));
    }

    public void updateDatabase(ActionEvent event) {
        Thread update = new Thread(() -> {
            try {
                progressBar.setProgress(0);
                List<City> cities = DbWeather.getInstance().getAllCities();
                for (City city :
                        cities) {
                    List<Weather> weathers = Parser.parseYandexWeather(city.getUrlYandex());
                    weathers.addAll(Parser.parseRambler(city.getUrlRambler()));
                    weathers.addAll(Parser.parseWorldWeather(city.getUrlWorldWeather()));

                    for (Weather weather : weathers) {
                        weather.setCityId(city.getId());
                        DbWeather.getInstance().addWeather(weather);
                    }

                    progressBar.setProgress(progressBar.getProgress() + 1.0 / cities.size());
                }
            } catch (SQLException | ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });
        update.setDaemon(true);
        update.start();
    }
}
