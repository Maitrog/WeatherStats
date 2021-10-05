package Models;

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

    public CityBuilder id(int _id) {
        id = _id;
        return this;
    }

    public CityBuilder nameRu(String _nameRu) {
        nameRu = _nameRu;
        return this;
    }

    public CityBuilder nameEn(String _nameEn) {
        nameEn = _nameEn;
        return this;
    }

    public CityBuilder urlYandex(String _url) {
        urlYandex = _url;
        return this;
    }

    public CityBuilder urlRambler(String _url) {
        urlRambler = _url;
        return this;
    }

    public CityBuilder urlWorldWeather(String _url) {
        urlWorldWeather = _url;
        return this;
    }

    public CityBuilder country(String _country) {
        country = _country;
        return this;
    }

    public CityBuilder weathers(List<Weather> _weathers) {
        weathers = new ArrayList<>(_weathers);
        return this;
    }
}
