package com.maitrog.models;

import java.util.List;

public class City {
    private final int id;
    private final String nameRu;
    private final String nameEn;
    private final String urlYandex;
    private final String urlRambler;
    private final String urlWorldWeather;
    private final String country;

    private final List<Weather> yandexWeathers;
    private final List<Weather> ramblerWeathers;
    private final List<Weather> worldWeathers;

    public City(int id, String nameRu, String nameEn, String urlYandex, String urlRambler, String urlWorldWeather,
                String country, List<Weather> yandexWeathers, List<Weather> ramblerWeathers, List<Weather> worldWeathers) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.urlYandex = urlYandex;
        this.urlRambler = urlRambler;
        this.urlWorldWeather = urlWorldWeather;
        this.country = country;
        this.yandexWeathers = yandexWeathers;
        this.ramblerWeathers = ramblerWeathers;
        this.worldWeathers = worldWeathers;
    }

    public int getId() {
        return id;
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getUrlYandex() {
        return urlYandex;
    }

    public String getUrlRambler() {
        return urlRambler;
    }

    public String getUrlWorldWeather() {
        return urlWorldWeather;
    }

    public String getCountry() {
        return country;
    }

    public List<Weather> getYandexWeathers() {
        return yandexWeathers;
    }

    public List<Weather> getRamblerWeathers() { return ramblerWeathers; }

    public List<Weather> getWorldWeathers() { return worldWeathers; }

    public void addYandexWeather(Weather _weather) {
        yandexWeathers.add(_weather);
    }

    public void addYandexWeather(List<Weather> _weathers) {
        yandexWeathers.addAll(_weathers);
    }

    public void addRamblerWeather(Weather _weather) {
        ramblerWeathers.add(_weather);
    }

    public void addRamblerWeather(List<Weather> _weathers) {
        ramblerWeathers.addAll(_weathers);
    }

    public void addWorldWeather(Weather _weather) {
        worldWeathers.add(_weather);
    }

    public void addWorldWeather(List<Weather> _weathers) {
        worldWeathers.addAll(_weathers);
    }
}
