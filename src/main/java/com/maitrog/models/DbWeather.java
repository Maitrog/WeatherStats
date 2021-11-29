package com.maitrog.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.weatherstats.Main;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

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
        ConfigDb configDb = mapper.readValue(new File("src/main/resources/config.json"), ConfigDb.class);
        CON_STR = configDb.toString();
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(CON_STR, configDb.user, configDb.password);
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
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<City> getAllCitiesWithWeather() {
        try (Statement statement = connection.createStatement()) {
            List<City> cities = getAllCities();
            for (City city :
                    cities) {
                List<Weather> weathers = getWeathers(city.getId());
                city.addWeather(weathers);
            }
            return cities;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getAllWeathers() {
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery("SELECT * FROM Weather");
            return parseWeatherResponse(weatherResult);
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getWeathers(int cityId) {
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery(String.format("SELECT * FROM Weather WHERE CityId = %d ORDER BY(CheckedDate)", cityId));
            return parseWeatherResponse(weatherResult);
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getWeathers(int cityId, SiteType siteType) {
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery(String.format("SELECT * FROM Weather WHERE CityId = %d AND SiteType = %d ORDER BY(CheckedDate)", cityId, SiteType.getValue(siteType)));
            return parseWeatherResponse(weatherResult);
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Weather> getWeathers(String name, SiteType siteType, Date targetDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery(String.format("SELECT Weather.Id, CheckedDate, TargetDate, MinTemperature, MaxTemperature, Pressure, Humidity, SiteType, CityId " +
                            "FROM Weather " +
                            "JOIN Cities ON Cities.Id = Weather.CityId " +
                            "WHERE (Cities.NameRu = '%s' OR Cities.NameEn = '%s') AND SiteType = %d AND TargetDate = '%s'" +
                            "ORDER BY(CheckedDate)",
                    name, name, SiteType.getValue(siteType), sdf.format(targetDate)));
            return parseWeatherResponse(weatherResult);
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }public List<List<Weather>> getSortedWeathers(int cityId, SiteType siteType) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery(String.format("""
                            SELECT *
                            FROM Weather
                            WHERE CityId=%d AND SiteType = %d AND TargetDate IN (SELECT DISTINCT TargetDate
                            FROM Weather
                            WHERE CityId = %d AND TargetDate = CheckedDate)
                            ORDER BY TargetDate, CheckedDate""",
                    cityId, SiteType.getValue(siteType), cityId));
            List<Weather> parsedWeather = parseWeatherResponse(weatherResult);
            List<List<Weather>> sortedWeather = new ArrayList<>();
            Date currentDate = parsedWeather.get(0).getTargetDate();
            List<Weather> weathers = new ArrayList<>();
            for(Weather weather : parsedWeather){
                if (!currentDate.equals(weather.getTargetDate())) {
                    sortedWeather.add(new ArrayList<>(weathers));
                    currentDate = weather.getTargetDate();
                    weathers = new ArrayList<>();
                }
                weathers.add(weather);
            }
            sortedWeather.add(new ArrayList<>(weathers));
            return sortedWeather;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Date> getTargetDate(int cityId) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try (Statement statement = connection.createStatement()) {
            ResultSet dateResult = statement.executeQuery(String.format("SELECT DISTINCT TargetDate\n" +
                            "FROM Weather\n" +
                            "WHERE CityId = %d AND TargetDate = CheckedDate\n" +
                            "ORDER BY(TargetDate)",
                    cityId));
            List<Date> dates = new ArrayList<>();
            while (dateResult.next()) {
                dates.add(dateResult.getDate("TargetDate"));
            }
            return dates;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Double> getAllAvgTemperature(int cityId){
        try (Statement statement = connection.createStatement()) {
            ResultSet avgTempResult = statement.executeQuery(String.format("WITH allTemp as ((SELECT MinTemperature as Temperature, TargetDate\n" +
                            "FROM Weather\n" +
                            "WHERE CityId = %d AND TargetDate = CheckedDate)\n" +
                            "UNION ALL\n" +
                            "(SELECT MaxTemperature as Temperature, TargetDate\n" +
                            "FROM Weather\n" +
                            "Where CityId = %d AND TargetDate = CheckedDate))\n" +
                            "SELECT AVG(Cast(Temperature as Float)) as avgTemp\n" +
                            "FROM allTemp\n" +
                            "GROUP BY(TargetDate)\n" +
                            "ORDER BY(TargetDate)",
                    cityId, cityId));

            List<Double> avgTemp = new ArrayList<>();
            while (avgTempResult.next()) {
                avgTemp.add(avgTempResult.getDouble("avgTemp"));
            }
            return avgTemp;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public double getAvgTemperature(int cityId, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try (Statement statement = connection.createStatement()) {
            ResultSet avgTempResult = statement.executeQuery(String.format("WITH allTemp as ((SELECT MinTemperature as Temperature\n" +
                            "FROM Weather\n" +
                            "WHERE CityId = %d AND TargetDate = '%s' AND CheckedDate = '%s')\n" +
                            "UNION ALL\n" +
                            "(SELECT MaxTemperature as Temperature\n" +
                            "FROM Weather\n" +
                            "Where CityId = %d AND TargetDate = '%s' AND CheckedDate = '%s'))\n" +
                            "\n" +
                            "SELECT AVG(Cast(Temperature as Float)) as avgTemp\n" +
                            "FROM allTemp",
                    cityId, sdf.format(date), sdf.format(date), cityId, sdf.format(date), sdf.format(date)));

            double avgTemp = -9999;
            while (avgTempResult.next()) {
                avgTemp = avgTempResult.getDouble("avgTemp");
            }
            return avgTemp;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return -9999;
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
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
        }
    }

    public void addWeather(Weather weather) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Weather(CheckedDate, TargetDate, MinTemperature, MaxTemperature, Pressure, Humidity, SiteType, CityId) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, weather.getCheckedDate());
            statement.setObject(2, weather.getTargetDate());
            statement.setObject(3, weather.getMinTemperature());
            statement.setObject(4, weather.getMaxTemperature());
            statement.setObject(5, weather.getPressure());
            statement.setObject(6, weather.getHumidity());
            statement.setObject(7, SiteType.getValue(weather.getSiteType()));
            statement.setObject(8, weather.getCityId());
            statement.execute();
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
        }
    }

    private List<Weather> parseWeatherResponse(ResultSet weatherResult) throws SQLException {
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
                    .siteType(weatherResult.getInt("SiteType"))
                    .cityId(weatherResult.getInt("CityId"))
                    .buildWeather());
        }
        return weathers;
    }

    public User getUser(String login) {
        try (Statement statement = connection.createStatement()) {
            ResultSet userResult = statement.executeQuery(String.format("SELECT * FROM Users WHERE Login = '%s'", login));
            User user = new User();
            while (userResult.next()) {
                user.setLogin(userResult.getString("Login"));
                user.setPasswordHash(userResult.getString("PasswordHash"));
                user.setRole(Role.values()[userResult.getInt("RoleId")]);
            }
            return user;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return new User();
        }
    }

    public void addUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Users(Login, PasswordHash, RoleId) " +
                        "VALUES (?, ?, ?)")) {
            statement.setObject(1, user.getLogin());
            statement.setObject(2, user.getPasswordHash());
            statement.setObject(3, Role.getValue(user.getRole()));
            statement.execute();
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
        }
    }
}

