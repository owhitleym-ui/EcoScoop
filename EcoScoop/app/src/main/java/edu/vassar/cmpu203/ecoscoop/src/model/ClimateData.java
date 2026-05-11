package edu.vassar.cmpu203.ecoscoop.src.model;

public class ClimateData {

    public final long[]  dailyTimes;
    public final float[] dailyTempMax;
    public final float[] dailyTempMin;
    public final float[] dailyPrecipitation;

    public ClimateData(
            long[]  dailyTimes,
            float[] dailyTempMax,
            float[] dailyTempMin,
            float[] dailyPrecipitation
    ) {
        this.dailyTimes        = dailyTimes;
        this.dailyTempMax      = dailyTempMax;
        this.dailyTempMin      = dailyTempMin;
        this.dailyPrecipitation = dailyPrecipitation;
    }
}
