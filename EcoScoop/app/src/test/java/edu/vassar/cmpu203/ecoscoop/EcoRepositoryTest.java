package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataFetcher;
import edu.vassar.cmpu203.ecoscoop.src.model.ClimateData;
import edu.vassar.cmpu203.ecoscoop.src.model.EcoRepository;
import edu.vassar.cmpu203.ecoscoop.src.model.WeatherData;

/**
 * Unit tests for the non-trivial behaviour of {@link EcoRepository}.
 *
 * Focuses on: null initial state before any data is fetched, and correct
 * storage and retrieval of weather and climate snapshots after {@code refresh()}.
 *
 * Uses a stub {@link EcoDataFetcher} subclass that overrides {@code fetch()} and
 * {@code fetchClimate()} to return pre-built data objects, avoiding real network
 * calls.
 */
public class EcoRepositoryTest {

    private EcoRepository repo;
    private WeatherData   stubWeather;
    private ClimateData   stubClimate;

    @Before
    public void setUp() {
        stubWeather = new WeatherData(
                40.7, -74.0, "America/New_York", -18000,
                22f, 10f, 1f, 0L, 21f,
                new long[7],   new float[7],   new float[7],
                new float[7],  new float[7],   new float[7],
                new float[7],  new float[7],
                new long[168], new float[168], new float[168], new float[168]);

        stubClimate = new ClimateData(
                new long[90], new float[90], new float[90], new float[90]);

        EcoDataFetcher stubFetcher = new EcoDataFetcher() {
            @Override public WeatherData fetch(double lat, double lon) { return stubWeather; }
            @Override public ClimateData fetchClimate(double lat, double lon) { return stubClimate; }
        };

        repo = new EcoRepository(stubFetcher);
    }

    // Initial state

    /**
     * Verifies that {@code getLatestWeather()} returns {@code null} before
     * {@code refresh()} is called — callers must handle the null case.
     */
    @Test
    public void testGetLatestWeather_nullBeforeRefresh() {
        assertNull(repo.getLatestWeather());
    }

    /**
     * Verifies that {@code getLatestClimate()} returns {@code null} before
     * {@code refresh()} is called — callers must handle the null case.
     */
    @Test
    public void testGetLatestClimate_nullBeforeRefresh() {
        assertNull(repo.getLatestClimate());
    }

    // After refresh

    /**
     * Verifies that after {@code refresh()}, {@code getLatestWeather()} returns
     * the exact WeatherData object produced by the fetcher.
     */
    @Test
    public void testRefresh_weatherStoredAndRetrievable() throws Exception {
        repo.refresh(40.7, -74.0);
        assertSame(stubWeather, repo.getLatestWeather());
    }

    /**
     * Verifies that after {@code refresh()}, {@code getLatestClimate()} returns
     * the exact ClimateData object produced by the fetcher.
     */
    @Test
    public void testRefresh_climateStoredAndRetrievable() throws Exception {
        repo.refresh(40.7, -74.0);
        assertSame(stubClimate, repo.getLatestClimate());
    }

    /**
     * Verifies that calling {@code refresh()} a second time overwrites the stored
     * data — the repository always holds the most recent snapshot.
     */
    @Test
    public void testRefresh_calledTwice_overwritesPreviousData() throws Exception {
        repo.refresh(40.7, -74.0);
        WeatherData first = repo.getLatestWeather();
        repo.refresh(40.7, -74.0);
        assertNotNull(repo.getLatestWeather());
        assertSame(first, repo.getLatestWeather());
    }

    /**
     * Verifies that both weather and climate are non-null after a single refresh,
     * confirming the fetcher is called for both data types in one pass.
     */
    @Test
    public void testRefresh_bothFieldsPopulated() throws Exception {
        repo.refresh(40.7, -74.0);
        assertNotNull(repo.getLatestWeather());
        assertNotNull(repo.getLatestClimate());
    }
}
