package com.maitrog.controllers;

import com.jfoenix.controls.JFXHamburger;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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
    private MFXButton updateButton;

    @FXML
    private MFXButton graphicsButton;

    @FXML
    private MFXButton siteAccuracy;

    @FXML
    private MFXButton settingsButton;

    @FXML
    private MFXButton graphicsErrorButton;

    @FXML
    private Text title;

    @FXML
    private AnchorPane mainScene;

    protected void openT() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            int i = 0;
            final Node anchorPane = mainScene.getChildren().get(0);

            @Override
            public void run() {
                if (i < 20 && gridPane.getWidth() < 350) {
                    gridPane.setPrefWidth(gridPane.getWidth() + 18);
                    AnchorPane.setLeftAnchor(anchorPane, gridPane.getWidth());
                } else {
                    this.cancel();
                }
                i++;
            }
        };

        graphicsButton.setAlignment(Pos.CENTER_LEFT);
        updateButton.setAlignment(Pos.CENTER_LEFT);
        settingsButton.setAlignment(Pos.CENTER_LEFT);
        siteAccuracy.setAlignment(Pos.CENTER_LEFT);
        graphicsErrorButton.setAlignment(Pos.CENTER_LEFT);
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
            final Node anchorPane = mainScene.getChildren().get(0);

            @Override
            public void run() {
                if (i < 20 && gridPane.getWidth() > 50) {
                    gridPane.setPrefWidth(gridPane.getWidth() - 18);
                    AnchorPane.setLeftAnchor(anchorPane, gridPane.getWidth());
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
        try {
            graphics(new ActionEvent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateDatabase(ActionEvent event) throws IOException {
        title.setText("Обновление данных");
        Parent parent = updateButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/UpdateDatabase.fxml");
    }

    public void graphics(ActionEvent event) throws IOException {
        title.setText("Прогноз погоды");
        Parent parent = graphicsButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/Graphics.fxml");
    }

    public void siteAccuracy(ActionEvent event) throws IOException {
        title.setText("Среднее отклонение прогноза погоды");
        Parent parent = siteAccuracy.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/SiteAccuracy.fxml");
    }
  
    public void graphicsError(ActionEvent event) throws IOException {
        title.setText("Temperature mistake charts");
        Parent parent = graphicsErrorButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/GraphicsError.fxml");
    }

    public void Settings(ActionEvent event) throws IOException {
        title.setText("Настройки");
        Parent parent = settingsButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/Settings.fxml");
    }

    private void loadScene(Pane pane, String s) throws IOException {
        var children = pane.getChildren();
        Main.logger.log(Level.INFO, "Scene start loading");
        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(s)));
        Main.logger.log(Level.INFO, "Scene was loaded");
        AnchorPane.setBottomAnchor(anchorPane, 0.0);
        AnchorPane.setLeftAnchor(anchorPane, isOpen ? 180.0 : 50.0);
        AnchorPane.setRightAnchor(anchorPane, 0.0);
        AnchorPane.setTopAnchor(anchorPane, 50.0);
        children.set(0, anchorPane);
    }
}
