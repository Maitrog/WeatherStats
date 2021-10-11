package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


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
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) ->
        {
            if(animTimer!=null){
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
        ImageView imageView = new ImageView(image);

        updateButton.setGraphic(new ImageView(image));
    }
}
