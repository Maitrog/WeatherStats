package com.maitrog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.models.DbWeather;
import com.maitrog.models.Locale;
import com.maitrog.models.SiteType;
import com.maitrog.models.Weather;
import com.maitrog.weatherstats.Main;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.jfree.data.general.Series;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

public class GraphicsController implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis checkDateText;

    @FXML
    private NumberAxis temperatureText;

    @FXML
    private MFXComboBox<String> comboBox;

    @FXML
    private MFXTextField textField;

    @FXML
    private MFXDatePicker datePicker;

    @FXML
    private MFXButton plotChartButton;

    @FXML
    private void plotMedian() {
        addMedian();
    }

    @FXML
    private void plotChart() {
        lineChart.getData().clear();
        addData();
        plotMedian();
    }

    private void fixRequest(List<Weather> weather) {
        while (weather.size() >= 20) weather.remove(0);
    }

    private void addMedian() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        List<Weather> weathers = new ArrayList<>();
        try {
            switch (comboBox.getSelectedValue()) {
                case "Rambler" -> {
                    weathers = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Rambler - Медиана");
                    //fixRequest(weathers);
                }
                case "Yandex" -> {
                    weathers = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Yandex - Медиана");
                    //fixRequest(weathers);
                }
                case "WorldWeather" -> {
                    weathers = DbWeather.getInstance().getWeathers(textField.getText(),
                            SiteType.WorldWeather, Date.valueOf(datePicker.getDate()));
                    series.setName("WorldWeather - Медиана");
                    //fixRequest(weathers);
                }
                case "All" -> {
                    List<Weather> ramblerDateWeather;
                    List<Weather> yandexDateWeather;
                    List<Weather> worldDateWeather;
                    XYChart.Series<String, Number> ramblerSeries = new XYChart.Series<>();
                    XYChart.Series<String, Number> yandexSeries = new XYChart.Series<>();
                    XYChart.Series<String, Number> worldSeries = new XYChart.Series<>();
                    ramblerDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    yandexDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    worldDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.WorldWeather,
                            Date.valueOf(datePicker.getDate()));
                    List<Double> parsedRambler = new ArrayList<>();
                    List<Double> parsedYandex = new ArrayList<>();
                    List<Double> parsedWorld = new ArrayList<>();
                    //fixRequest(ramblerDateWeather);
                    //fixRequest(yandexDateWeather);
                    //fixRequest(worldDateWeather);
                    double median;
                    for (Weather weather : ramblerDateWeather) {
                        double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                        temperature /= 2;
                        temperature += weather.getMinTemperature();
                        parsedRambler.add(temperature);
                    }
                    Collections.sort(parsedRambler);
                    if (parsedRambler.size() % 2 == 0) median = (parsedRambler.get(parsedRambler.size() / 2) + parsedRambler.get(parsedRambler.size() / 2 - 1)) / 2;
                    else median = parsedRambler.get(parsedRambler.size() / 2);
                    for (Weather weather : ramblerDateWeather) {
                        ramblerSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(), median));
                    }
                    lineChart.getData().add(ramblerSeries);
                    ramblerSeries.setName("Rambler - Медиана");
                    for (Weather weather : yandexDateWeather) {
                        double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                        temperature /= 2;
                        temperature += weather.getMinTemperature();
                        parsedYandex.add(temperature);
                    }
                    Collections.sort(parsedYandex);
                    if (parsedYandex.size() % 2 == 0) median = (parsedYandex.get(parsedYandex.size() / 2) + parsedYandex.get(parsedYandex.size() / 2 - 1)) / 2;
                    else median = parsedYandex.get(parsedYandex.size() / 2);
                    for (Weather weather : yandexDateWeather) {
                        yandexSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(), median));
                    }
                    lineChart.getData().add(yandexSeries);
                    yandexSeries.setName("Yandex - Медиана");
                    for (Weather weather : worldDateWeather) {
                        double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                        temperature /= 2;
                        temperature += weather.getMinTemperature();
                        parsedWorld.add(temperature);
                    }
                    Collections.sort(parsedWorld);
                    if (parsedWorld.size() % 2 == 0) median = (parsedWorld.get(parsedWorld.size() / 2) + parsedWorld.get(parsedWorld.size() / 2 - 1)) / 2;
                    else median = parsedWorld.get(parsedWorld.size() / 2);
                    for (Weather weather : worldDateWeather) {
                        worldSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(), median));
                    }
                    lineChart.getData().add(worldSeries);
                    worldSeries.setName("WorldWeather - Медиана");
                    return;
                }
            }
            List<Double> parsedWeather = new ArrayList<>();
            for (Weather weather : weathers) {
                double temperature = (weather.getMaxTemperature() - weather.getMinTemperature());
                temperature /= 2;
                temperature += weather.getMinTemperature();
                parsedWeather.add(temperature);
            }
            Collections.sort(parsedWeather);
            double median;
            if (parsedWeather.size() % 2 == 0) median = (parsedWeather.get(parsedWeather.size() / 2) + parsedWeather.get(parsedWeather.size() / 2 - 1)) / 2;
            else median = parsedWeather.get(parsedWeather.size() / 2);
            for (Weather weather : weathers) {
                series.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(), median));
            }
            lineChart.getData().add(series);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        List<Weather> targetDateWeather = new ArrayList<>();
        try {
            switch (comboBox.getSelectedValue()) {
                case "Rambler" -> {
                    targetDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Rambler");
                    //fixRequest(targetDateWeather);
                }
                case "Yandex" -> {
                    targetDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    series.setName("Yandex");
                    //fixRequest(targetDateWeather);
                }
                case "WorldWeather" -> {
                    targetDateWeather = DbWeather.getInstance().getWeathers(textField.getText(),
                            SiteType.WorldWeather, Date.valueOf(datePicker.getDate()));
                    series.setName("WorldWeather");
                    //fixRequest(targetDateWeather);
                }
                case "All", "Все" -> {
                    List<Weather> ramblerDateWeather;
                    List<Weather> yandexDateWeather;
                    List<Weather> worldDateWeather;
                    XYChart.Series<String, Number> ramblerSeries = new XYChart.Series<>();
                    XYChart.Series<String, Number> yandexSeries = new XYChart.Series<>();
                    XYChart.Series<String, Number> worldSeries = new XYChart.Series<>();
                    XYChart.Series<String, Number> allSeries = new XYChart.Series<>();
                    ramblerDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Rambler,
                            Date.valueOf(datePicker.getDate()));
                    yandexDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.Yandex,
                            Date.valueOf(datePicker.getDate()));
                    worldDateWeather = DbWeather.getInstance().getWeathers(textField.getText(), SiteType.WorldWeather,
                            Date.valueOf(datePicker.getDate()));
                    //fixRequest(ramblerDateWeather);
                    //fixRequest(yandexDateWeather);
                    //fixRequest(worldDateWeather);
                    for (Weather weather : ramblerDateWeather) {
                        ramblerSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
                    }
                    lineChart.getData().add(ramblerSeries);
                    ramblerSeries.setName("Rambler");
                    for (Weather weather : yandexDateWeather) {
                        yandexSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
                    }
                    lineChart.getData().add(yandexSeries);
                    yandexSeries.setName("Yandex");
                    for (Weather weather : worldDateWeather) {
                        worldSeries.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                                (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
                    }
                    //worldSeries.getData().sort(Comparator.comparing(d -> d.getXValue().toString()));
                    lineChart.getData().add(worldSeries);
                    worldSeries.setName("WorldWeather");
                    return;
                }
            }

            for (Weather weather : targetDateWeather) {
                series.getData().add(new XYChart.Data<>(weather.getCheckedDate().toString(),
                        (weather.getMaxTemperature() - weather.getMinTemperature()) / 2 + weather.getMinTemperature()));
            }
            lineChart.getData().add(series);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localize();
        if(Main.user.getLanguage().equals("en"))
        {
            comboBox.getItems().add("All");
            comboBox.getItems().add("Yandex");
            comboBox.getItems().add("Rambler");
            comboBox.getItems().add("WorldWeather");
        }
        else if (Main.user.getLanguage().equals("ru"))
        {
            comboBox.getItems().add("Все");
            comboBox.getItems().add("Yandex");
            comboBox.getItems().add("Rambler");
            comboBox.getItems().add("WorldWeather");
        }
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
                lineChart.setTitle(locale.get(5).getRu());
                checkDateText.setLabel(locale.get(19).getRu());
                temperatureText.setLabel(locale.get(18).getRu());
                comboBox.setPromptText(locale.get(17).getRu());
                plotChartButton.setText(locale.get(12).getRu());
                break;
            case "en":
                lineChart.setTitle(locale.get(5).getEn());
                checkDateText.setLabel(locale.get(19).getEn());
                temperatureText.setLabel(locale.get(18).getEn());
                comboBox.setPromptText(locale.get(17).getEn());
                plotChartButton.setText(locale.get(12).getEn());
                break;
            default:
                break;
        }
    }
}
