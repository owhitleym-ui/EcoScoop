package edu.vassar.cmpu203.ecoscoop.src.view;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.vassar.cmpu203.ecoscoop.R;

/**
 * Bottom sheet showing historical climate averages and a temperature anomaly banner.
 * Receives raw float arrays and a metric flag; computes all display values internally.
 */
public class ClimateSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_USE_METRIC   = "useMetric";
    private static final String ARG_CURRENT_TEMP = "currentTemp";
    private static final String ARG_DAILY_MAX    = "dailyMax";
    private static final String ARG_DAILY_MIN    = "dailyMin";
    private static final String ARG_DAILY_PRECIP = "dailyPrecip";

    public static ClimateSheetFragment newInstance(
            boolean useMetric, float currentTempCelsius,
            float[] dailyMax, float[] dailyMin, float[] dailyPrecip) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_USE_METRIC, useMetric);
        args.putFloat(ARG_CURRENT_TEMP, currentTempCelsius);
        args.putFloatArray(ARG_DAILY_MAX, dailyMax);
        args.putFloatArray(ARG_DAILY_MIN, dailyMin);
        args.putFloatArray(ARG_DAILY_PRECIP, dailyPrecip);
        ClimateSheetFragment f = new ClimateSheetFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_climate_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = requireArguments();

        boolean useMetric   = args.getBoolean(ARG_USE_METRIC);
        float currentTempC  = args.getFloat(ARG_CURRENT_TEMP);
        float[] dailyMax    = args.getFloatArray(ARG_DAILY_MAX);
        float[] dailyMin    = args.getFloatArray(ARG_DAILY_MIN);
        float[] dailyPrecip = args.getFloatArray(ARG_DAILY_PRECIP);

        String tempUnit   = useMetric ? "°C" : "°F";
        String precipUnit = useMetric ? "mm" : "in";

        float avgHigh     = toTemp(average(dailyMax), useMetric);
        float avgLow      = toTemp(average(dailyMin), useMetric);
        float totalPrecip = toPrecip(sum(dailyPrecip), useMetric);
        float currentTemp = toTemp(currentTempC, useMetric);
        float anomaly     = currentTemp - avgHigh;
        String sign       = anomaly >= 0 ? "+" : "";

        // Anomaly banner — background color is the only dynamic part
        int anomalyColor  = anomaly > 0 ? 0xFFC0392B : 0xFF2980B9;
        int bannerBgColor = anomaly > 0 ? 0x15C0392B : 0x152980B9;
        LinearLayout banner = view.findViewById(R.id.anomalyBanner);
        GradientDrawable bannerBg = new GradientDrawable();
        bannerBg.setColor(bannerBgColor);
        bannerBg.setCornerRadius(getResources().getDisplayMetrics().density * 8);
        banner.setBackground(bannerBg);

        TextView anomalyMainTv = view.findViewById(R.id.anomalyMainText);
        anomalyMainTv.setText(sign + String.format("%.1f", anomaly) + tempUnit + " vs historical avg");
        anomalyMainTv.setTextColor(anomalyColor);

        TextView anomalyBigTv = view.findViewById(R.id.anomalyBigNumber);
        anomalyBigTv.setText(sign + String.format("%.0f", anomaly) + "°");
        anomalyBigTv.setTextColor(anomalyColor);

        ((TextView) view.findViewById(R.id.anomalyDesc)).setText(
            anomaly > 2  ? "Notably warmer than historical average" :
            anomaly < -2 ? "Notably cooler than historical average" :
                           "Near the historical average");

        // Chips
        String[][] chipData = {
            {String.format("%.1f", avgHigh) + tempUnit,          "Avg High"},
            {String.format("%.1f", avgLow)  + tempUnit,          "Avg Low"},
            {String.format("%.0f", totalPrecip) + " " + precipUnit, "Total Precip"}
        };
        int[] valueIds = {R.id.chipValue0, R.id.chipValue1, R.id.chipValue2};
        int[] labelIds = {R.id.chipLabel0, R.id.chipLabel1, R.id.chipLabel2};
        for (int i = 0; i < 3; i++) {
            ((TextView) view.findViewById(valueIds[i])).setText(chipData[i][0]);
            ((TextView) view.findViewById(labelIds[i])).setText(chipData[i][1]);
        }

        // Historical data table
        LinearLayout table = view.findViewById(R.id.climateTableContainer);
        int count = dailyMax != null ? dailyMax.length : 0;
        LayoutInflater inf = LayoutInflater.from(getContext());
        for (int i = 0; i < count; i++) {
            float hi = toTemp(dailyMax[i], useMetric);
            float lo = toTemp(dailyMin  != null ? dailyMin[i]   : 0, useMetric);
            float pr = toPrecip(dailyPrecip != null ? dailyPrecip[i] : 0, useMetric);

            View row = inf.inflate(R.layout.item_climate_row, table, false);
            ((TextView) row.findViewById(R.id.climateDay)).setText("Day " + (i + 1));
            ((TextView) row.findViewById(R.id.climateHigh)).setText(String.format("%.0f%s", hi, tempUnit));
            ((TextView) row.findViewById(R.id.climateLow)).setText(String.format("%.0f%s", lo, tempUnit));
            ((TextView) row.findViewById(R.id.climatePrecip)).setText(String.format("%.1f %s", pr, precipUnit));
            if (i % 2 == 0) row.setBackgroundColor(0x08000000);
            table.addView(row);
        }
    }

    // ── Unit conversion ───────────────────────────────────────────────────────

    private float toTemp(float celsius, boolean useMetric) {
        return useMetric ? celsius : celsius * 9f / 5f + 32f;
    }

    private float toPrecip(float mm, boolean useMetric) {
        return useMetric ? mm : mm * 0.0393701f;
    }

    // ── Math helpers ──────────────────────────────────────────────────────────

    private float average(float[] a) {
        if (a == null || a.length == 0) return 0f;
        float s = 0; for (float v : a) s += v; return s / a.length;
    }

    private float sum(float[] a) {
        if (a == null) return 0f;
        float s = 0; for (float v : a) s += v; return s;
    }
}
