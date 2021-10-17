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

    private final List<Weather> weathers;

    public City(int id, String nameRu, String nameEn, String urlYandex, String urlRambler, String urlWorldWeather,
                String country, List<Weather> weathers) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.urlYandex = urlYandex;
        this.urlRambler = urlRambler;
        this.urlWorldWeather = urlWorldWeather;
        this.country = country;
        this.weathers = weathers;
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

    public List<Weather> getWeathers() {
        return weathers;
    }

    public void addWeather(Weather weather) {
        weathers.add(weather);
    }

    public void addWeather(List<Weather> weathers) {
        this.weathers.addAll(weathers);
    }


}
