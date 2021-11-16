package com.maitrog.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;

public class AuthController implements Initializable {
    private String tempLogin;
    private String tempPassword;
    private boolean authorized = false;
    private boolean registered = false;

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
                if (login.getText().equals("") || login.getText().length() < 4) {
                    /*
                     *   TODO:
                     *    здесь прописывается проверка с бд логина
                     */
                    return !super.isValid();
                } else {
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
                if (password.getPassword().equals("") || password.getPassword().length() < 4) {
                    return !super.isValid();
                } else {
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
                if (confirmPassword.getPassword().equals(password.getPassword())) {
                    return super.isValid();
                } else {
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
                tempPassword = password.getPassword();
                Main.logger.log(Level.INFO, "Button worked");
                Thread authThread = new Thread(() -> {
                    if (loginValidator.isValid() && passwordValidator.isValid()) {
                        try {
                            DbWeather db = DbWeather.getInstance();
                            User dbUser = db.getUser(tempLogin);
                            if (dbUser.getPasswordHash().equals(DigestUtils.sha256Hex(tempPassword))) {
                                Main.user = dbUser;
                                authorized = true;
                            } else {
                                authorized = false;
                                throw new IOException("Неверный пароль");
                            }
                            Main.logger.log(Level.INFO, "Authorized");
                        } catch (SQLException | ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                            passwordValidator.setValidatorMessage("Неправильный пароль");
                        }

                        Main.logger.log(Level.INFO, "Authorization hidden");
                    }
                });
                authThread.start();
                try {
                    authThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(authorized){
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
                    Stage tempClose = (Stage) register.getScene().getWindow();
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
                } catch (Exception e) {
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
                    Stage tempClose = (Stage) register.getScene().getWindow();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        registerFinal.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tempLogin = login.getText();
                tempPassword = password.getPassword();
                Main.logger.log(Level.INFO, "Button worked");
                Thread regThread = new Thread(() -> {
                    if (loginValidator.isValid() && passwordValidator.isValid() && confirmValidator.isValid()) {
                        try {
                            DbWeather db = DbWeather.getInstance();
                            db.addUser(new User(tempLogin, DigestUtils.sha256Hex(tempPassword), Role.USER));
                            registered = true;
                            Main.logger.log(Level.INFO, "Added user to DB");
                        } catch (SQLException | ClassNotFoundException | IOException e) {
                            registered = false;
                            e.printStackTrace();
                        }
                        Main.logger.log(Level.INFO, "Registered");
                    }
                });
                regThread.start();
                try {
                    regThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(registered){
                    Stage stage = (Stage) authorize.getScene().getWindow();
                    stage.hide();
                    Main.logger.log(Level.INFO, "Stage hidden");
                }
            }
        });
    }
}
