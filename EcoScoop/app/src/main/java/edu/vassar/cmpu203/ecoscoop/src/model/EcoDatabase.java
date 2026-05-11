package edu.vassar.cmpu203.ecoscoop.src.model;

/**
 * Data store interface for the most recent {@link WeatherData} and {@link ClimateData}.
 */
public interface EcoDatabase {

    /** Returns the most recently fetched weather snapshot, or null if none is available. */
    WeatherData getLatestWeather();

    /** Returns the most recently fetched historical climate snapshot, or null if none is available. */
    ClimateData getLatestClimate();
}
