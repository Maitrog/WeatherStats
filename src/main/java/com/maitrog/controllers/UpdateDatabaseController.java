package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.models.*;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import io.github.palexdev.materialfx.controls.MFXProgressBar;

public class UpdateDatabaseController implements Initializable {
    @FXML
    private MFXProgressBar progressBar;

    @FXML
    private Text lastCheckDate;

    @FXML
    private Text lastCheckText;

    @FXML
    private MFXButton updateButton;

    private static int lastCheckedCityId = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Main.user.getRole() == Role.USER)
        {
            updateButton.setDisable(true);
        }
        localize();
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
                lastCheckText.setText(locale.get(10).getRu());
                updateButton.setText(locale.get(11).getRu());
                break;
            case "en":
                lastCheckText.setText(locale.get(10).getEn());
                updateButton.setText(locale.get(11).getEn());
                break;
            default:
                break;
        }
    }
}
