package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.maitrog.weatherstats.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import org.jfree.chart.ui.Align;

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

    @FXML
    private JFXButton graphicsButton;

    @FXML
    private JFXButton settingsButton;

    protected void openT() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                if (i < 20 && gridPane.getWidth() < 350) {
                    gridPane.setPrefWidth(gridPane.getWidth() + 18);
                } else {
                    this.cancel();
                }
                i++;
            }
        };

        graphicsButton.setAlignment(Pos.CENTER_LEFT);
        updateButton.setAlignment(Pos.CENTER_LEFT);
        settingsButton.setAlignment(Pos.CENTER_LEFT);
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
                if (i < 20 && gridPane.getWidth() > 50) {
                    gridPane.setPrefWidth(gridPane.getWidth() - 18);
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
        Main.logger.log(Level.INFO, " Main window was initialized");
    }

    public void updateDatabase(ActionEvent event) throws IOException {
        Parent parent = updateButton.getParent().getParent();
        Pane pane = (Pane) parent;

        var children = pane.getChildren();

        if (children.size() == 3){
            Main.logger.log(Level.INFO, "Content was detected in main window");
            children.remove(0);
            Main.logger.log(Level.INFO, "Content was deleted in main window");
        }
        if (children.size() < 3) {
            Main.logger.log(Level.INFO, "Update database window start loading");
            AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/UpdateDatabase.fxml")));
            Main.logger.log(Level.INFO, "Update database window was loaded");
            AnchorPane.setBottomAnchor(anchorPane, 0.0);
            AnchorPane.setLeftAnchor(anchorPane, 50.0);
            AnchorPane.setRightAnchor(anchorPane, 0.0);
            AnchorPane.setTopAnchor(anchorPane, 50.0);

            children.add(anchorPane);
            anchorPane.toBack();
            anchorPane.toBack();
        }
    }

    public  void graphics(ActionEvent event) throws IOException {
        Parent parent = updateButton.getParent().getParent();
        Pane pane = (Pane) parent;

        var children = pane.getChildren();
        if (children.size() == 3){
            Main.logger.log(Level.INFO, "Content was detected in main window");
            children.remove(0);
            Main.logger.log(Level.INFO, "Content was deleted in main window");
        }
        if (children.size() < 3) {
            Main.logger.log(Level.INFO, "Update database window start loading");
            AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/Graphics.fxml")));
            Main.logger.log(Level.INFO, "Update database window was loaded");
            AnchorPane.setBottomAnchor(anchorPane, 0.0);
            AnchorPane.setLeftAnchor(anchorPane, 50.0);
            AnchorPane.setRightAnchor(anchorPane, 0.0);
            AnchorPane.setTopAnchor(anchorPane, 50.0);

            children.add(anchorPane);
            anchorPane.toBack();
            anchorPane.toBack();
        }
    }
}
