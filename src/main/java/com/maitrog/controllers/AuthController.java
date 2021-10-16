package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    private String temp_login;
    private int temp_password;

    @FXML
    private JFXTextArea login;

    @FXML
    private JFXTextArea password;

    @FXML
    private JFXButton authorize;

    @FXML
    private JFXButton register;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RequiredFieldValidator validatorLogin = new RequiredFieldValidator();
        RequiredFieldValidator validatorPassword = new RequiredFieldValidator();
        validatorLogin.setMessage("Неверный логин");
        validatorPassword.setMessage("Неверный пароль");
        login.getValidators().add(validatorLogin);
        password.getValidators().add(validatorPassword);
        login.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                login.validate();
            }
        });
        password.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                password.validate();
            }
        });



        authorize.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                temp_login = login.getText();
                temp_password = password.getText().hashCode();
                /*
                 *TODO:
                 * проверка текста с базой данных
                 */
                if(true)
                {
                    Stage stage = (Stage) authorize.getScene().getWindow();
                    stage.hide();
                }
            }
        });

        register.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                temp_login = login.getText();
                temp_password = password.getText().hashCode();
                /*
                 * TODO:
                 *  добавление пользователя в базу данных
                 */
                if(true)
                {
                    Stage stage = (Stage) register.getScene().getWindow();
                    stage.hide();
                }
            }
        });
    }
}
