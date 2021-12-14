package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Locale;
import com.maitrog.models.Role;
import com.maitrog.models.User;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
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

    @FXML
    private Label loginText;

    @FXML
    private Label passwordText;

    @FXML
    private Label confirmText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localize();
        AbstractMFXValidator loginValidator = new AbstractMFXValidator() {
            @Override
            public void setValidatorMessage(String validatorMessage) {
                super.setValidatorMessage(validatorMessage);
            }

            @Override
            public boolean isValid() {
                if (login.getText().equals("") || login.getText().length() < 4) {
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

        authorize.setOnAction(actionEvent -> {
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

                }
                Platform.runLater(() -> {
                    if (authorized) {
                        Stage stage = (Stage) authorize.getScene().getWindow();
                        stage.hide();
                        Main.logger.log(Level.INFO, "Authorization hidden");
                    }
                });
            });
            Stage stage = (Stage) authorize.getScene().getWindow();
            stage.hide();
            authThread.setDaemon(true);
            authThread.start();
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
                    regStage.setOnCloseRequest(event -> tempMain.close());
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
                    regStage.setOnCloseRequest(event -> tempMain.close());
                    regStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        registerFinal.setOnAction(actionEvent -> {
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
                Platform.runLater(() -> {
                    if (registered) {
                        Stage stage = (Stage) authorize.getScene().getWindow();
                        stage.hide();
                        Main.logger.log(Level.INFO, "Stage hidden");
                    }
                });
            });
            regThread.setDaemon(true);
            regThread.start();
        });
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
                loginText.setText(locale.get(0).getRu());
                passwordText.setText(locale.get(1).getRu());
                authorize.setText(locale.get(2).getRu());
                register.setText(locale.get(3).getRu());
                registerFinal.setText(locale.get(3).getRu());
                backToLogin.setText(locale.get(29).getRu());
                confirmText.setText(locale.get(28).getRu());
                break;
            case "en":
                loginText.setText(locale.get(0).getEn());
                passwordText.setText(locale.get(1).getEn());
                authorize.setText(locale.get(2).getEn());
                register.setText(locale.get(3).getEn());
                registerFinal.setText(locale.get(3).getEn());
                backToLogin.setText(locale.get(29).getEn());
                confirmText.setText(locale.get(28).getEn());
                break;
            default:
                break;
        }
    }
}
