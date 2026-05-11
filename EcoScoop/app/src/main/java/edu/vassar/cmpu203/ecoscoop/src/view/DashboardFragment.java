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

public class DashboardFragment extends Fragment implements DashboardUI {

    private FragmentDashboardBinding binding;
    private DashboardUI.Listener listener;
    private EcoDataRetriever pendingRetriever;

    // ── Lifecycle (unchanged) ─────────────────────────────────────────────────

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

    // ── DashboardUI ───────────────────────────────────────────────────────────

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

    // ── Bind methods ──────────────────────────────────────────────────────────

    private void bindHeader(EcoDataRetriever retriever) {
        int code = (int) retriever.getCurrentWeatherCode();
        float[] max = retriever.getDailyTempMax();
        float[] min = retriever.getDailyTempMin();

        binding.textHeroTemp.setText(String.format("%.0f°", retriever.getCurrentTemp()));
        binding.textHeroCondition.setText(conditionLabel(code));
        binding.textHeroIcon.setText(conditionEmoji(code));

        if (max != null && min != null && max.length > 0)
            binding.textHeroHighLow.setText(
                String.format("H: %.0f°  L: %.0f°", max[0], min[0]));
    }

    private void bindCards(EcoDataRetriever retriever) {
        float[] precip = retriever.getDailyPrecip();
        float[] humid  = retriever.getHourlyHumidity();
        float[] max    = retriever.getDailyTempMax();
        float[] min    = retriever.getDailyTempMin();
        float[] windMax = retriever.getDailyWindMax();

        binding.textCurrentTemp.setText(String.format("%.1f°C", retriever.getCurrentTemp()));
        binding.textCurrentWind.setText(String.format("%.1f km/h", retriever.getCurrentWind()));
        binding.textPrecipitation.setText(precip != null ? String.format("%.1f mm", precip[0]) : "-- mm");
        binding.textHumidity.setText(humid != null ? String.format("%.0f%%", avg(humid, 12)) : "-- %");

        // Card click → rich detail sheet
        binding.cardTemperature.setOnClickListener(v -> {
            String[] labels = new String[max != null ? max.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(max[i]) + "° / " + fmt0(min != null ? min[i] : 0) + "°";
            }
            showDetailSheet("🌡", "Temperature",
                String.format("%.1f°C", retriever.getCurrentTemp()),
                new String[]{"Today High", "Today Low", "7-Day Avg"},
                new String[]{fmt0(safeFirst(max)) + "°C", fmt0(safeFirst(min)) + "°C", fmt1(average(max)) + "°C"},
                labels, values, "High / Low per day");
        });

        binding.cardWind.setOnClickListener(v -> {
            String[] labels = new String[windMax != null ? windMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(windMax[i]) + " km/h";
            }
            String note = retriever.getCurrentWind() > 40 ? "⚠️ Strong winds today" : "Calm conditions";
            showDetailSheet("💨", "Wind Speed",
                String.format("%.1f km/h", retriever.getCurrentWind()),
                new String[]{"Max Today", "7-Day Avg", "Conditions"},
                new String[]{fmt0(safeFirst(windMax)) + " km/h", fmt1(average(windMax)) + " km/h", note},
                labels, values, "Daily max wind speed");
        });

        float precipToday = safeFirst(precip);
        binding.cardPrecipitation.setOnClickListener(v -> {
            String[] labels = new String[precip != null ? precip.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt1(precip[i]) + " mm";
            }
            showDetailSheet("🌧", "Precipitation",
                String.format("%.1f mm", precipToday),
                new String[]{"Peak Day", "7-Day Total", "Daily Avg"},
                new String[]{fmt1(max(precip)) + " mm", fmt1(sum(precip)) + " mm", fmt1(average(precip)) + " mm"},
                labels, values, "Daily precipitation totals");
        });

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

    private void bindDailyStrip(EcoDataRetriever retriever) {
        binding.dailyForecastStrip.removeAllViews();
        float[] max    = retriever.getDailyTempMax();
        float[] min    = retriever.getDailyTempMin();
        float[] precip = retriever.getDailyPrecip();
        if (max == null || min == null) return;

        for (int i = 0; i < max.length; i++) {
            // Build each day chip inline — no separate layout file needed
            LinearLayout chip = makeDayChip(i, max[i], min[i],
                precip != null && i < precip.length && precip[i] > 1f);

            final int day = i;
            final float hi = max[i], lo = (min != null ? min[i] : 0),
                        pr = (precip != null && i < precip.length ? precip[i] : 0);
            chip.setOnClickListener(v -> showDetailSheet(
                "📅", dayLabel(day),
                fmt0(hi) + "°C",
                new String[]{"High", "Low", "Precip."},
                new String[]{fmt0(hi) + "°C", fmt0(lo) + "°C", fmt1(pr) + " mm"},
                new String[0], new String[0],
                conditionLabel((int) retriever.getCurrentWeatherCode())
            ));
            binding.dailyForecastStrip.addView(chip);
        }
    }

    private void bindClimate(EcoDataRetriever retriever) {
        ClimateData climate = retriever.ecoDatabase.getLatestClimate();
        if (climate == null) { binding.cardClimate.setAlpha(0.4f); return; }

        // Compute summaries from the raw daily arrays ClimateData actually provides
        float avgHigh     = average(climate.dailyTempMax);
        float avgLow      = average(climate.dailyTempMin);
        float totalPrecip = sum(climate.dailyPrecipitation);
        float anomaly     = retriever.getCurrentTemp() - avgHigh;
        String sign       = anomaly >= 0 ? "+" : "";

        binding.cardClimate.setAlpha(1f);
        binding.textClimateAvgHigh.setText(fmt1(avgHigh) + "°C");
        binding.textClimateAvgLow.setText(fmt1(avgLow) + "°C");
        binding.textClimateAnomaly.setText(sign + fmt1(anomaly) + "°C");

        binding.cardClimate.setOnClickListener(v -> showClimateSheet(climate, retriever.getCurrentTemp()));
    }

    // ── Detail sheets ─────────────────────────────────────────────────────────

    /**
     * Generic detail sheet used by all weather cards.
     *
     * @param emoji       Large icon at the top
     * @param title       Card name
     * @param bigValue    The headline number shown large
     * @param chipLabels  3 summary chip labels
     * @param chipValues  3 summary chip values (parallel to chipLabels)
     * @param rowLabels   Left column of the scrollable data table (e.g. day names)
     * @param rowValues   Right column of the scrollable data table
     * @param tableNote   Small header above the table
     */
    private void showDetailSheet(String emoji, String title, String bigValue,
                                 String[] chipLabels, String[] chipValues,
                                 String[] rowLabels, String[] rowValues,
                                 String tableNote) {
        if (!isAdded() || getContext() == null) return;
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());

        // Root scroll so very long tables don't overflow the screen
        android.widget.ScrollView scroll = new android.widget.ScrollView(getContext());
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(8), dp(20), dp(40));
        root.setBackgroundColor(0xFFF8FBF8);
        scroll.addView(root);

        // ── Drag handle ──────────────────────────────────────────────────────
        View handle = new View(getContext());
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(dp(40), dp(4));
        hlp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        hlp.setMargins(0, dp(10), 0, dp(16));
        handle.setLayoutParams(hlp);
        handle.setBackgroundColor(0xFFCCCCCC);
        handle.post(() -> {
            android.graphics.drawable.GradientDrawable rd = new android.graphics.drawable.GradientDrawable();
            rd.setColor(0xFFCCCCCC);
            rd.setCornerRadius(dp(4));
            handle.setBackground(rd);
        });
        root.addView(handle);

        // ── Header: emoji + title ────────────────────────────────────────────
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

        // ── Big value ────────────────────────────────────────────────────────
        root.addView(makeText(bigValue, 48, "#1A3D1C", false));
        root.addView(sheetDivider());

        // ── 3 stat chips ─────────────────────────────────────────────────────
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

        // ── Scrollable data table ─────────────────────────────────────────────
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
                // Alternate row tint
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
     * Dedicated climate detail sheet — shows anomaly context and the full
     * historical daily breakdown from ClimateData arrays.
     */
    private void showClimateSheet(ClimateData climate, float currentTemp) {
        if (!isAdded() || getContext() == null) return;
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());

        android.widget.ScrollView scroll = new android.widget.ScrollView(getContext());
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(8), dp(20), dp(40));
        root.setBackgroundColor(0xFFF8FBF8);
        scroll.addView(root);

        // Derived values
        float avgHigh     = average(climate.dailyTempMax);
        float avgLow      = average(climate.dailyTempMin);
        float totalPrecip = sum(climate.dailyPrecipitation);
        float anomaly     = currentTemp - avgHigh;
        String sign       = anomaly >= 0 ? "+" : "";
        int anomalyColor  = anomaly > 0 ? 0xFFC0392B : 0xFF2980B9;

        // ── Drag handle ──────────────────────────────────────────────────────
        View handle = new View(getContext());
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(dp(40), dp(4));
        hlp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        hlp.setMargins(0, dp(10), 0, dp(16));
        handle.setLayoutParams(hlp);
        handle.setBackgroundColor(0xFFCCCCCC);
        root.addView(handle);

        // ── Header ───────────────────────────────────────────────────────────
        root.addView(makeText("🌍  Climate Overview", 18, "#1A3D1C", true));
        root.addView(makeText("Historical daily averages", 12, "#7A9E7C", false));
        root.addView(sheetDivider());

        // ── Anomaly banner ────────────────────────────────────────────────────
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
        TextView anomalyTv = makeText(sign + fmt1(anomaly) + "°C vs historical avg", 15, "#1A3D1C", true);
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

        // ── 3 stat chips ──────────────────────────────────────────────────────
        LinearLayout chips = new LinearLayout(getContext());
        chips.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, dp(16));
        chips.setLayoutParams(clp);

        String[][] chipData = {
            {fmt1(avgHigh) + "°C", "Avg High"},
            {fmt1(avgLow)  + "°C", "Avg Low"},
            {fmt0(totalPrecip) + " mm", "Total Precip"}
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

        // ── Daily breakdown table ────────────────────────────────────────────
        root.addView(makeText("Daily Historical Data", 12, "#7A9E7C", true));
        root.addView(sheetDivider());

        // Column headers
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

            float hi  = climate.dailyTempMax[i];
            float lo  = climate.dailyTempMin != null ? climate.dailyTempMin[i] : 0;
            float pr  = climate.dailyPrecipitation != null ? climate.dailyPrecipitation[i] : 0;

            TextView dayTv = makeText("Day " + (i + 1), 13, "#444444", false);
            dayTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f));
            TextView hiTv = makeText(fmt0(hi) + "°C", 13, "#C0392B", true);
            hiTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            TextView loTv = makeText(fmt0(lo) + "°C", 13, "#2980B9", true);
            loTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            TextView prTv = makeText(fmt1(pr) + " mm", 13, "#1A3D1C", false);
            prTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            row.addView(dayTv); row.addView(hiTv);
            row.addView(loTv); row.addView(prTv);
            root.addView(row);
        }

        sheet.setContentView(scroll);
        sheet.show();
    }

    /** Thin horizontal divider used inside detail sheets */
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

    /** Builds a day chip view inline — replaces item_day_forecast.xml */
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

    /** Creates a TextView with the given properties */
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

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    private String dayLabel(int offset) {
        return offset == 0 ? "Today"
            : LocalDate.now().plusDays(offset).format(DateTimeFormatter.ofPattern("EEE M/d"));
    }

    private String conditionLabel(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 2) return "Partly cloudy";
        if (code == 3) return "Overcast";
        if (code <= 49) return "Foggy";
        if (code <= 59) return "Drizzle";
        if (code <= 69) return "Rain";
        if (code <= 79) return "Snow";
        if (code <= 82) return "Rain showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }

    private String conditionEmoji(int code) {
        if (code == 0) return "☀️";
        if (code <= 2) return "⛅";
        if (code == 3) return "☁️";
        if (code <= 49) return "🌫";
        if (code <= 69) return "🌧";
        if (code <= 79) return "❄️";
        if (code <= 99) return "⛈";
        return "🌡";
    }

    // ── Math helpers ──────────────────────────────────────────────────────────

    private float safeFirst(float[] a) { return (a != null && a.length > 0) ? a[0] : 0f; }
    private float average(float[] a)   { return avg(a, a != null ? a.length : 0); }
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
    private String fmt0(float v) { return String.format("%.0f", v); }
    private String fmt1(float v) { return String.format("%.1f", v); }
}
