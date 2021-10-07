package com.maitrog.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbWeather {
    public String CON_STR;

    private static DbWeather instance = null;
    private final Connection connection;

    public static synchronized DbWeather getInstance() throws SQLException, ClassNotFoundException, IOException {
        if (instance == null) {
            instance = new DbWeather();
        }
        return instance;
    }

    private DbWeather() throws SQLException, ClassNotFoundException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ConfigDb configDb = mapper.readValue("config.json", ConfigDb.class);
        CON_STR = configDb.toString();
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(CON_STR);
    }

    public List<City> getAllCities() {
        try (Statement statement = connection.createStatement()) {
            List<City> cities = new ArrayList<>();
            ResultSet citiesResult = statement.executeQuery("SELECT * FROM Cities");
            while (citiesResult.next()) {
                cities.add(new CityBuilder().id(citiesResult.getInt("Id"))
                        .nameRu(citiesResult.getString("NameRu"))
                        .nameEn(citiesResult.getString("NameEn"))
                        .urlYandex(citiesResult.getString("UrlYandex"))
                        .urlRambler(citiesResult.getString("UrlRambler"))
                        .urlWorldWeather(citiesResult.getString("UrlWorldWeather"))
                        .country(citiesResult.getString("Country"))
                        .buildCity());
            }
            return cities;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<City> getAllCitiesWithWeather() {
        try (Statement statement = connection.createStatement()) {
            List<City> cities = new ArrayList<>();
            ResultSet citiesResult = statement.executeQuery("SELECT * FROM Cities");
            while (citiesResult.next()) {
                int cityId = citiesResult.getInt("Id");
                List<Weather> yandexWeathers = getAllWeathers("YandexWeather", cityId);
                List<Weather> ramblerWeathers = getAllWeathers("RamblerWeather", cityId);
                List<Weather> worldWeathers = getAllWeathers("WorldWeather", cityId);
                cities.add(new CityBuilder().id(cityId)
                        .nameRu(citiesResult.getString("NameRu"))
                        .nameEn(citiesResult.getString("NameEn"))
                        .urlYandex(citiesResult.getString("UrlYandex"))
                        .urlRambler(citiesResult.getString("UrlRambler"))
                        .urlWorldWeather(citiesResult.getString("UrlWorldWeather"))
                        .country(citiesResult.getString("Country"))
                        .yandexWeathers(yandexWeathers)
                        .ramblerWeather(ramblerWeathers)
                        .worldWeather(worldWeathers)
                        .buildCity());
            }
            return cities;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getAllWeathers(String tableName) {
        try (Statement statement = connection.createStatement()) {
            List<Weather> weathers = new ArrayList<>();
            ResultSet weatherResult = statement.executeQuery(String.format("SELECT * FROM %s", tableName));
            while (weatherResult.next()) {
                weathers.add(new WeatherBuilder()
                        .id(weatherResult.getInt("Id"))
                        .checkedDate(weatherResult.getDate("CheckedDate"))
                        .targetDate(weatherResult.getDate("TargetDate"))
                        .minTemperature(weatherResult.getInt("MinTemperature"))
                        .maxTemperature(weatherResult.getInt("MaxTemperature"))
                        .pressure(weatherResult.getInt("Pressure"))
                        .humidity(weatherResult.getInt("Humidity"))
                        .cityId(weatherResult.getInt("CityId"))
                        .buildWeather());
            }
            return weathers;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getAllWeathers(String tableName, int cityId) {
        try (Statement statement = connection.createStatement()) {
            List<Weather> weathers = new ArrayList<>();
            ResultSet weatherResult = statement.executeQuery(String.format("SELECT * FROM %s WHERE CityId = %d", tableName, cityId));
            while (weatherResult.next()) {
                weathers.add(new WeatherBuilder()
                        .id(weatherResult.getInt("Id"))
                        .checkedDate(weatherResult.getDate("CheckedDate"))
                        .targetDate(weatherResult.getDate("TargetDate"))
                        .minTemperature(weatherResult.getInt("MinTemperature"))
                        .maxTemperature(weatherResult.getInt("MaxTemperature"))
                        .pressure(weatherResult.getInt("Pressure"))
                        .humidity(weatherResult.getInt("Humidity"))
                        .cityId(weatherResult.getInt("CityId"))
                        .buildWeather());
            }
            return weathers;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addCity(City city) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Cities(NameRu, NameEn, UrlYandex, UrlRambler, UrlWorldWeather, Country) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, city.getNameRu());
            statement.setObject(2, city.getNameEn());
            statement.setObject(3, city.getUrlYandex());
            statement.setObject(4, city.getUrlRambler());
            statement.setObject(5, city.getUrlWorldWeather());
            statement.setObject(6, city.getCountry());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addWeather(Weather weather) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Weather(Site, CheckedDate, TargetDate, MinTemperature, MaxTemperature, Pressure, Humidity, CityId) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, weather.getCheckedDate());
            statement.setObject(2, weather.getTargetDate());
            statement.setObject(3, weather.getMinTemperature());
            statement.setObject(4, weather.getMaxTemperature());
            statement.setObject(5, weather.getPressure());
            statement.setObject(6, weather.getHumidity());
            statement.setObject(7, weather.getCityId());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

