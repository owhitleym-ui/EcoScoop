package edu.vassar.cmpu203.ecoscoop.src.controller;

import edu.vassar.cmpu203.ecoscoop.src.model.EcoDatabase;

public class EcoDataRetriever {

    public final EcoDatabase ecoDatabase;


    public EcoDataRetriever(EcoDatabase weatherDatabase){
        this.ecoDatabase = weatherDatabase;
    }

    public float getCurrentTemp()       { return ecoDatabase.getLatestWeather().currentTemp; }
    public float getCurrentWind()       { return ecoDatabase.getLatestWeather().currentWindSpeed; }
    public float getCurrentWeatherCode(){ return ecoDatabase.getLatestWeather().currentWeatherCode; }

    public float[] getDailyTempMax()    { return ecoDatabase.getLatestWeather().dailyTempMax; }
    public float[] getDailyTempMin()    { return ecoDatabase.getLatestWeather().dailyTempMin; }
    public float[] getDailyPrecip()     { return ecoDatabase.getLatestWeather().dailyPrecipitation; }
    public float[] getDailyWindMax()    { return ecoDatabase.getLatestWeather().dailyWindSpeedMax; }

    public float[] getHourlyTemp()      { return ecoDatabase.getLatestWeather().hourlyTemp; }
    public float[] getHourlyHumidity()  { return ecoDatabase.getLatestWeather().hourlyHumidity; }
    public float[] getHourlyWind()      { return ecoDatabase.getLatestWeather().hourlyWindSpeed; }
}
