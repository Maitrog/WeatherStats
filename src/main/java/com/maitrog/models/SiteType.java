package com.maitrog.models;

public enum SiteType {
    Yandex,
    Rambler,
    WorldWeather;

    public static int getValue(SiteType siteType) {
        switch (siteType) {
            case Yandex -> {
                return 0;
            }
            case Rambler -> {
                return 1;
            }
            case WorldWeather -> {
                return 2;
            }
            default -> {
                return 3;
            }
        }
    }
}
