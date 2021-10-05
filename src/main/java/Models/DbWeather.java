package Models;

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
                ResultSet weatherResult = statement.executeQuery(String.format("SELECT * FROM Weather WHERE CityId=%d", cityId));
                List<Weather> weathers = new ArrayList<>();
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
                cities.add(new CityBuilder().id(cityId)
                        .nameRu(citiesResult.getString("NameRu"))
                        .nameEn(citiesResult.getString("NameEn"))
                        .urlYandex(citiesResult.getString("UrlYandex"))
                        .urlRambler(citiesResult.getString("UrlRambler"))
                        .urlWorldWeather(citiesResult.getString("UrlWorldWeather"))
                        .country(citiesResult.getString("Country"))
                        .weathers(weathers)
                        .buildCity());
            }
            return cities;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getAllWeathers() {
        try (Statement statement = connection.createStatement()) {
            List<Weather> weathers = new ArrayList<>();
            ResultSet weatherResult = statement.executeQuery("SELECT * FROM Weather");
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

    public void addCity(City _city) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Cities(NameRu, NameEn, UrlYandex, UrlRambler, UrlWorldWeather, Country) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, _city.getNameRu());
            statement.setObject(2, _city.getNameEn());
            statement.setObject(3, _city.getUrlYandex());
            statement.setObject(4, _city.getUrlRambler());
            statement.setObject(5, _city.getUrlWorldWeather());
            statement.setObject(6, _city.getCountry());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addWeather(Weather _weather) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Weather(Site, CheckedDate, TargetDate, MinTemperature, MaxTemperature, Pressure, Humidity, CityId)" +
                        "VALUE(?, ?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, _weather.getCheckedDate());
            statement.setObject(2, _weather.getTargetDate());
            statement.setObject(3, _weather.getMinTemperature());
            statement.setObject(4, _weather.getMaxTemperature());
            statement.setObject(5, _weather.getPressure());
            statement.setObject(6, _weather.getHumidity());
            statement.setObject(7, _weather.getCityId());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

