package com.example.annie.dewatch.OpenWeatherMap;

import com.google.android.gms.maps.model.LatLng;

public class WeatherData {
    private static final String API_KEY = "&APPID=7bd4dc637f9432797e4ab3356c137966";
    private LatLng VancouverCoordinates = new LatLng(49.2577143,-123.1939432);

    private final static String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?";

    public static String getCurrentCityWeatherUrl(double latitude, double longitude) {
        return CURRENT_WEATHER_URL + "lat=" + latitude + "&lon=" + longitude + API_KEY;
    }

    public double getTemperature() {
        return main.temp;
    }

    public String getWeather() {
        return weather[0].main;
    }

    Weather[] weather;
    Main main;

    class Weather {
        int id;
        String main;
        String description;
    }

    class Main {
        double temp;
    }
}
