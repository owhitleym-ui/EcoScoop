package edu.vassar.cmpu203.ecoscoop.src.model;

/**
 * Immutable value object holding all fields returned by the Open-Meteo forecast API:
 * current conditions, a 7-day daily forecast, and 168-hour hourly arrays.
 */
public class WeatherData {

    public final double latitude;
    public final double longitude;
    public final String timezone;
    public final int utcOffsetSeconds;

    // Current conditions
    public final float currentTemp;
    public final float currentWindSpeed;
    public final float currentWeatherCode;
    public final long  currentTime;
    public final float currentFeelsLike;

    // Daily forecast
    public final long[]  dailyTimes;
    public final float[] dailyTempMax;
    public final float[] dailyTempMin;
    public final float[] dailyPrecipitation;
    public final float[] dailyWindSpeedMax;
    public final float[] dailyUVIndexMax;
    public final float[] dailyApparentTempMax;
    public final float[] dailyEvapotranspiration;

    // Hourly forecast
    public final long[]  hourlyTimes;
    public final float[] hourlyTemp;
    public final float[] hourlyHumidity;
    public final float[] hourlyWindSpeed;

    /** Creates a WeatherData snapshot with all current, daily, and hourly fields from the API response. */
    public WeatherData(
            double latitude, double longitude,
            String timezone, int utcOffsetSeconds,
            float currentTemp, float currentWindSpeed,
            float currentWeatherCode, long currentTime,
            float currentFeelsLike,
            long[] dailyTimes, float[] dailyTempMax,
            float[] dailyTempMin, float[] dailyPrecipitation,
            float[] dailyWindSpeedMax,
            float[] dailyUVIndexMax,
            float[] dailyApparentTempMax,
            float[] dailyEvapotranspiration,
            long[] hourlyTimes, float[] hourlyTemp,
            float[] hourlyHumidity, float[] hourlyWindSpeed
    ) {
        this.latitude              = latitude;
        this.longitude             = longitude;
        this.timezone              = timezone;
        this.utcOffsetSeconds      = utcOffsetSeconds;
        this.currentTemp           = currentTemp;
        this.currentWindSpeed      = currentWindSpeed;
        this.currentWeatherCode    = currentWeatherCode;
        this.currentTime           = currentTime;
        this.currentFeelsLike      = currentFeelsLike;
        this.dailyTimes            = dailyTimes;
        this.dailyTempMax          = dailyTempMax;
        this.dailyTempMin          = dailyTempMin;
        this.dailyPrecipitation    = dailyPrecipitation;
        this.dailyWindSpeedMax     = dailyWindSpeedMax;
        this.dailyUVIndexMax       = dailyUVIndexMax;
        this.dailyApparentTempMax  = dailyApparentTempMax;
        this.dailyEvapotranspiration = dailyEvapotranspiration;
        this.hourlyTimes           = hourlyTimes;
        this.hourlyTemp            = hourlyTemp;
        this.hourlyHumidity        = hourlyHumidity;
        this.hourlyWindSpeed       = hourlyWindSpeed;
    }
}
