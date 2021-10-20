package com.maitrog.weatherstats;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main extends Application{

    public static final Logger logger = Logger.getGlobal();
    private final FileHandler fileHandler = new FileHandler("InfoLog.log");

    public Main() throws IOException {
    }

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        for (Object propertyKeyName:System.getProperties().keySet()){
            logger.log(Level.INFO,propertyKeyName+" - "+System.getProperty(propertyKeyName.toString()));
        }

        logger.log(Level.INFO, "Start loading main window");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/Main.fxml")));
        logger.log(Level.INFO, "Main window was loaded");
        logger.log(Level.INFO, "Start loading authentication window");
        Parent authRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/AuthWindow.fxml")));
        logger.log(Level.INFO, "Authentication window was loaded");
        Parent regRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/RegWindow.fxml")));

        Scene scene = new Scene(root);
        Scene authWindow = new Scene(authRoot, 300, 400);
        Scene regWindow = new Scene(regRoot, 300, 400);

        Stage authStage = new Stage();
        authStage.setResizable(false);
        authStage.initModality(Modality.APPLICATION_MODAL);
        authStage.initOwner(stage);
        authStage.setScene(authWindow);
        authStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();
            }
        });


        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(650);
        stage.show();
        authStage.show();
    }
}