package edu.vassar.cmpu203.ecoscoop.src.model;

public class WeatherData {

    public final double latitude;
    public final double longitude;
    public final String timezone;
    public final int utcOffsetSeconds;


    public final float currentTemp;
    public final float currentWindSpeed;
    public final float currentWeatherCode;
    public final long currentTime;

    // Daily forecast
    public final long[] dailyTimes;
    public final float[] dailyTempMax;
    public final float[] dailyTempMin;
    public final float[] dailyPrecipitation;
    public final float[] dailyWindSpeedMax;

    // Hourly forecast
    public final long[] hourlyTimes;
    public final float[] hourlyTemp;
    public final float[] hourlyHumidity;
    public final float[] hourlyWindSpeed;

    public WeatherData(
            double latitude, double longitude,
            String timezone, int utcOffsetSeconds,
            float currentTemp, float currentWindSpeed,
            float currentWeatherCode, long currentTime,
            long[] dailyTimes, float[] dailyTempMax,
            float[] dailyTempMin, float[] dailyPrecipitation,
            float[] dailyWindSpeedMax,
            long[] hourlyTimes, float[] hourlyTemp,
            float[] hourlyHumidity, float[] hourlyWindSpeed
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.utcOffsetSeconds = utcOffsetSeconds;
        this.currentTemp = currentTemp;
        this.currentWindSpeed = currentWindSpeed;
        this.currentWeatherCode = currentWeatherCode;
        this.currentTime = currentTime;
        this.dailyTimes = dailyTimes;
        this.dailyTempMax = dailyTempMax;
        this.dailyTempMin = dailyTempMin;
        this.dailyPrecipitation = dailyPrecipitation;
        this.dailyWindSpeedMax = dailyWindSpeedMax;
        this.hourlyTimes = hourlyTimes;
        this.hourlyTemp = hourlyTemp;
        this.hourlyHumidity = hourlyHumidity;
        this.hourlyWindSpeed = hourlyWindSpeed;
    }
}