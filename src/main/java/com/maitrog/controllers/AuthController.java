package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    private String tempLogin;
    private int tempPassword;

    @FXML
    private JFXTextArea login;

    @FXML
    private JFXTextArea password;

    @FXML
    private JFXTextArea confirmPassword;

    @FXML
    private JFXButton authorize;

    @FXML
    private JFXButton register;

    @FXML
    private JFXButton registerFinal;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RequiredFieldValidator validatorLogin = new RequiredFieldValidator();
        RequiredFieldValidator validatorPassword = new RequiredFieldValidator();
        RequiredFieldValidator validatorConfirm = new RequiredFieldValidator();
        login.getValidators().add(validatorLogin);
        password.getValidators().add(validatorPassword);
        confirmPassword.getValidators().add(validatorConfirm);
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
        confirmPassword.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                confirmPassword.validate();
            }
        });


        authorize.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tempLogin = login.getText();
                tempPassword = password.getText().hashCode();
                /*
                 *TODO:
                 * проверка данных пользователя с базой данных
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
                try {
                    FXMLLoader regWindow = new FXMLLoader(getClass().getResource("/com/maitrog/views/RegWindow.fxml"));
                    Parent root1 = regWindow.load();
                    Stage tempClose = (Stage)register.getScene().getWindow();
                    Stage tempMain = (Stage) tempClose.getOwner();
                    tempClose.close();
                    Stage regStage = new Stage();
                    regStage.setScene(new Scene(root1));
                    regStage.setResizable(false);
                    regStage.initModality(Modality.APPLICATION_MODAL);
                    regStage.initOwner(tempClose.getOwner());
                    regStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            tempMain.close();
                        }
                    });
                    regStage.show();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        registerFinal.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tempLogin = login.getText();
                tempPassword = password.getText().hashCode();
                /*
                 *TODO:
                 * ввод пользователя в базу данных
                 */
                if(true)
                {
                    Stage stage = (Stage) registerFinal.getScene().getWindow();
                    stage.hide();
                }
            }
        });
    }
}
