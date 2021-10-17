package com.maitrog.weatherstats;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application{

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/maitrog/views/Main.fxml")));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(400);
        stage.show();
    }
}