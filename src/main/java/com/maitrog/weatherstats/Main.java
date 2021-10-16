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
        Parent auth_root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/AuthWindow.fxml")));

        Scene scene = new Scene(root);
        Scene auth_window = new Scene(auth_root, 300, 400);

        Stage auth_stage = new Stage();
        auth_stage.initModality(Modality.APPLICATION_MODAL);
        auth_stage.initOwner(stage);
        auth_stage.setScene(auth_window);
        auth_stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();
            }
        });


        stage.setScene(scene);


        stage.setMinHeight(350);
        stage.setMinWidth(400);

        stage.show();
        auth_stage.show();
    }
}