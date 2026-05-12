package edu.vassar.cmpu203.ecoscoop.src.model;

import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataFetcher;

/**
 * In-memory {@link EcoDatabase} implementation that stores the latest weather and climate snapshots
 * fetched by {@link EcoDataFetcher}.
 */
public class EcoRepository implements EcoDatabase {
    private final EcoDataFetcher fetcher;
    private WeatherData latestWeather;
    private ClimateData latestClimate;

    /** Creates a repository backed by the given {@link EcoDataFetcher}. */
    public EcoRepository(EcoDataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    /**
     * Fetches fresh weather and climate data for the given coordinates and stores them in memory.
     * Blocks the calling thread; must be invoked from a background thread.
     */
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
