package edu.vassar.cmpu203.ecoscoop.src.controller;

import edu.vassar.cmpu203.ecoscoop.src.model.WeatherDatabase;

public class WeatherRetriever {

    public final WeatherDatabase weatherDatabase;


    public WeatherRetriever(WeatherDatabase weatherDatabase){
        this.weatherDatabase = weatherDatabase;
    }

    public float getCurrentTemp()       { return weatherDatabase.getLatest().currentTemp; }
    public float getCurrentWind()       { return weatherDatabase.getLatest().currentWindSpeed; }
    public float getCurrentWeatherCode(){ return weatherDatabase.getLatest().currentWeatherCode; }

    public float[] getDailyTempMax()    { return weatherDatabase.getLatest().dailyTempMax; }
    public float[] getDailyTempMin()    { return weatherDatabase.getLatest().dailyTempMin; }
    public float[] getDailyPrecip()     { return weatherDatabase.getLatest().dailyPrecipitation; }
    public float[] getDailyWindMax()    { return weatherDatabase.getLatest().dailyWindSpeedMax; }

    public float[] getHourlyTemp()      { return weatherDatabase.getLatest().hourlyTemp; }
    public float[] getHourlyHumidity()  { return weatherDatabase.getLatest().hourlyHumidity; }
    public float[] getHourlyWind()      { return weatherDatabase.getLatest().hourlyWindSpeed; }
}
