package edu.vassar.cmpu203.ecoscoop.src.controller;

import edu.vassar.cmpu203.ecoscoop.src.model.EcoDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.WeatherData;

/**
 * Null-safe accessor facade over {@link EcoDatabase} that returns safe defaults (0/null)
 * when no weather data has been loaded yet.
 */
public class EcoDataRetriever {

    public final EcoDatabase ecoDatabase;

    /** Creates a retriever backed by the given {@link EcoDatabase}. */
    public EcoDataRetriever(EcoDatabase weatherDatabase) {
        this.ecoDatabase = weatherDatabase;
    }

    private WeatherData weather() { return ecoDatabase.getLatestWeather(); }

    public float getCurrentTemp()        { return weather() != null ? weather().currentTemp          : 0f; }
    public float getCurrentWind()        { return weather() != null ? weather().currentWindSpeed      : 0f; }
    public float getCurrentWeatherCode() { return weather() != null ? weather().currentWeatherCode    : 0f; }
    public float getCurrentFeelsLike()   { return weather() != null ? weather().currentFeelsLike      : 0f; }
    public String getTimezone()          { return weather() != null ? weather().timezone              : null; }
    public double getLatitude()          { return weather() != null ? weather().latitude              : 0.0; }
    public double getLongitude()         { return weather() != null ? weather().longitude             : 0.0; }

    public float[] getDailyTempMax()          { return weather() != null ? weather().dailyTempMax            : null; }
    public float[] getDailyTempMin()          { return weather() != null ? weather().dailyTempMin            : null; }
    public float[] getDailyPrecip()           { return weather() != null ? weather().dailyPrecipitation      : null; }
    public float[] getDailyWindMax()          { return weather() != null ? weather().dailyWindSpeedMax        : null; }
    public float[] getDailyUVIndexMax()       { return weather() != null ? weather().dailyUVIndexMax          : null; }
    public float[] getDailyApparentTempMax()  { return weather() != null ? weather().dailyApparentTempMax     : null; }
    public float[] getDailyEvapotranspiration(){ return weather() != null ? weather().dailyEvapotranspiration : null; }

    public float[] getHourlyTemp()     { return weather() != null ? weather().hourlyTemp        : null; }
    public float[] getHourlyHumidity() { return weather() != null ? weather().hourlyHumidity    : null; }
    public float[] getHourlyWind()     { return weather() != null ? weather().hourlyWindSpeed   : null; }
}
