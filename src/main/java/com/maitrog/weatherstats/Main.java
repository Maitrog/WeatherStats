package com.maitrog.weatherstats;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class Main extends Application{

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/Main.fxml")));
        Parent authRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/AuthWindow.fxml")));
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
        stage.setMinHeight(350);
        stage.setMinWidth(400);
        stage.show();
        authStage.show();
    }
}