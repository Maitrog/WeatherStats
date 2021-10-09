package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainController implements Initializable {
    private Timer animTimer;
    private TimerTask task;
    private boolean isOpen = false;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private VBox vBox;

    protected void openT() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                if (i < 14 && vBox.getWidth() < 250) {
                    vBox.setPrefWidth(vBox.getWidth() + 15);
                } else {
                    this.cancel();
                }
                i++;
            }
        };
        animTimer = new Timer();
        animTimer.schedule(task, 0, 16);
    }

    protected void closeT() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i < 14 && vBox.getWidth() > 50) {
                    vBox.setPrefWidth(vBox.getWidth() - 15);
                } else {
                    this.cancel();
                }
                i++;
            }
        };
        animTimer = new Timer();
        animTimer.schedule(task, 0, 16);
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
    }
}
