package com.maitrog.models;

import java.sql.Date;

public class WeatherBuilder {
    private int id;
    private Date checkedDate = new Date(System.currentTimeMillis());
    private Date targetDate;
    private int minTemperature;
    private int maxTemperature;
    private int pressure = -1;
    private int humidity = -1;
    private SiteType siteType;
    private int cityId;

    public Weather buildWeather() {
        return new Weather(id, checkedDate, targetDate, minTemperature, maxTemperature, pressure, humidity, siteType, cityId);
    }

    public WeatherBuilder id(int id) {
        this.id = id;
        return this;
    }

    public WeatherBuilder checkedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
        return this;
    }

    public WeatherBuilder targetDate(Date targetDate) {
        this.targetDate = targetDate;
        return this;
    }

    public WeatherBuilder minTemperature(int minTemp) {
        minTemperature = minTemp;
        return this;
    }

    public WeatherBuilder maxTemperature(int maxTemp) {
        maxTemperature = maxTemp;
        return this;
    }

    public WeatherBuilder pressure(int pressure) {
        this.pressure = pressure;
        return this;
    }

    public WeatherBuilder humidity(int humidity) {
        this.humidity = humidity;
        return this;
    }

    public WeatherBuilder siteType(int siteType) {
        this.siteType = SiteType.values()[siteType];
        return this;
    }

    public WeatherBuilder cityId(int cityId) {
        this.cityId = cityId;
        return this;
    }

    public WeatherBuilder siteType(SiteType siteType) {
        this.siteType = siteType;
        return this;
    }
}
