package edu.vassar.cmpu203.ecoscoop.src.model;

public interface EcoDatabase {
    WeatherData getLatestWeather();

    ClimateData getLatestClimate();
}
