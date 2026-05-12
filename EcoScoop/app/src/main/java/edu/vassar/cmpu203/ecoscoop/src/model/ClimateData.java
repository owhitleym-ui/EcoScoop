package edu.vassar.cmpu203.ecoscoop.src.model;

/**
 * Immutable value object holding ERA5 historical climate arrays used for anomaly calculations.
 */
public class ClimateData {

    public final long[]  dailyTimes;
    public final float[] dailyTempMax;
    public final float[] dailyTempMin;
    public final float[] dailyPrecipitation;

    /** Creates a ClimateData snapshot with parallel daily arrays for times, high/low temps, and precipitation. */
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
