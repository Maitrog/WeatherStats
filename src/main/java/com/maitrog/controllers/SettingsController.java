package com.maitrog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import com.maitrog.models.ConfigDb;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Role;
import com.maitrog.models.User;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import io.github.palexdev.materialfx.validation.base.Validated;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;

public class SettingsController implements Initializable {
    public String CON_STR;

    @FXML
    private MFXTextField ipDB;

    @FXML
    private MFXTextField nameDB;

    @FXML
    private MFXTextField userName;

    @FXML
    private MFXTextField userPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObjectMapper mapper = new ObjectMapper();
        ConfigDb configDb = null;
        try {
            configDb = mapper.readValue(new File("src/main/resources/config.json"), ConfigDb.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ipDB.setText(configDb.server);
        nameDB.setText(configDb.database);
        userName.setText(configDb.user);
        userPassword.setText(configDb.password);
    }

    public void saveConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ConfigDb configDb = null;
        try {
            configDb = mapper.readValue(new File("src/main/resources/config.json"), ConfigDb.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        configDb.server = ipDB.getText();
        configDb.database = nameDB.getText();
        configDb.user = userName.getText();
        configDb.password = userPassword.getText();
        mapper.writeValue(new File("src/main/resources/config.json"), configDb);
    }
}
