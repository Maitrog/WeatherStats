package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.maitrog.models.City;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Parser;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;


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
        Main.logger.log(Level.INFO, "Start initializing main window");
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
        Main.logger.log(Level.INFO, " Main window was initialized");
    }

    public void updateDatabase(ActionEvent event) throws IOException {
        Parent parent = updateButton.getParent().getParent();
        Pane pane = (Pane) parent;

        var children = pane.getChildren();

        if (children.size() == 3){
            Main.logger.log(Level.INFO, "Content was detected in main window");
            children.remove(children.size() - 1);
            Main.logger.log(Level.INFO, "Content was deleted in main window");
        }
        if (children.size() < 3) {
            Main.logger.log(Level.INFO, "Update database window start loading");
            AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/UpdateDatabase.fxml")));
            Main.logger.log(Level.INFO, "Update database window was loaded");
            AnchorPane.setBottomAnchor(anchorPane, 0.0);
            AnchorPane.setLeftAnchor(anchorPane, 50.0);
            AnchorPane.setRightAnchor(anchorPane, 0.0);
            AnchorPane.setTopAnchor(anchorPane, 0.0);

            children.add(anchorPane);
        }
    }
}
