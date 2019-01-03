package com.example.annie.dewatch.OpenWeatherMap;

import com.example.annie.dewatch.BuildConfig;
import com.google.android.gms.maps.model.LatLng;

public class WeatherData {
    private static final String API_KEY = "&APPID=" + BuildConfig.OpenWeatherMapKey;
    private LatLng VancouverCoordinates = new LatLng(49.2577143,-123.1939432);
    private final static String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?";

    private static final double KELVIN_CONVERSION = 273.15;

    public static String getCurrentCityWeatherUrl(double latitude, double longitude) {
        return CURRENT_WEATHER_URL + "lat=" + latitude + "&lon=" + longitude + API_KEY;
    }

    /**
     * @return temperature in Celsius
     */
    public double getTemperature() {
        return main.temp - KELVIN_CONVERSION;
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
