package edu.vassar.cmpu203.ecoscoop.src.model;

import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataFetcher;

public class EcoRepository implements EcoDatabase {
    private final EcoDataFetcher fetcher;
    private WeatherData latestWeather;
    private ClimateData latestClimate;

    public EcoRepository(EcoDataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public void refresh(double lat, double lon) throws Exception {
        this.latestWeather = fetcher.fetch(lat, lon);
        this.latestClimate = fetcher.fetchClimate(lat, lon);
    }

    @Override
    public WeatherData getLatestWeather() {
        return latestWeather;
    }

    @Override
    public ClimateData getLatestClimate() {
        return latestClimate;
    }
}
