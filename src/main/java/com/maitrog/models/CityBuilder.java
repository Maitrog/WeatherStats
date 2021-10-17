package com.maitrog.models;

import java.util.ArrayList;
import java.util.List;

public class CityBuilder {
    private int id;
    private String nameRu;
    private String nameEn;
    private String urlYandex;
    private String urlRambler;
    private String urlWorldWeather;
    private String country;

    private List<Weather> weathers;

    public City buildCity() {
        return new City(id, nameRu, nameEn, urlYandex, urlRambler, urlWorldWeather, country, weathers);
    }

    public CityBuilder id(int id) {
        this.id = id;
        return this;
    }

    public CityBuilder nameRu(String nameRu) {
        this.nameRu = nameRu;
        return this;
    }

    public CityBuilder nameEn(String nameEn) {
        this.nameEn = nameEn;
        return this;
    }

    public CityBuilder urlYandex(String url) {
        urlYandex = url;
        return this;
    }

    public CityBuilder urlRambler(String url) {
        urlRambler = url;
        return this;
    }

    public CityBuilder urlWorldWeather(String url) {
        urlWorldWeather = url;
        return this;
    }

    public CityBuilder country(String country) {
        this.country = country;
        return this;
    }

    public CityBuilder weathers(List<Weather> weathers) {
        this.weathers = new ArrayList<>(weathers);
        return this;
    }
}
