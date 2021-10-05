package Models;

import java.util.Date;

public class Weather {
    private final int id;
    private final Date checkedDate;
    private final Date targetDate;
    private final int minTemperature;
    private final int maxTemperature;
    private final int pressure;
    private final int humidity;
    private final int cityId;

    public Weather(int _id, Date _checkedDate, Date _targetDate, int _minTemperature, int _maxTemperature, int _pressure, int _humidity, int _cityId) {
        id = _id;
        checkedDate = _checkedDate;
        targetDate = _targetDate;
        minTemperature = _minTemperature;
        maxTemperature = _maxTemperature;
        pressure = _pressure;
        humidity = _humidity;
        cityId = _cityId;
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
