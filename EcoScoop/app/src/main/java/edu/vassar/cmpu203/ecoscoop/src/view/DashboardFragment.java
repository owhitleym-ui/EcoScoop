package edu.vassar.cmpu203.ecoscoop.src.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import edu.vassar.cmpu203.ecoscoop.R;
import edu.vassar.cmpu203.ecoscoop.databinding.FragmentDashboardBinding;
import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataRetriever;
import edu.vassar.cmpu203.ecoscoop.src.model.ClimateData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Fragment that displays current weather conditions, a 7-day forecast strip, weather metric cards
 * (UV, wind, humidity, ET₀, feels-like), and a historical climate anomaly panel with
 * tappable {@link com.google.android.material.bottomsheet.BottomSheetDialog} detail views.
 */
public class DashboardFragment extends Fragment implements DashboardUI {

    private FragmentDashboardBinding binding;
    private DashboardUI.Listener listener;
    private EcoDataRetriever pendingRetriever;
    private boolean useMetric = true; // default to metric; updated by controller before display

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashboardUI.Listener) {
            this.listener = (DashboardUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement DashboardUI.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.articleFeedTab.setOnClickListener(v -> { if (listener != null) listener.onArticleTabClick(); });
        binding.dashboardTab.setOnClickListener(v -> { /* already here */ });
        binding.searchTab.setOnClickListener(v -> { if (listener != null) listener.onSearchClick(); });
        binding.profileTab.setOnClickListener(v -> { if (listener != null) listener.onProfileClick(); });

        binding.btnSearchGPS.setOnClickListener(v -> { if (listener != null) listener.onRequestGPSRefresh(); });
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.searchEditText.getText().toString().trim();
            if (!query.isEmpty() && listener != null) listener.onSearchLocation(query);
            return true;
        });

        if (pendingRetriever != null) {
            onWeatherLoaded(pendingRetriever);
            pendingRetriever = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }

    // DashboardUI Implementation

    /** Populates all dashboard sections from the given retriever; deferred if the view is not yet ready. */
    @SuppressLint("SetTextI18n")
    @Override
    public void onWeatherLoaded(EcoDataRetriever retriever) {
        if (binding == null) { pendingRetriever = retriever; return; }

        bindHeader(retriever);
        bindCards(retriever);
        bindDailyStrip(retriever);
        bindClimate(retriever);
    }

    @Override
    public void setListener(DashboardUI.Listener listener) {
        this.listener = listener;
    }

    /** Called by the controller before or after weather data arrives. */
    public void setUseMetric(boolean useMetric) {
        this.useMetric = useMetric;
    }

    // Unit Conversion Settings Helper

    /** Convert Celsius to the user's preferred temperature unit. */
    private float temp(float celsius) {
        return useMetric ? celsius : celsius * 9f / 5f + 32f;
    }

    private String tempUnit() { return useMetric ? "°C" : "°F"; }

    /** Convert km/h to the user's preferred speed unit. */
    private float wind(float kmh) {
        return useMetric ? kmh : kmh * 0.621371f;
    }

    private String windUnit() { return useMetric ? "km/h" : "mph"; }

    /** Convert mm to the user's preferred precipitation unit. */
    private float precip(float mm) {
        return useMetric ? mm : mm * 0.0393701f;
    }

    private String precipUnit() { return useMetric ? "mm" : "in"; }

    // ── Bind methods ──────────────────────────────────────────────────────────

    /** Populates the hero temperature, condition label, emoji, and high/low text. */
    private void bindHeader(EcoDataRetriever retriever) {
        int code = (int) retriever.getCurrentWeatherCode();
        float[] max = retriever.getDailyTempMax();
        float[] min = retriever.getDailyTempMin();

        binding.textHeroTemp.setText(String.format("%.0f°", temp(retriever.getCurrentTemp())));
        binding.textHeroCondition.setText(conditionLabel(code));
        binding.textHeroIcon.setText(conditionEmoji(code));

        if (max != null && min != null && max.length > 0)
            binding.textHeroHighLow.setText(
                String.format("H: %.0f°  L: %.0f°", temp(max[0]), temp(min[0])));
        // Location label is set separately via setLocationLabel() by the controller.
    }

    /** Updates the location chip with a resolved city name or coordinate string. */
    public void setLocationLabel(String label) {
        if (binding != null) {
            binding.textLocationName.setText(label);
        }
    }

    /** Populates each metric card (temp, wind, precip, UV, feels-like, ET₀, humidity) and wires their tap listeners. */
    private void bindCards(EcoDataRetriever retriever) {
        float[] precip  = retriever.getDailyPrecip();
        float[] humid   = retriever.getHourlyHumidity();
        float[] max     = retriever.getDailyTempMax();
        float[] min     = retriever.getDailyTempMin();
        float[] windMax = retriever.getDailyWindMax();

        binding.textCurrentTemp.setText(String.format("%.1f%s", temp(retriever.getCurrentTemp()), tempUnit()));
        binding.textCurrentWind.setText(String.format("%.1f %s", wind(retriever.getCurrentWind()), windUnit()));
        binding.textPrecipitation.setText(precip != null ? String.format("%.1f %s", precip(precip[0]), precipUnit()) : "-- " + precipUnit());
        binding.textHumidity.setText(humid != null ? String.format("%.0f%%", avg(humid, 12)) : "-- %");

        // UV Index
        float[] uvMax = retriever.getDailyUVIndexMax();
        float uvToday = safeFirst(uvMax);
        binding.textUVIndex.setText(fmt1(uvToday));
        binding.textUVLabel.setText(uvLabel(uvToday));
        binding.textUVIndex.setTextColor(uvColor(uvToday));

        // Feels Like
        float feelsLike = retriever.getCurrentFeelsLike();
        binding.textFeelsLike.setText(String.format("%.1f%s", temp(feelsLike), tempUnit()));

        // Evapotranspiration
        float[] et0 = retriever.getDailyEvapotranspiration();
        float etToday = safeFirst(et0);
        binding.textEvapotranspiration.setText(String.format("%.1f mm/day", etToday));
        binding.textETLevel.setText(etLabel(etToday));

        // Temperature card
        binding.cardTemperature.setOnClickListener(v -> {
            String[] labels = new String[max != null ? max.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(temp(max[i])) + "° / " + fmt0(temp(min != null ? min[i] : 0)) + "°";
            }
            showDetailSheet("🌡", "Temperature",
                String.format("%.1f%s", temp(retriever.getCurrentTemp()), tempUnit()),
                new String[]{"Today High", "Today Low", "7-Day Avg"},
                new String[]{
                    fmt0(temp(safeFirst(max))) + tempUnit(),
                    fmt0(temp(safeFirst(min))) + tempUnit(),
                    fmt1(temp(average(max))) + tempUnit()
                },
                labels, values, "High / Low per day");
        });

        // Wind card
        binding.cardWind.setOnClickListener(v -> {
            String[] labels = new String[windMax != null ? windMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(wind(windMax[i])) + " " + windUnit();
            }
            float currentWindConverted = wind(retriever.getCurrentWind());
            String note = currentWindConverted > (useMetric ? 40 : 25) ? "⚠️ Strong winds today" : "Calm conditions";
            showDetailSheet("💨", "Wind Speed",
                String.format("%.1f %s", wind(retriever.getCurrentWind()), windUnit()),
                new String[]{"Max Today", "7-Day Avg", "Conditions"},
                new String[]{
                    fmt0(wind(safeFirst(windMax))) + " " + windUnit(),
                    fmt1(wind(average(windMax))) + " " + windUnit(),
                    note
                },
                labels, values, "Daily max wind speed");
        });

        // Precipitation card
        float precipToday = precip(safeFirst(precip));
        binding.cardPrecipitation.setOnClickListener(v -> {
            String[] labels = new String[precip != null ? precip.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt1(precip(precip[i])) + " " + precipUnit();
            }
            float[] convertedPrecip = precip != null ? convertArray(precip, false) : null;
            showDetailSheet("🌧", "Precipitation",
                String.format("%.1f %s", precipToday, precipUnit()),
                new String[]{"Peak Day", "7-Day Total", "Daily Avg"},
                new String[]{
                    fmt1(max(convertedPrecip)) + " " + precipUnit(),
                    fmt1(sum(convertedPrecip)) + " " + precipUnit(),
                    fmt1(average(convertedPrecip)) + " " + precipUnit()
                },
                labels, values, "Daily precipitation totals");
        });

        // UV Index card
        binding.cardUV.setOnClickListener(v -> {
            String[] labels = new String[uvMax != null ? uvMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt1(uvMax[i]) + "  " + uvLabel(uvMax[i]);
            }
            showDetailSheet("☀️", "UV Index",
                fmt1(uvToday) + " — " + uvLabel(uvToday),
                new String[]{"Today", "Peak (7-day)", "Risk"},
                new String[]{
                    fmt1(uvToday),
                    fmt1(max(uvMax)),
                    uvToday >= 6 ? "Protect skin & eyes" : uvToday >= 3 ? "Wear sunscreen" : "Low risk"
                },
                labels, values, "Daily UV index max");
        });

        // Feels Like card
        float[] feelsMax = retriever.getDailyApparentTempMax();
        binding.cardFeelsLike.setOnClickListener(v -> {
            String[] labels = new String[feelsMax != null ? feelsMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(temp(feelsMax[i])) + tempUnit();
            }
            float diff = temp(feelsLike) - temp(retriever.getCurrentTemp());
            String sign = diff >= 0 ? "+" : "";
            showDetailSheet("🌡", "Feels Like",
                String.format("%.1f%s", temp(feelsLike), tempUnit()),
                new String[]{"vs. Actual", "High (today)", "7-Day Avg Feels"},
                new String[]{
                    sign + fmt1(diff) + tempUnit(),
                    feelsMax != null && feelsMax.length > 0 ? fmt0(temp(feelsMax[0])) + tempUnit() : "--",
                    fmt1(temp(average(feelsMax))) + tempUnit()
                },
                labels, values, "Daily apparent temperature max");
        });

        // Evapotranspiration card
        binding.cardEvapotranspiration.setOnClickListener(v -> {
            String[] labels = new String[et0 != null ? et0.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt1(et0[i]) + " mm";
            }
            showDetailSheet("🌱", "Water Stress (ET₀)",
                fmt1(etToday) + " mm/day",
                new String[]{"Level", "7-Day Avg", "Weekly Total"},
                new String[]{
                    etLabel(etToday),
                    fmt1(average(et0)) + " mm/day",
                    fmt1(sum(et0)) + " mm"
                },
                labels, values,
                "Evapotranspiration measures how much water the land loses to atmosphere.\n" +
                "Higher values signal drought stress and intensify with climate warming.");
        });

        // Humidity card (no conversion — % is universal)
        float humidAvg = humid != null ? avg(humid, 12) : 0;
        binding.cardHumidity.setOnClickListener(v -> {
            int show = humid != null ? Math.min(24, humid.length) : 0;
            String[] labels = new String[show];
            String[] values = new String[show];
            for (int i = 0; i < show; i++) {
                labels[i] = String.format("%02d:00", i);
                values[i] = fmt0(humid[i]) + "%";
            }
            String feel = humidAvg > 70 ? "High — may feel muggy" : "Comfortable range";
            showDetailSheet("💧", "Humidity",
                String.format("%.0f%%", humidAvg),
                new String[]{"Peak", "Low", "Feel"},
                new String[]{fmt0(max(humid)) + "%", fmt0(min(humid)) + "%", feel},
                labels, values, "Hourly relative humidity");
        });
    }

    /** Builds and populates the horizontal 7-day forecast chip strip. */
    private void bindDailyStrip(EcoDataRetriever retriever) {
        binding.dailyForecastStrip.removeAllViews();
        float[] max    = retriever.getDailyTempMax();
        float[] min    = retriever.getDailyTempMin();
        float[] precip = retriever.getDailyPrecip();
        if (max == null || min == null) return;

        for (int i = 0; i < max.length; i++) {
            float hiConverted = temp(max[i]);
            float loConverted = temp(min[i]);
            boolean rainy = precip != null && i < precip.length && precip[i] > 1f;
            LinearLayout chip = makeDayChip(i, hiConverted, loConverted, rainy);

            final int day = i;
            final float hi = hiConverted;
            final float lo = loConverted;
            final float pr = precip(precip != null && i < precip.length ? precip[i] : 0);
            chip.setOnClickListener(v -> {
                if (!isAdded() || getContext() == null) return;
                showDetailSheet(
                    "📅", dayLabel(day),
                    fmt0(hi) + tempUnit(),
                    new String[]{"High", "Low", "Precip."},
                    new String[]{fmt0(hi) + tempUnit(), fmt0(lo) + tempUnit(), fmt1(pr) + " " + precipUnit()},
                    new String[0], new String[0],
                    conditionLabel((int) retriever.getCurrentWeatherCode())
                );
            });
            binding.dailyForecastStrip.addView(chip);
        }
    }

    /** Populates the climate anomaly card using historical ERA5 data from the retriever. */
    private void bindClimate(EcoDataRetriever retriever) {
        ClimateData climate = retriever.ecoDatabase.getLatestClimate();
        if (climate == null) { binding.cardClimate.setAlpha(0.4f); return; }

        float avgHigh     = temp(average(climate.dailyTempMax));
        float avgLow      = temp(average(climate.dailyTempMin));
        float totalPrecip = precip(sum(climate.dailyPrecipitation));
        float anomaly     = temp(retriever.getCurrentTemp()) - avgHigh;
        String sign       = anomaly >= 0 ? "+" : "";

        binding.cardClimate.setAlpha(1f);
        binding.textClimateAvgHigh.setText(fmt1(avgHigh) + tempUnit());
        binding.textClimateAvgLow.setText(fmt1(avgLow) + tempUnit());
        binding.textClimateAnomaly.setText(sign + fmt1(anomaly) + tempUnit());

        binding.cardClimate.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;
            showClimateSheet(climate, retriever.getCurrentTemp());
        });
    }

    /**
     * Shows a bottom sheet with a large value, three stat chips, and an optional scrollable data table.
     *
     * @param emoji      decorative icon shown next to the title
     * @param title      card title
     * @param bigValue   prominent value displayed in large text
     * @param chipLabels labels for the three summary chips
     * @param chipValues values for the three summary chips
     * @param rowLabels  day/hour labels for the data table rows (empty to hide table)
     * @param rowValues  corresponding values for the data table rows
     * @param tableNote  caption shown above the data table
     */
    private void showDetailSheet(String emoji, String title, String bigValue,
                                 String[] chipLabels, String[] chipValues,
                                 String[] rowLabels, String[] rowValues,
                                 String tableNote) {
        if (!isAdded() || getContext() == null) return;
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());

        android.widget.ScrollView scroll = new android.widget.ScrollView(getContext());
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(8), dp(20), dp(40));
        root.setBackgroundColor(0xFFF8FBF8);
        scroll.addView(root);

        // Drag handle
        View handle = new View(getContext());
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(dp(40), dp(4));
        hlp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        hlp.setMargins(0, dp(10), 0, dp(16));
        handle.setLayoutParams(hlp);
        handle.post(() -> {
            android.graphics.drawable.GradientDrawable rd = new android.graphics.drawable.GradientDrawable();
            rd.setColor(0xFFCCCCCC);
            rd.setCornerRadius(dp(4));
            handle.setBackground(rd);
        });
        root.addView(handle);

        // Header
        LinearLayout header = new LinearLayout(getContext());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams hlp2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hlp2.setMargins(0, 0, 0, dp(4));
        header.setLayoutParams(hlp2);

        TextView emojiView = makeText(emoji, 28, "#000000", false);
        LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        elp.setMargins(0, 0, dp(12), 0);
        emojiView.setLayoutParams(elp);
        header.addView(emojiView);
        header.addView(makeText(title, 18, "#1A3D1C", true));
        root.addView(header);

        root.addView(makeText(bigValue, 48, "#1A3D1C", false));
        root.addView(sheetDivider());

        // 3 stat chips
        LinearLayout chips = new LinearLayout(getContext());
        chips.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, dp(16));
        chips.setLayoutParams(clp);

        for (int i = 0; i < Math.min(3, chipLabels.length); i++) {
            LinearLayout chip = new LinearLayout(getContext());
            chip.setOrientation(LinearLayout.VERTICAL);
            chip.setGravity(android.view.Gravity.CENTER);
            chip.setPadding(dp(8), dp(10), dp(8), dp(10));
            LinearLayout.LayoutParams clp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if (i < 2) clp2.setMargins(0, 0, dp(8), 0);
            chip.setLayoutParams(clp2);
            android.graphics.drawable.GradientDrawable chipBg = new android.graphics.drawable.GradientDrawable();
            chipBg.setColor(0xFFEAF3EA);
            chipBg.setCornerRadius(dp(8));
            chip.setBackground(chipBg);
            chip.addView(makeText(chipValues[i], 16, "#1A3D1C", true));
            chip.addView(makeText(chipLabels[i], 10, "#7A9E7C", false));
            chips.addView(chip);
        }
        root.addView(chips);

        // Scrollable data table
        if (rowLabels.length > 0) {
            root.addView(makeText(tableNote, 12, "#7A9E7C", true));
            root.addView(sheetDivider());
            for (int i = 0; i < rowLabels.length; i++) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rlp.setMargins(0, 0, 0, dp(2));
                row.setLayoutParams(rlp);
                row.setPadding(dp(4), dp(8), dp(4), dp(8));
                if (i % 2 == 0) row.setBackgroundColor(0x08000000);

                TextView labelTv = makeText(rowLabels[i], 13, "#444444", false);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                labelTv.setLayoutParams(llp);
                row.addView(labelTv);
                row.addView(makeText(rowValues[i], 13, "#1A3D1C", true));
                root.addView(row);
            }
        }

        sheet.setContentView(scroll);
        sheet.show();
    }

    /**
     * Shows a bottom sheet with historical climate averages and a temperature anomaly banner.
     *
     * @param currentTempCelsius today's actual temperature in Celsius used to calculate the anomaly
     */
    private void showClimateSheet(ClimateData climate, float currentTempCelsius) {
        if (!isAdded() || getContext() == null) return;
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());

        android.widget.ScrollView scroll = new android.widget.ScrollView(getContext());
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(8), dp(20), dp(40));
        root.setBackgroundColor(0xFFF8FBF8);
        scroll.addView(root);

        float avgHigh     = temp(average(climate.dailyTempMax));
        float avgLow      = temp(average(climate.dailyTempMin));
        float totalPrecip = precip(sum(climate.dailyPrecipitation));
        float anomaly     = temp(currentTempCelsius) - avgHigh;
        String sign       = anomaly >= 0 ? "+" : "";
        int anomalyColor  = anomaly > 0 ? 0xFFC0392B : 0xFF2980B9;

        // Drag handle
        View handle = new View(getContext());
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(dp(40), dp(4));
        hlp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        hlp.setMargins(0, dp(10), 0, dp(16));
        handle.setLayoutParams(hlp);
        handle.setBackgroundColor(0xFFCCCCCC);
        root.addView(handle);

        root.addView(makeText("🌍  Climate Overview", 18, "#1A3D1C", true));
        root.addView(makeText("Historical daily averages", 12, "#7A9E7C", false));
        root.addView(sheetDivider());

        // Anomaly banner
        LinearLayout banner = new LinearLayout(getContext());
        banner.setOrientation(LinearLayout.HORIZONTAL);
        banner.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        blp.setMargins(0, 0, 0, dp(16));
        banner.setLayoutParams(blp);
        banner.setPadding(dp(16), dp(12), dp(16), dp(12));
        android.graphics.drawable.GradientDrawable bannerBg = new android.graphics.drawable.GradientDrawable();
        bannerBg.setColor(anomaly > 0 ? 0x15C0392B : 0x152980B9);
        bannerBg.setCornerRadius(dp(8));
        banner.setBackground(bannerBg);

        LinearLayout bannerText = new LinearLayout(getContext());
        bannerText.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams btlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        bannerText.setLayoutParams(btlp);
        bannerText.addView(makeText("Temperature Anomaly", 11, "#7A9E7C", false));
        TextView anomalyTv = makeText(sign + fmt1(anomaly) + tempUnit() + " vs historical avg", 15, "#1A3D1C", true);
        anomalyTv.setTextColor(anomalyColor);
        bannerText.addView(anomalyTv);
        bannerText.addView(makeText(
            anomaly > 2  ? "Notably warmer than historical average" :
            anomaly < -2 ? "Notably cooler than historical average" :
                           "Near the historical average",
            11, "#555555", false));
        banner.addView(bannerText);

        TextView anomalyBig = makeText(sign + fmt0(anomaly) + "°", 32, "#1A3D1C", false);
        anomalyBig.setTextColor(anomalyColor);
        banner.addView(anomalyBig);
        root.addView(banner);

        // 3 stat chips
        LinearLayout chips = new LinearLayout(getContext());
        chips.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, dp(16));
        chips.setLayoutParams(clp);

        String[][] chipData = {
            {fmt1(avgHigh) + tempUnit(), "Avg High"},
            {fmt1(avgLow)  + tempUnit(), "Avg Low"},
            {fmt0(totalPrecip) + " " + precipUnit(), "Total Precip"}
        };
        for (int i = 0; i < chipData.length; i++) {
            LinearLayout chip = new LinearLayout(getContext());
            chip.setOrientation(LinearLayout.VERTICAL);
            chip.setGravity(android.view.Gravity.CENTER);
            chip.setPadding(dp(8), dp(10), dp(8), dp(10));
            LinearLayout.LayoutParams clp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if (i < 2) clp2.setMargins(0, 0, dp(8), 0);
            chip.setLayoutParams(clp2);
            android.graphics.drawable.GradientDrawable chipBg = new android.graphics.drawable.GradientDrawable();
            chipBg.setColor(0xFFEAF3EA);
            chipBg.setCornerRadius(dp(8));
            chip.setBackground(chipBg);
            chip.addView(makeText(chipData[i][0], 16, "#1A3D1C", true));
            chip.addView(makeText(chipData[i][1], 10, "#7A9E7C", false));
            chips.addView(chip);
        }
        root.addView(chips);

        // Daily breakdown table
        root.addView(makeText("Daily Historical Data", 12, "#7A9E7C", true));
        root.addView(sheetDivider());

        LinearLayout colHeader = new LinearLayout(getContext());
        colHeader.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams chlp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        chlp.setMargins(0, 0, 0, dp(4));
        colHeader.setLayoutParams(chlp);
        colHeader.setPadding(dp(4), dp(4), dp(4), dp(4));
        colHeader.setBackgroundColor(0xFFEAF3EA);

        TextView h1 = makeText("Day", 11, "#7A9E7C", true);
        h1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f));
        TextView h2 = makeText("High", 11, "#7A9E7C", true);
        h2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView h3 = makeText("Low", 11, "#7A9E7C", true);
        h3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView h4 = makeText("Precip", 11, "#7A9E7C", true);
        h4.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        colHeader.addView(h1); colHeader.addView(h2);
        colHeader.addView(h3); colHeader.addView(h4);
        root.addView(colHeader);

        int count = climate.dailyTempMax != null ? climate.dailyTempMax.length : 0;
        for (int i = 0; i < count; i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(dp(4), dp(9), dp(4), dp(9));
            if (i % 2 == 0) row.setBackgroundColor(0x08000000);

            float hi = temp(climate.dailyTempMax[i]);
            float lo = temp(climate.dailyTempMin != null ? climate.dailyTempMin[i] : 0);
            float pr = precip(climate.dailyPrecipitation != null ? climate.dailyPrecipitation[i] : 0);

            TextView dayTv = makeText("Day " + (i + 1), 13, "#444444", false);
            dayTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f));
            TextView hiTv = makeText(fmt0(hi) + tempUnit(), 13, "#C0392B", true);
            hiTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            TextView loTv = makeText(fmt0(lo) + tempUnit(), 13, "#2980B9", true);
            loTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            TextView prTv = makeText(fmt1(pr) + " " + precipUnit(), 13, "#1A3D1C", false);
            prTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            row.addView(dayTv); row.addView(hiTv);
            row.addView(loTv); row.addView(prTv);
            root.addView(row);
        }

        sheet.setContentView(scroll);
        sheet.show();
    }

    /** Thin horizontal divider used inside detail sheets. */
    private View sheetDivider() {
        View v = new View(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0, dp(8), 0, dp(12));
        v.setLayoutParams(lp);
        v.setBackgroundColor(0xFFE0E8E0);
        return v;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Creates a vertical chip view for the daily forecast strip showing day label, weather icon, and high/low temps. */
    private LinearLayout makeDayChip(int dayIndex, float hi, float lo, boolean rainy) {
        if (getContext() == null) return new LinearLayout(requireActivity());
        LinearLayout chip = new LinearLayout(getContext());
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(android.view.Gravity.CENTER);
        chip.setBackgroundResource(R.drawable.card_background);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(68), dp(105));
        lp.setMargins(0, 0, dp(8), 0);
        chip.setLayoutParams(lp);
        chip.setPadding(dp(8), dp(10), dp(8), dp(10));

        chip.addView(makeText(dayIndex == 0 ? "Today" : dayLabel(dayIndex), 10, "#7A9E7C", true));
        chip.addView(makeText(rainy ? "🌧" : "🌤", 22, "#000000", false));
        chip.addView(makeText(fmt0(hi) + "°", 14, "#1A3D1C", true));
        chip.addView(makeText(fmt0(lo) + "°", 12, "#AAAAAA", false));
        return chip;
    }

    /** Creates a styled TextView for use in programmatically built sheet layouts. */
    private TextView makeText(String text, int spSize, String hexColor, boolean bold) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(spSize);
        tv.setTextColor(android.graphics.Color.parseColor(hexColor));
        tv.setTypeface(bold
            ? android.graphics.Typeface.DEFAULT_BOLD
            : android.graphics.Typeface.DEFAULT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(4));
        tv.setLayoutParams(lp);
        return tv;
    }

    /** Converts dp to pixels using the current display metrics. */
    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    /** Returns "Today" for offset 0, or a short date string (e.g. "Mon 5/12") for future days. */
    private String dayLabel(int offset) {
        return offset == 0 ? "Today"
            : LocalDate.now().plusDays(offset).format(DateTimeFormatter.ofPattern("EEE M/d"));
    }

    /** Maps a UV index value to a human-readable risk label (Low → Extreme). */
    private String uvLabel(float uv) {
        if (uv >= 11) return "Extreme";
        if (uv >= 8)  return "Very High";
        if (uv >= 6)  return "High";
        if (uv >= 3)  return "Moderate";
        return "Low";
    }

    /** Maps a UV index value to a color (green = low, yellow, orange, red, purple = extreme). */
    private int uvColor(float uv) {
        if (uv >= 11) return android.graphics.Color.parseColor("#7B1FA2"); // purple
        if (uv >= 8)  return android.graphics.Color.parseColor("#C62828"); // red
        if (uv >= 6)  return android.graphics.Color.parseColor("#E65100"); // orange
        if (uv >= 3)  return android.graphics.Color.parseColor("#F9A825"); // yellow
        return android.graphics.Color.parseColor("#2E7D32");               // green
    }

    /** Maps an ET₀ value (mm/day) to a water-stress label. */
    private String etLabel(float et) {
        if (et >= 6)  return "Severe drought stress";
        if (et >= 4)  return "High water stress";
        if (et >= 2)  return "Moderate evaporation";
        return "Low water stress";
    }

    /** Maps a WMO weather code to a short English condition label (e.g. "Rain", "Clear sky"). */
    private String conditionLabel(int code) {
        if (code == 0)  return "Clear sky";
        if (code <= 2)  return "Partly cloudy";
        if (code == 3)  return "Overcast";
        if (code <= 49) return "Foggy";
        if (code <= 59) return "Drizzle";
        if (code <= 69) return "Rain";
        if (code <= 79) return "Snow";
        if (code <= 82) return "Rain showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }

    /** Maps a WMO weather code to a representative emoji character. */
    private String conditionEmoji(int code) {
        if (code == 0)  return "☀️";
        if (code <= 2)  return "⛅";
        if (code == 3)  return "☁️";
        if (code <= 49) return "🌫";
        if (code <= 69) return "🌧";
        if (code <= 79) return "❄️";
        if (code <= 99) return "⛈";
        return "🌡";
    }

    // ── Math helpers ──────────────────────────────────────────────────────────

    private float safeFirst(float[] a) { return (a != null && a.length > 0) ? a[0] : 0f; }
    private float average(float[] a)   { return a != null ? avg(a, a.length) : 0f; }
    private float avg(float[] a, int n) {
        if (a == null || a.length == 0 || n == 0) return 0f;
        float s = 0; int c = Math.min(n, a.length);
        for (int i = 0; i < c; i++) s += a[i];
        return s / c;
    }
    private float max(float[] a) {
        if (a == null || a.length == 0) return 0f;
        float m = a[0]; for (float v : a) if (v > m) m = v; return m;
    }
    private float min(float[] a) {
        if (a == null || a.length == 0) return 0f;
        float m = a[0]; for (float v : a) if (v < m) m = v; return m;
    }
    private float sum(float[] a) {
        if (a == null) return 0f;
        float s = 0; for (float v : a) s += v; return s;
    }

    /** Converts a raw metric float array for use in stats (max/sum/avg). */
    private float[] convertArray(float[] src, boolean isTemp) {
        if (src == null) return null;
        float[] out = new float[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = isTemp ? temp(src[i]) : precip(src[i]);
        }
        return out;
    }

    private String fmt0(float v) { return String.format("%.0f", v); }
    private String fmt1(float v) { return String.format("%.1f", v); }
}
