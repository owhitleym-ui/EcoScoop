package edu.vassar.cmpu203.ecoscoop.src.model;

import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataFetcher;

public class WeatherRepository implements WeatherDatabase{
    private final EcoDataFetcher fetcher;
    private WeatherData latest;

    public WeatherRepository(EcoDataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public void refresh(double lat, double lon) throws Exception {
        this.latest = fetcher.fetch(lat, lon);
    }

    @Override
    public WeatherData getLatest() {
        return latest;
    }
}
