package com.maitrog.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maitrog.weatherstats.Main;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    }

    public List<Weather> getDistributionData(String name, Date targetDate, Date lowestDate, int dateDiff) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery(String.format("SELECT Weather.Id, CheckedDate, TargetDate, MinTemperature, MaxTemperature, Pressure, Humidity, SiteType, CityId " +
                            "FROM Weather " +
                            "JOIN Cities ON Cities.Id = Weather.CityId " +
                            "WHERE (Cities.NameRu = '%s' OR Cities.NameEn = '%s') AND (TargetDate BETWEEN '%s' AND '%s') AND DATEDIFF(day, CheckedDate, TargetDate) = %d" +
                            "ORDER BY(CheckedDate)",
                    name, name, sdf.format(lowestDate), sdf.format(targetDate), dateDiff));
            return parseWeatherResponse(weatherResult);
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<List<List<Weather>>> getAllSortedWeather(SiteType siteType) {
        try (Statement statement = connection.createStatement()) {
            ResultSet weatherResult = statement.executeQuery(String.format("""
                            SELECT *
                            FROM Weather
                            WHERE SiteType = %d AND TargetDate IN (SELECT DISTINCT TargetDate
                            FROM Weather
                            WHERE TargetDate = CheckedDate)
                            ORDER BY CityId, TargetDate, CheckedDate""",
                    SiteType.getValue(siteType)));
            List<Weather> parsedWeather = parseWeatherResponse(weatherResult);
            List<List<List<Weather>>> allSortedWeathers = new ArrayList<>();
            List<List<Weather>> sortedWeather = new ArrayList<>();
            Date currentDate = parsedWeather.get(0).getTargetDate();
            int currentCityId = 0;
            List<Weather> weathers = new ArrayList<>();
            for (Weather weather : parsedWeather) {
                if (currentCityId != weather.getCityId()) {
                    if (currentCityId != 0) {
                        sortedWeather.add(new ArrayList<>(weathers));
                        weathers = new ArrayList<>();
                        allSortedWeathers.add(new ArrayList<>(sortedWeather));
                    }
                    currentCityId = weather.getCityId();
                    sortedWeather = new ArrayList<>();
                }
                if (!currentDate.equals(weather.getTargetDate())) {
                    if (weathers.size() > 0) {
                        sortedWeather.add(new ArrayList<>(weathers));
                        weathers = new ArrayList<>();
                    }
                    currentDate = weather.getTargetDate();
                }
                weathers.add(weather);
            }
            sortedWeather.add(new ArrayList<>(weathers));
            allSortedWeathers.add(new ArrayList<>(sortedWeather));
            return allSortedWeathers;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            return Collections.emptyList();
        }
    }

    public List<List<Double>> getAllAvgTemperature() {
        try (Statement statement = connection.createStatement()) {
            ResultSet avgTempResult = statement.executeQuery("""
                    WITH allTemp as ((SELECT MinTemperature as Temperature, TargetDate, CityId
                    FROM Weather
                    WHERE TargetDate = CheckedDate)
                    UNION ALL
                    (SELECT MaxTemperature as Temperature, TargetDate, CityId
                    FROM Weather
                    Where TargetDate = CheckedDate))
                    SELECT CityId, AVG(Cast(Temperature as Float)) as avgTemp
                    FROM allTemp
                    GROUP BY CityId, TargetDate
                    ORDER BY CityId, TargetDate""");
            List<List<Double>> allAvgTemperature = new ArrayList<>();
            List<Double> avgTemp = new ArrayList<>();
            int cityId = 0;
            while (avgTempResult.next()) {
                int newCityId = avgTempResult.getInt("cityId");
                if (cityId != newCityId) {
                    if (cityId != 0) {
                        allAvgTemperature.add(avgTemp);
                    }
                    cityId = newCityId;
                    avgTemp = new ArrayList<>();
                }
                avgTemp.add(avgTempResult.getDouble("avgTemp"));
            }
            allAvgTemperature.add(avgTemp);
            return allAvgTemperature;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            return Collections.emptyList();
        }
    }

    public List<List<Double>> getAllMaxTemperature() {
        try (Statement statement = connection.createStatement()) {
            ResultSet avgTempResult = statement.executeQuery("""
                    WITH allTemp as (
                    (SELECT MaxTemperature as Temperature, TargetDate, CityId
                    FROM Weather
                    Where TargetDate = CheckedDate))
                    SELECT distinct CityId, TargetDate, PERCENTILE_CONT(0.5) within group(order by Temperature) OVER(PArtition BY CityId, TargetDate) as avgTemp
                    FROM allTemp
                    ORDER BY CityId, TargetDate""");
            List<List<Double>> allAvgTemperature = new ArrayList<>();
            List<Double> avgTemp = new ArrayList<>();
            int cityId = 0;
            while (avgTempResult.next()) {
                int newCityId = avgTempResult.getInt("cityId");
                if (cityId != newCityId) {
                    if (cityId != 0) {
                        allAvgTemperature.add(avgTemp);
                    }
                    cityId = newCityId;
                    avgTemp = new ArrayList<>();
                }
                avgTemp.add(avgTempResult.getDouble("avgTemp"));
            }
            allAvgTemperature.add(avgTemp);
            return allAvgTemperature;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            return Collections.emptyList();
        }
    }

    public List<List<Double>> getAllMinTemperature() {
        try (Statement statement = connection.createStatement()) {
            ResultSet avgTempResult = statement.executeQuery("""
                    WITH allTemp as (
                    (SELECT MinTemperature as Temperature, TargetDate, CityId
                    FROM Weather
                    Where TargetDate = CheckedDate))
                    SELECT distinct CityId, TargetDate, PERCENTILE_CONT(0.5) within group(order by Temperature) OVER(PArtition BY CityId, TargetDate) as avgTemp
                    FROM allTemp
                    ORDER BY CityId, TargetDate""");
            List<List<Double>> allAvgTemperature = new ArrayList<>();
            List<Double> avgTemp = new ArrayList<>();
            int cityId = 0;
            while (avgTempResult.next()) {
                int newCityId = avgTempResult.getInt("cityId");
                if (cityId != newCityId) {
                    if (cityId != 0) {
                        allAvgTemperature.add(avgTemp);
                    }
                    cityId = newCityId;
                    avgTemp = new ArrayList<>();
                }
                avgTemp.add(avgTempResult.getDouble("avgTemp"));
            }
            allAvgTemperature.add(avgTemp);
            return allAvgTemperature;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Pair<Date, Double>> getAvgTemperature(String cityName, Date lowestDate, Date targetDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try (Statement statement = connection.createStatement()) {
            ResultSet avgTempResult = statement.executeQuery(String.format("""
                    WITH allTemp as ((SELECT MinTemperature as Temperature, TargetDate
                    FROM Weather
                    LEFT JOIN Cities ON Cities.Id = Weather.CityId
                    WHERE TargetDate = CheckedDate AND (Cities.NameRu = '%s' OR Cities.NameEn = '%s') AND (TargetDate BETWEEN '%s' AND '%s'))
                    UNION ALL
                    (SELECT MaxTemperature as Temperature, TargetDate
                    FROM Weather
                    LEFT JOIN Cities ON Cities.Id = Weather.CityId
                    Where TargetDate = CheckedDate AND (Cities.NameRu = '%s' OR Cities.NameEn = '%s') AND (TargetDate BETWEEN '%s' AND '%s')))
                    SELECT TargetDate, AVG(Cast(Temperature as Float)) as avgTemp
                    FROM allTemp
                    GROUP BY TargetDate
                    ORDER BY TargetDate""", cityName, cityName, sdf.format(lowestDate), sdf.format(targetDate), cityName, cityName, sdf.format(lowestDate), sdf.format(targetDate)));
            List<Pair<Date, Double>> allAvgTemperature = new ArrayList<>();
            while (avgTempResult.next()) {
                Date newDate = avgTempResult.getDate("TargetDate");
                Double avg = avgTempResult.getDouble("avgTemp");
                allAvgTemperature.add(new Pair<>(newDate, avg));
            }
            return allAvgTemperature;
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, throwables.getMessage());
            return Collections.emptyList();
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

