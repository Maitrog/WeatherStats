package com.maitrog.controllers;

import com.maitrog.models.*;
import com.maitrog.weatherstats.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import io.github.palexdev.materialfx.controls.MFXProgressBar;

public class UpdateDatabaseController implements Initializable {
    @FXML
    private MFXProgressBar progressBar;

    @FXML
    private Text lastCheckDate;

    private static int lastCheckedCityId = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void updateDatabase(ActionEvent event) {
        Thread update = new Thread();
        if(Main.user.getRole() == Role.ADMIN)
        {
            Main.logger.log(Level.INFO, "Start update database");
            update = new Thread(() -> {
                try {
                    progressBar.setProgress(0);
                    List<City> cities = DbWeather.getInstance().getAllCities();
                    double step = 1.0 / cities.size();
                    for (City city :
                            cities) {
                        if (city.getId() > lastCheckedCityId) {
                            List<Weather> weathers = Parser.parseYandexWeather(city.getUrlYandex());
                            weathers.addAll(Parser.parseRambler(city.getUrlRambler()));
                            weathers.addAll(Parser.parseWorldWeather(city.getUrlWorldWeather()));

                            for (Weather weather : weathers) {
                                weather.setCityId(city.getId());
                                DbWeather.getInstance().addWeather(weather);
                            }
                            lastCheckedCityId = city.getId();
                        }
                        progressBar.setProgress(progressBar.getProgress() + step);
                    }
                    lastCheckedCityId = 0;
                    Main.logger.log(Level.INFO, "Database was updated");
                } catch (SQLException | ClassNotFoundException | IOException e) {
                    Main.logger.log(Level.SEVERE, "Update database was crashed. Exception: ", e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        update.setDaemon(true);
        update.start();
    }
}
