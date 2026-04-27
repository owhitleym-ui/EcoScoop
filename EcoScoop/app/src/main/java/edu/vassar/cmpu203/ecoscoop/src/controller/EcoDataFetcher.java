package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.annotation.SuppressLint;

import com.google.flatbuffers.FlatBufferBuilder;
import com.openmeteo.sdk.WeatherApiResponse;
import com.openmeteo.sdk.Model;
import com.openmeteo.sdk.Variable;
import com.openmeteo.sdk.VariablesSearch;
import com.openmeteo.sdk.VariableWithValues;
import com.openmeteo.sdk.Variable;
import com.openmeteo.sdk.Aggregation;
import com.openmeteo.sdk.VariablesWithTime;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import edu.vassar.cmpu203.ecoscoop.src.model.WeatherData;


public class EcoDataFetcher {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    /**
     * Fetches and parses all weather data for a given location.
     * Must be called from a background thread.
     *
     * @param lat Latitude from GPS or any source
     * @param lon Longitude from GPS or any source
     * @return Fully populated WeatherData object
     */
    public WeatherData fetch(double lat, double lon) throws Exception {
        byte[] raw = fetchBytes(lat, lon);
        WeatherApiResponse response = decode(raw);
        return parse(response);
    }

    // -------------------------------------------------------------------------
    // HTTP
    // -------------------------------------------------------------------------
    private byte[] fetchBytes(double lat, double lon) throws Exception {
        @SuppressLint("DefaultLocale") String url = String.format(
                "%s?latitude=%.5f&longitude=%.5f" +
                        "&current=temperature_2m,weather_code,wind_speed_10m" +
                        "&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m" +
                        "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max" +
                        "&timezone=auto&format=flatbuffers",
                BASE_URL, lat, lon
        );

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
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

    // -------------------------------------------------------------------------
    // FlatBuffer decode — skips the 4-byte size prefix
    // -------------------------------------------------------------------------
    private WeatherApiResponse decode(byte[] raw) {
        ByteBuffer bb = ByteBuffer.wrap(raw, 4, raw.length - 4)
                .order(ByteOrder.LITTLE_ENDIAN);
        return WeatherApiResponse.getRootAsWeatherApiResponse(bb);
    }

    // -------------------------------------------------------------------------
    // Parse everything out into WeatherData
    // -------------------------------------------------------------------------
    private WeatherData parse(WeatherApiResponse r) {

        // --- Current ---
        VariablesWithTime current = r.current();
        float currentTemp        = new VariablesSearch(current).variable(Variable.temperature).altitude(2).first().value();
        float currentWind        = new VariablesSearch(current).variable(Variable.wind_speed).altitude(10).first().value();
        float currentWeatherCode = new VariablesSearch(current).variable(Variable.weather_code).first().value();
        long  currentTime        = current.time();

        // --- Hourly ---
        VariablesWithTime hourly = r.hourly();
        float[] hourlyTemp       = toFloatArray(new VariablesSearch(hourly).variable(Variable.temperature).altitude(2).first());
        float[] hourlyHumidity   = toFloatArray(new VariablesSearch(hourly).variable(Variable.relative_humidity).altitude(2).first());
        float[] hourlyWind       = toFloatArray(new VariablesSearch(hourly).variable(Variable.wind_speed).altitude(10).first());
        long[]  hourlyTimes      = buildTimestamps(hourly);

        // --- Daily ---
        VariablesWithTime daily  = r.daily();
        float[] dailyTempMax     = toFloatArray(new VariablesSearch(daily).variable(Variable.temperature).altitude(2).aggregation(Aggregation.maximum).first());
        float[] dailyTempMin     = toFloatArray(new VariablesSearch(daily).variable(Variable.temperature).altitude(2).aggregation(Aggregation.minimum).first());
        float[] dailyPrecip      = toFloatArray(new VariablesSearch(daily).variable(Variable.precipitation).aggregation(Aggregation.sum).first());
        float[] dailyWindMax     = toFloatArray(new VariablesSearch(daily).variable(Variable.wind_speed).altitude(10).aggregation(Aggregation.maximum).first());
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
    // Helpers
    // -------------------------------------------------------------------------

    /** Copies SDK values into a plain float[] */
    private float[] toFloatArray(VariableWithValues v) {
        float[] arr = new float[v.valuesLength()];
        for (int i = 0; i < arr.length; i++) arr[i] = v.values(i);
        return arr;
    }

    /** Builds a unix timestamp for each step in a VariablesWithTime bucket */
    private long[] buildTimestamps(VariablesWithTime vwt) {
        // number of steps = length of any variable in this bucket
        // use interval + start to compute each timestamp
        int count = (int) ((vwt.timeEnd() - vwt.time()) / vwt.interval());
        long[] times = new long[count];
        for (int i = 0; i < count; i++) {
            times[i] = vwt.time() + (long) i * vwt.interval();
        }
        return times;
    }
}




