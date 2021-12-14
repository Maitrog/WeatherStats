package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import com.maitrog.models.*;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.*;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;

public class SettingsController implements Initializable {
    public String CON_STR;

    @FXML
    private MFXLabel ipText;

    @FXML
    private MFXLabel nameDbText;

    @FXML
    private MFXLabel nameText;

    @FXML
    private MFXLabel passwordText;

    @FXML
    private MFXLabel languageText;

    @FXML
    private MFXTextField ipDB;

    @FXML
    private MFXTextField nameDB;

    @FXML
    private MFXTextField userName;

    @FXML
    private MFXTextField userPassword;

    @FXML
    private MFXComboBox comboBox;

    @FXML
    private MFXButton saveConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localize();
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
        if(Main.user.getLanguage().equals("ru"))
        {
            comboBox.getItems().add("Русский");
            comboBox.getItems().add("Английский");
        }
        else if(Main.user.getLanguage().equals("en"))
        {
            comboBox.getItems().add("Russian");
            comboBox.getItems().add("English");
        }
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
        if(comboBox.getSelectedValue().toString().equals("Русский") || comboBox.getSelectedValue().toString().equals("Russian"))
            Main.user.setLanguage("ru");
        else if (comboBox.getSelectedValue().toString().equals("Английский") || comboBox.getSelectedValue().toString().equals("English"))
            Main.user.setLanguage("en");
        configDb.language = Main.user.getLanguage();
        mapper.writeValue(new File("src/main/resources/config.json"), configDb);
        Parent parent = saveConfig.getParent().getParent();
        refresh((Pane) parent, "/com/maitrog/views/Settings.fxml");
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
                ipText.setText(locale.get(13).getRu());
                nameDbText.setText(locale.get(14).getRu());
                nameText.setText(locale.get(15).getRu());
                passwordText.setText(locale.get(1).getRu());
                languageText.setText(locale.get(27).getRu());
                comboBox.setPromptText(locale.get(17).getRu());
                saveConfig.setText(locale.get(16).getRu());
                break;
            case "en":
                ipText.setText(locale.get(13).getEn());
                nameDbText.setText(locale.get(14).getEn());
                nameText.setText(locale.get(15).getEn());
                passwordText.setText(locale.get(1).getEn());
                languageText.setText(locale.get(27).getEn());
                comboBox.setPromptText(locale.get(17).getEn());
                saveConfig.setText(locale.get(16).getEn());
                break;
            default:
                break;
        }
    }

    private void refresh(Pane pane, String s) throws IOException {
        var children = pane.getChildren();
        Main.logger.log(Level.INFO, "Scene start loading");
        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(s)));
        Main.logger.log(Level.INFO, "Scene was loaded");
        AnchorPane.setBottomAnchor(anchorPane, 0.0);
        AnchorPane.setLeftAnchor(anchorPane, 50.0);
        AnchorPane.setRightAnchor(anchorPane, 0.0);
        AnchorPane.setTopAnchor(anchorPane, 50.0);
        children.set(0, anchorPane);
    }
}
