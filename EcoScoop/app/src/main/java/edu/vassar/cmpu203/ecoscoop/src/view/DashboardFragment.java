package edu.vassar.cmpu203.ecoscoop.src.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import edu.vassar.cmpu203.ecoscoop.R;
import edu.vassar.cmpu203.ecoscoop.databinding.FragmentDashboardBinding;
import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataRetriever;
import edu.vassar.cmpu203.ecoscoop.src.model.ClimateData;

/**
 * Fragment that displays current weather conditions, a 7-day forecast strip, weather metric cards
 * (UV, wind, humidity, ET₀, feels-like), and a historical climate anomaly panel.
 * Tapping any card opens a {@link DetailSheetFragment} or {@link ClimateSheetFragment}.
 */
public class DashboardFragment extends Fragment implements DashboardUI {

    private FragmentDashboardBinding binding;
    private DashboardUI.Listener listener;
    private EcoDataRetriever pendingRetriever;
    private boolean useMetric = true;

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

    public void setUseMetric(boolean useMetric) {
        this.useMetric = useMetric;
    }

    // Unit Conversion Settings Helper

    private float temp(float celsius) {
        return useMetric ? celsius : celsius * 9f / 5f + 32f;
    }

    private String tempUnit() { return useMetric ? "°C" : "°F"; }

    private float wind(float kmh) {
        return useMetric ? kmh : kmh * 0.621371f;
    }

    private String windUnit() { return useMetric ? "km/h" : "mph"; }

    private float precip(float mm) {
        return useMetric ? mm : mm * 0.0393701f;
    }

    private String precipUnit() { return useMetric ? "mm" : "in"; }

    // Bind methods

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
    }

    public void setLocationLabel(String label) {
        if (binding != null) binding.textLocationName.setText(label);
    }

    private void bindCards(EcoDataRetriever retriever) {
        float[] precip  = retriever.getDailyPrecip();
        float[] humid   = retriever.getHourlyHumidity();
        float[] max     = retriever.getDailyTempMax();
        float[] min     = retriever.getDailyTempMin();
        float[] windMax = retriever.getDailyWindMax();

        binding.textCurrentTemp.setText(String.format("%.1f%s", temp(retriever.getCurrentTemp()), tempUnit()));
        binding.textCurrentWind.setText(String.format("%.1f %s", wind(retriever.getCurrentWind()), windUnit()));
        binding.textPrecipitation.setText(precip != null
            ? String.format("%.1f %s", precip(precip[0]), precipUnit()) : "-- " + precipUnit());
        binding.textHumidity.setText(humid != null
            ? String.format("%.0f%%", avg(humid, 12)) : "-- %");

        float[] uvMax = retriever.getDailyUVIndexMax();
        float uvToday = safeFirst(uvMax);
        binding.textUVIndex.setText(fmt1(uvToday));
        binding.textUVLabel.setText(uvLabel(uvToday));
        binding.textUVIndex.setTextColor(uvColor(uvToday));

        float feelsLike = retriever.getCurrentFeelsLike();
        binding.textFeelsLike.setText(String.format("%.1f%s", temp(feelsLike), tempUnit()));

        float[] et0 = retriever.getDailyEvapotranspiration();
        float etToday = safeFirst(et0);
        binding.textEvapotranspiration.setText(String.format("%.1f mm/day", etToday));
        binding.textETLevel.setText(etLabel(etToday));

        float[] feelsMax = retriever.getDailyApparentTempMax();

        // Temperature card
        binding.cardTemperature.setOnClickListener(v -> {
            String[] labels = new String[max != null ? max.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(temp(max[i])) + "° / " + fmt0(temp(min != null ? min[i] : 0)) + "°";
            }
            DetailSheetFragment.newInstance(
                "🌡", "Temperature",
                String.format("%.1f%s", temp(retriever.getCurrentTemp()), tempUnit()),
                new String[]{"Today High", "Today Low", "7-Day Avg"},
                new String[]{
                    fmt0(temp(safeFirst(max))) + tempUnit(),
                    fmt0(temp(safeFirst(min))) + tempUnit(),
                    fmt1(temp(average(max))) + tempUnit()
                },
                labels, values, "High / Low per day"
            ).show(getParentFragmentManager(), "detail");
        });

        // Wind card
        binding.cardWind.setOnClickListener(v -> {
            String[] labels = new String[windMax != null ? windMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(wind(windMax[i])) + " " + windUnit();
            }
            String note = wind(retriever.getCurrentWind()) > (useMetric ? 40 : 25)
                ? "⚠️ Strong winds today" : "Calm conditions";
            DetailSheetFragment.newInstance(
                "💨", "Wind Speed",
                String.format("%.1f %s", wind(retriever.getCurrentWind()), windUnit()),
                new String[]{"Max Today", "7-Day Avg", "Conditions"},
                new String[]{
                    fmt0(wind(safeFirst(windMax))) + " " + windUnit(),
                    fmt1(wind(average(windMax))) + " " + windUnit(),
                    note
                },
                labels, values, "Daily max wind speed"
            ).show(getParentFragmentManager(), "detail");
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
            float[] cp = convertArray(precip, false);
            DetailSheetFragment.newInstance(
                "🌧", "Precipitation",
                String.format("%.1f %s", precipToday, precipUnit()),
                new String[]{"Peak Day", "7-Day Total", "Daily Avg"},
                new String[]{
                    fmt1(max(cp)) + " " + precipUnit(),
                    fmt1(sum(cp)) + " " + precipUnit(),
                    fmt1(average(cp)) + " " + precipUnit()
                },
                labels, values, "Daily precipitation totals"
            ).show(getParentFragmentManager(), "detail");
        });

        // UV Index card
        binding.cardUV.setOnClickListener(v -> {
            String[] labels = new String[uvMax != null ? uvMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt1(uvMax[i]) + "  " + uvLabel(uvMax[i]);
            }
            DetailSheetFragment.newInstance(
                "☀️", "UV Index",
                fmt1(uvToday) + " — " + uvLabel(uvToday),
                new String[]{"Today", "Peak (7-day)", "Risk"},
                new String[]{
                    fmt1(uvToday),
                    fmt1(max(uvMax)),
                    uvToday >= 6 ? "Protect skin & eyes" : uvToday >= 3 ? "Wear sunscreen" : "Low risk"
                },
                labels, values, "Daily UV index max"
            ).show(getParentFragmentManager(), "detail");
        });

        // Feels Like card
        binding.cardFeelsLike.setOnClickListener(v -> {
            String[] labels = new String[feelsMax != null ? feelsMax.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt0(temp(feelsMax[i])) + tempUnit();
            }
            float diff = temp(feelsLike) - temp(retriever.getCurrentTemp());
            String sign = diff >= 0 ? "+" : "";
            DetailSheetFragment.newInstance(
                "🌡", "Feels Like",
                String.format("%.1f%s", temp(feelsLike), tempUnit()),
                new String[]{"vs. Actual", "High (today)", "7-Day Avg Feels"},
                new String[]{
                    sign + fmt1(diff) + tempUnit(),
                    feelsMax != null && feelsMax.length > 0 ? fmt0(temp(feelsMax[0])) + tempUnit() : "--",
                    fmt1(temp(average(feelsMax))) + tempUnit()
                },
                labels, values, "Daily apparent temperature max"
            ).show(getParentFragmentManager(), "detail");
        });

        // Evapotranspiration card
        binding.cardEvapotranspiration.setOnClickListener(v -> {
            String[] labels = new String[et0 != null ? et0.length : 0];
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = dayLabel(i);
                values[i] = fmt1(et0[i]) + " mm";
            }
            DetailSheetFragment.newInstance(
                "🌱", "Water Stress (ET₀)",
                fmt1(etToday) + " mm/day",
                new String[]{"Level", "7-Day Avg", "Weekly Total"},
                new String[]{
                    etLabel(etToday),
                    fmt1(average(et0)) + " mm/day",
                    fmt1(sum(et0)) + " mm"
                },
                labels, values,
                "Evapotranspiration measures how much water the land loses to atmosphere.\n" +
                "Higher values signal drought stress and intensify with climate warming."
            ).show(getParentFragmentManager(), "detail");
        });

        // Humidity card
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
            DetailSheetFragment.newInstance(
                "💧", "Humidity",
                String.format("%.0f%%", humidAvg),
                new String[]{"Peak", "Low", "Feel"},
                new String[]{fmt0(max(humid)) + "%", fmt0(min(humid)) + "%", feel},
                labels, values, "Hourly relative humidity"
            ).show(getParentFragmentManager(), "detail");
        });
    }

    private void bindDailyStrip(EcoDataRetriever retriever) {
        binding.dailyForecastStrip.removeAllViews();
        float[] max    = retriever.getDailyTempMax();
        float[] min    = retriever.getDailyTempMin();
        float[] precip = retriever.getDailyPrecip();
        if (max == null || min == null) return;

        for (int i = 0; i < max.length; i++) {
            float hi = temp(max[i]);
            float lo = temp(min[i]);
            boolean rainy = precip != null && i < precip.length && precip[i] > 1f;
            LinearLayout chip = makeDayChip(i, hi, lo, rainy);

            final int day = i;
            final float fHi = hi;
            final float fLo = lo;
            final float fPr = precip(precip != null && i < precip.length ? precip[i] : 0);
            chip.setOnClickListener(v -> {
                if (!isAdded() || getContext() == null) return;
                DetailSheetFragment.newInstance(
                    "📅", dayLabel(day),
                    fmt0(fHi) + tempUnit(),
                    new String[]{"High", "Low", "Precip."},
                    new String[]{fmt0(fHi) + tempUnit(), fmt0(fLo) + tempUnit(), fmt1(fPr) + " " + precipUnit()},
                    new String[0], new String[0],
                    conditionLabel((int) retriever.getCurrentWeatherCode())
                ).show(getParentFragmentManager(), "detail");
            });
            binding.dailyForecastStrip.addView(chip);
        }
    }

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
            ClimateSheetFragment.newInstance(
                useMetric,
                retriever.getCurrentTemp(),
                climate.dailyTempMax,
                climate.dailyTempMin,
                climate.dailyPrecipitation
            ).show(getParentFragmentManager(), "climate");
        });
    }
    //  Helpers
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

        chip.addView(styledText(dayIndex == 0 ? "Today" : dayLabel(dayIndex), 10, "#7A9E7C", true));
        chip.addView(styledText(rainy ? "🌧" : "🌤", 22, "#000000", false));
        chip.addView(styledText(fmt0(hi) + "°", 14, "#1A3D1C", true));
        chip.addView(styledText(fmt0(lo) + "°", 12, "#AAAAAA", false));
        return chip;
    }

    private TextView styledText(String text, int sp, String hex, boolean bold) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTextColor(Color.parseColor(hex));
        tv.setTypeface(bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        return tv;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String dayLabel(int offset) {
        return offset == 0 ? "Today"
            : LocalDate.now().plusDays(offset).format(DateTimeFormatter.ofPattern("EEE M/d"));
    }

    private String uvLabel(float uv) {
        if (uv >= 11) return "Extreme";
        if (uv >= 8)  return "Very High";
        if (uv >= 6)  return "High";
        if (uv >= 3)  return "Moderate";
        return "Low";
    }

    private int uvColor(float uv) {
        if (uv >= 11) return Color.parseColor("#7B1FA2");
        if (uv >= 8)  return Color.parseColor("#C62828");
        if (uv >= 6)  return Color.parseColor("#E65100");
        if (uv >= 3)  return Color.parseColor("#F9A825");
        return Color.parseColor("#2E7D32");
    }

    private String etLabel(float et) {
        if (et >= 6) return "Severe drought stress";
        if (et >= 4) return "High water stress";
        if (et >= 2) return "Moderate evaporation";
        return "Low water stress";
    }

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

    // Math helpers

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
    private float[] convertArray(float[] src, boolean isTemp) {
        if (src == null) return null;
        float[] out = new float[src.length];
        for (int i = 0; i < src.length; i++) out[i] = isTemp ? temp(src[i]) : precip(src[i]);
        return out;
    }
    private String fmt0(float v) { return String.format("%.0f", v); }
    private String fmt1(float v) { return String.format("%.1f", v); }
}
