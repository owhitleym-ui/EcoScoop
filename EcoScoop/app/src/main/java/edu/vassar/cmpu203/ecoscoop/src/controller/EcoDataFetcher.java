package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.annotation.SuppressLint;

import com.openmeteo.sdk.WeatherApiResponse;
import com.openmeteo.sdk.Variable;
import com.openmeteo.sdk.VariablesSearch;
import com.openmeteo.sdk.VariableWithValues;
import com.openmeteo.sdk.Aggregation;
import com.openmeteo.sdk.VariablesWithTime;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.util.Objects;

import edu.vassar.cmpu203.ecoscoop.src.model.ClimateData;
import edu.vassar.cmpu203.ecoscoop.src.model.WeatherData;


public class EcoDataFetcher {

    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String CLIMATE_URL  = "https://climate-api.open-meteo.com/v1/climate";

    /**
     * Fetches and parses current ForeCast + hourly + daily forecast for a given location.
     * Must be called from a background thread.
     */
    public WeatherData fetch(double lat, double lon) throws Exception {
        @SuppressLint("DefaultLocale")
        String url = String.format(
                "%s?latitude=%.5f&longitude=%.5f" +
                "&current=temperature_2m,weather_code,wind_speed_10m" +
                "&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m" +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max" +
                "&timezone=auto&format=flatbuffers",
                FORECAST_URL, lat, lon
        );
        return parse(decode(fetchBytes(url)));
    }

    private WeatherData parse(WeatherApiResponse r) {

        // --- Current ---
        VariablesWithTime current = r.current();
        float currentTemp        = Objects.requireNonNull(new VariablesSearch(current).variable(Variable.temperature).altitude(2).first()).value();
        float currentWind        = Objects.requireNonNull(new VariablesSearch(current).variable(Variable.wind_speed).altitude(10).first()).value();
        float currentWeatherCode = Objects.requireNonNull(new VariablesSearch(current).variable(Variable.weather_code).first()).value();
        assert current != null;
        long  currentTime        = current.time();

        // --- Hourly ---
        VariablesWithTime hourly = r.hourly();
        float[] hourlyTemp       = toFloatArray(Objects.requireNonNull(new VariablesSearch(hourly).variable(Variable.temperature).altitude(2).first()));
        float[] hourlyHumidity   = toFloatArray(Objects.requireNonNull(new VariablesSearch(hourly).variable(Variable.relative_humidity).altitude(2).first()));
        float[] hourlyWind       = toFloatArray(Objects.requireNonNull(new VariablesSearch(hourly).variable(Variable.wind_speed).altitude(10).first()));
        assert hourly != null;
        long[]  hourlyTimes      = buildTimestamps(hourly);

        // --- Daily ---
        VariablesWithTime daily  = r.daily();
        float[] dailyTempMax     = toFloatArray(Objects.requireNonNull(new VariablesSearch(daily).variable(Variable.temperature).altitude(2).aggregation(Aggregation.maximum).first()));
        float[] dailyTempMin     = toFloatArray(Objects.requireNonNull(new VariablesSearch(daily).variable(Variable.temperature).altitude(2).aggregation(Aggregation.minimum).first()));
        float[] dailyPrecip      = toFloatArray(Objects.requireNonNull(new VariablesSearch(daily).variable(Variable.precipitation).aggregation(Aggregation.sum).first()));
        float[] dailyWindMax     = toFloatArray(Objects.requireNonNull(new VariablesSearch(daily).variable(Variable.wind_speed).altitude(10).aggregation(Aggregation.maximum).first()));
        assert daily != null;
        long[]  dailyTimes       = buildTimestamps(daily);

        return new WeatherData(
                r.latitude(), r.longitude(),
                r.timezone(), r.utcOffsetSeconds(),
                currentTemp, currentWind, currentWeatherCode, currentTime,
                dailyTimes, dailyTempMax, dailyTempMin, dailyPrecip, dailyWindMax,
                hourlyTimes, hourlyTemp, hourlyHumidity, hourlyWind
        );
    }

    // -------------------------------------------------------------------------
    // Climate
    // -------------------------------------------------------------------------

    /**
     * Fetches 3 months of historical daily climate data using the ERA5 model.
     * Uses flatbuffers — no JSON parsing needed.
     * Must be called from a background thread.
     */
    public ClimateData fetchClimate(double lat, double lon) throws Exception {
        LocalDate end   = LocalDate.now().minusYears(1).withMonth(12).withDayOfMonth(31);
        LocalDate start = end.minusMonths(3);

        @SuppressLint("DefaultLocale")
        String url = String.format(
                "%s?latitude=%.5f&longitude=%.5f" +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum" +
                "&models=ERA5" +
                "&start_date=%s&end_date=%s" +
                "&timezone=auto&format=flatbuffers",
                CLIMATE_URL, lat, lon, start, end
        );
        return parseClimate(decode(fetchBytes(url)));
    }

    private ClimateData parseClimate(WeatherApiResponse r) {
        VariablesWithTime daily = r.daily();

        float[] dailyTempMax = toFloatArray(Objects.requireNonNull(
                new VariablesSearch(daily).variable(Variable.temperature)
                        .altitude(2).aggregation(Aggregation.maximum).first()));
        float[] dailyTempMin = toFloatArray(Objects.requireNonNull(
                new VariablesSearch(daily).variable(Variable.temperature)
                        .altitude(2).aggregation(Aggregation.minimum).first()));
        float[] dailyPrecip  = toFloatArray(Objects.requireNonNull(
                new VariablesSearch(daily).variable(Variable.precipitation)
                        .aggregation(Aggregation.sum).first()));
        assert daily != null;
        long[] dailyTimes = buildTimestamps(daily);

        return new ClimateData(dailyTimes, dailyTempMax, dailyTempMin, dailyPrecip);
    }

    // -------------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------------

    private byte[] fetchBytes(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);

        try (InputStream is = conn.getInputStream();
             ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
            return buf.toByteArray();
        }
    }

    private WeatherApiResponse decode(byte[] raw) {
        ByteBuffer bb = ByteBuffer.wrap(raw, 4, raw.length - 4)
                .order(ByteOrder.LITTLE_ENDIAN);
        return WeatherApiResponse.getRootAsWeatherApiResponse(bb);
    }

    /** Copies SDK values into a plain float[] */
    private float[] toFloatArray(VariableWithValues v) {
        float[] arr = new float[v.valuesLength()];
        for (int i = 0; i < arr.length; i++) arr[i] = v.values(i);
        return arr;
    }

    /** Builds a unix timestamp array for each step in a VariablesWithTime bucket */
    private long[] buildTimestamps(VariablesWithTime vwt) {
        int count = (int) ((vwt.timeEnd() - vwt.time()) / vwt.interval());
        long[] times = new long[count];
        for (int i = 0; i < count; i++) {
            times[i] = vwt.time() + (long) i * vwt.interval();
        }
        return times;
    }
}
