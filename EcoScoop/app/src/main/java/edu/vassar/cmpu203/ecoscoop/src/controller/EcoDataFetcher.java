package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;

import edu.vassar.cmpu203.ecoscoop.src.model.ClimateData;
import edu.vassar.cmpu203.ecoscoop.src.model.WeatherData;

/**
 * Fetches weather and climate data from the Open-Meteo JSON API.
 * All methods are blocking and must be called from a background thread.
 */
public class EcoDataFetcher {

    private static final String TAG          = "EcoDataFetcher";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String CLIMATE_URL  = "https://archive-api.open-meteo.com/v1/archive";

    // Forecast Fetching

    /** Fetches current conditions, hourly, and 7-day daily forecast. */
    public WeatherData fetch(double lat, double lon) throws Exception {
        String url = String.format(Locale.US,
                "%s?latitude=%.5f&longitude=%.5f" +
                "&current=temperature_2m,weather_code,wind_speed_10m,apparent_temperature" +
                "&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m" +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum," +
                "wind_speed_10m_max,uv_index_max,apparent_temperature_max," +
                "et0_fao_evapotranspiration" +
                "&timezone=auto&forecast_days=7",
                FORECAST_URL, lat, lon);

        Log.d(TAG, "Weather URL: " + url);
        return parseWeather(new JSONObject(fetchString(url)));
    }

    private WeatherData parseWeather(JSONObject r) throws Exception {
        double latitude  = r.optDouble("latitude",           0.0);
        double longitude = r.optDouble("longitude",          0.0);
        String timezone  = r.optString("timezone",           "UTC");
        int utcOffset    = r.optInt("utc_offset_seconds",    0);

        // Current conditions
        JSONObject cur    = r.getJSONObject("current");
        float curTemp     = (float) cur.optDouble("temperature_2m",    0.0);
        float curWind     = (float) cur.optDouble("wind_speed_10m",    0.0);
        float curCode     = (float) cur.optInt("weather_code",         0);
        float curFeels    = (float) cur.optDouble("apparent_temperature", curTemp);
        long  curTime     = System.currentTimeMillis() / 1000L;

        // Hourly (first 24 h most relevant; API returns 168 h)
        JSONObject hourlyObj  = r.getJSONObject("hourly");
        float[] hourlyTemp    = toFloatArray(hourlyObj.getJSONArray("temperature_2m"));
        float[] hourlyHumid   = toFloatArray(hourlyObj.getJSONArray("relative_humidity_2m"));
        float[] hourlyWind    = toFloatArray(hourlyObj.getJSONArray("wind_speed_10m"));
        long[]  hourlyTimes   = new long[hourlyTemp.length];

        // Daily
        JSONObject dailyObj       = r.getJSONObject("daily");
        float[] dailyMax          = toFloatArray(dailyObj.getJSONArray("temperature_2m_max"));
        float[] dailyMin          = toFloatArray(dailyObj.getJSONArray("temperature_2m_min"));
        float[] dailyPrecip       = toFloatArray(dailyObj.getJSONArray("precipitation_sum"));
        float[] dailyWindMax      = toFloatArray(dailyObj.getJSONArray("wind_speed_10m_max"));
        float[] dailyUV           = toFloatArray(dailyObj.getJSONArray("uv_index_max"));
        float[] dailyFeelsMax     = toFloatArray(dailyObj.getJSONArray("apparent_temperature_max"));
        float[] dailyET0          = toFloatArray(dailyObj.getJSONArray("et0_fao_evapotranspiration"));
        long[]  dailyTimes        = new long[dailyMax.length];

        Log.d(TAG, "Parsed weather: temp=" + curTemp + " feels=" + curFeels + " code=" + curCode + " tz=" + timezone);

        return new WeatherData(
                latitude, longitude, timezone, utcOffset,
                curTemp, curWind, curCode, curTime, curFeels,
                dailyTimes, dailyMax, dailyMin, dailyPrecip, dailyWindMax,
                dailyUV, dailyFeelsMax, dailyET0,
                hourlyTimes, hourlyTemp, hourlyHumid, hourlyWind);
    }

    // Climate Fetching

    /** Fetches ~3 months of historical ERA5 climate data for anomaly calculation. */
    public ClimateData fetchClimate(double lat, double lon) throws Exception {
        LocalDate end   = LocalDate.now().minusYears(1).withMonth(12).withDayOfMonth(31);
        LocalDate start = end.minusMonths(3);

        String url = String.format(Locale.US,
                "%s?latitude=%.5f&longitude=%.5f" +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum" +
                "&start_date=%s&end_date=%s" +
                "&timezone=auto",
                CLIMATE_URL, lat, lon, start, end);

        Log.d(TAG, "Climate URL: " + url);
        return parseClimate(new JSONObject(fetchString(url)));
    }

    private ClimateData parseClimate(JSONObject r) throws Exception {
        JSONObject daily    = r.getJSONObject("daily");
        float[] dailyMax    = toFloatArray(daily.getJSONArray("temperature_2m_max"));
        float[] dailyMin    = toFloatArray(daily.getJSONArray("temperature_2m_min"));
        float[] dailyPrecip = toFloatArray(daily.getJSONArray("precipitation_sum"));
        long[]  dailyTimes  = new long[dailyMax.length];

        Log.d(TAG, "Parsed climate: " + dailyMax.length + " days of historical data");

        return new ClimateData(dailyTimes, dailyMax, dailyMin, dailyPrecip);
    }

    // Private Helper Methods

    /** Downloads a specific URL and returns its body as a UTF-8 string. */
    private String fetchString(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(15_000);

        int code = conn.getResponseCode();
        if (code != 200) {
            // Capture the error body so we can log it
            String errBody = readStream(conn.getErrorStream());
            conn.disconnect();
            throw new IOException("HTTP " + code + " from " + urlStr + " — " + errBody);
        }

        try {
            return readStream(conn.getInputStream());
        } finally {
            conn.disconnect();
        }
    }

    private String readStream(InputStream is) throws Exception {
        if (is == null) return "";
        try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
            return buf.toString("UTF-8");
        }
    }

    /** Converts a JSONArray of numbers to a float[]. Null elements become 0. */
    private float[] toFloatArray(JSONArray arr) throws Exception {
        float[] result = new float[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            result[i] = arr.isNull(i) ? 0f : (float) arr.getDouble(i);
        }
        return result;
    }
}
