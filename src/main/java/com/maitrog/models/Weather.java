package com.maitrog.models;

import java.sql.Date;

public class Weather {
    private final int id;
    private final Date checkedDate;
    private final Date targetDate;
    private final int minTemperature;
    private final int maxTemperature;
    private final int pressure;
    private final int humidity;
    private final Site site;
    private final int cityId;

    public Weather(int id, Date checkedDate, Date targetDate, int minTemperature, int maxTemperature, int pressure, int humidity, Site site, int cityId) {
        this.id = id;
        this.checkedDate = checkedDate;
        this.targetDate = targetDate;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.site = site;
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getCityId() {
        return cityId;
    }
}