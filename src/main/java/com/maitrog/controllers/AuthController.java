package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import com.maitrog.models.Role;
import com.maitrog.models.User;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import io.github.palexdev.materialfx.validation.base.Validated;
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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class AuthController implements Initializable {
    private String tempLogin;
    private String tempPassword;

    @FXML
    private MFXTextField login;

    @FXML
    private MFXPasswordField password;

    @FXML
    private MFXPasswordField confirmPassword;

    @FXML
    private MFXButton authorize;

    @FXML
    private MFXButton register;

    @FXML
    private MFXButton registerFinal;

    @FXML
    private MFXButton backToLogin;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AbstractMFXValidator loginValidator = new AbstractMFXValidator() {
            @Override
            public void setValidatorMessage(String validatorMessage) {
                super.setValidatorMessage(validatorMessage);
            }

            @Override
            public boolean isValid() {
                if(login.getText().equals(""))
                {
                    /*
                    *   TODO:
                    *    здесь прописывается проверка с бд логина
                     */
                    return !super.isValid();
                }
                else
                {
                    return super.isValid();
                }
            }
        };
        AbstractMFXValidator passwordValidator = new AbstractMFXValidator() {
            @Override
            public void setValidatorMessage(String validatorMessage) {
                super.setValidatorMessage(validatorMessage);
            }

            @Override
            public boolean isValid() {
                if(password.getText().equals(""))
                {
                    return !super.isValid();
                }
                else
                {
                    return super.isValid();
                }
            }
        };
        AbstractMFXValidator confirmValidator = new AbstractMFXValidator() {
            @Override
            public void setValidatorMessage(String validatorMessage) {
                super.setValidatorMessage(validatorMessage);
            }

            @Override
            public boolean isValid() {
                if(confirmPassword.getText().equals(password.getText()))
                {
                    return super.isValid();
                }
                else
                {
                    return !super.isValid();
                }
            }
        };
        loginValidator.setValidatorMessage("Введите логин");
        passwordValidator.setValidatorMessage("Введите пароль");
        confirmValidator.setValidatorMessage("Пароли не совпадают");

        authorize.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tempLogin = login.getText();
                tempPassword = password.getText();
                /*
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(tempPassword.getBytes(StandardCharsets.UTF_8));
                 */
                /*
                 *TODO:
                 * проверка данных пользователя с базой данных
                 */
                if(loginValidator.isValid() && passwordValidator.isValid())
                {
                    if(tempLogin.equals("admin") && tempPassword.equals("1234"))
                    {
                        User user = new User(tempLogin, tempPassword, Role.ADMIN);
                        Main.user = user;
                        Stage stage = (Stage) authorize.getScene().getWindow();
                        stage.hide();
                    }
                    else
                    {
                        User user = new User(tempLogin, tempPassword, Role.USER);
                        Main.user = user;
                        Stage stage = (Stage) authorize.getScene().getWindow();
                        stage.hide();
                    }
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

        backToLogin.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader regWindow = new FXMLLoader(getClass().getResource("/com/maitrog/views/AuthWindow.fxml"));
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
                tempPassword = password.getText();
                /*
                 *TODO:
                 * ввод пользователя в базу данных
                 */
                if(loginValidator.isValid() && passwordValidator.isValid() && confirmValidator.isValid())
                {
                    if(tempLogin.equals("admin") && tempPassword.equals("1234"))
                    {
                        User user = new User(tempLogin, tempPassword, Role.ADMIN);
                        Main.user = user;
                        Stage stage = (Stage) registerFinal.getScene().getWindow();
                        stage.hide();
                    }
                    else
                    {
                        User user = new User(tempLogin, tempPassword, Role.USER);
                        Main.user = user;
                        Stage stage = (Stage) authorize.getScene().getWindow();
                        stage.hide();
                    }
                }
            }
        });
    }
}
