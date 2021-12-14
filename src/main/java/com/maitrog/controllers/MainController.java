package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXHamburger;
import com.maitrog.models.Locale;
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

import java.io.File;
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
            localize();
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
        ObjectMapper mapper = new ObjectMapper();
        List<com.maitrog.models.Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Main.user.getLanguage().equals("ru"))
            title.setText(locale.get(8).getRu());
        else
            title.setText(locale.get(8).getEn());
        Parent parent = updateButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/UpdateDatabase.fxml");
    }

    public void graphics(ActionEvent event) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<com.maitrog.models.Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Main.user.getLanguage().equals("ru"))
            title.setText(locale.get(5).getRu());
        else
            title.setText(locale.get(5).getEn());
        Parent parent = graphicsButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/Graphics.fxml");
    }

    public void siteAccuracy(ActionEvent event) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<com.maitrog.models.Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Main.user.getLanguage().equals("ru"))
            title.setText(locale.get(7).getRu());
        else
            title.setText(locale.get(7).getEn());
        Parent parent = siteAccuracy.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/SiteAccuracy.fxml");
    }
  
    public void graphicsError(ActionEvent event) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<com.maitrog.models.Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Main.user.getLanguage().equals("ru"))
            title.setText(locale.get(20).getRu());
        else
            title.setText(locale.get(20).getEn());
        Parent parent = graphicsErrorButton.getParent().getParent();
        loadScene((Pane) parent, "/com/maitrog/views/GraphicsError.fxml");
    }

    public void Settings(ActionEvent event) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<com.maitrog.models.Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Main.user.getLanguage().equals("ru"))
            title.setText(locale.get(9).getRu());
        else
            title.setText(locale.get(9).getEn());
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

    public void localize()
    {
        ObjectMapper mapper = new ObjectMapper();
        List<Locale> locale = null;
        try {
            locale = mapper.readValue(new File("src/main/resources/locale.json"), new TypeReference<List<Locale>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(Main.user.getLanguage())
        {
            case "ru":
                updateButton.setText(locale.get(8).getRu());
                graphicsButton.setText(locale.get(5).getRu());
                settingsButton.setText(locale.get(9).getRu());
                siteAccuracy.setText(locale.get(7).getRu());
                graphicsErrorButton.setText(locale.get(20).getRu());
                break;
            case "en":
                updateButton.setText(locale.get(8).getEn());
                graphicsButton.setText(locale.get(5).getEn());
                settingsButton.setText(locale.get(9).getEn());
                siteAccuracy.setText(locale.get(7).getEn());
                graphicsErrorButton.setText(locale.get(20).getEn());
                break;
            default:
                break;
        }
    }
}
