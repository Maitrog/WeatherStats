package Models;

import java.util.Date;

public class WeatherBuilder {
    private int id;
    private Date checkedDate = new Date(System.currentTimeMillis());
    private Date targetDate;
    private int minTemperature;
    private int maxTemperature;
    private int pressure = -1;
    private int humidity = -1;
    private int cityId;

    public Weather buildWeather() {
        return new Weather(id, checkedDate, targetDate, minTemperature, maxTemperature, pressure, humidity, cityId);
    }

    public WeatherBuilder id(int _id) {
        id = _id;
        return this;
    }

    public WeatherBuilder checkedDate(Date _checkedDate) {
        checkedDate = _checkedDate;
        return this;
    }

    public WeatherBuilder targetDate(Date _targetDate) {
        targetDate = _targetDate;
        return this;
    }

    public WeatherBuilder minTemperature(int _minTemp) {
        minTemperature = _minTemp;
        return this;
    }

    public WeatherBuilder maxTemperature(int _maxTemp) {
        maxTemperature = _maxTemp;
        return this;
    }

    public WeatherBuilder pressure(int _pressure) {
        pressure = _pressure;
        return this;
    }

    public WeatherBuilder humidity(int _humidity) {
        humidity = _humidity;
        return this;
    }

    public WeatherBuilder cityId(int _cityId) {
        cityId = _cityId;
        return this;
    }
}
