package edu.vassar.cmpu203.ecoscoop.src.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentDashboardBinding;
import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataRetriever;

/**
 * Dashboard — the app's landing screen.
 *
 * Currently a mockup with static text and the standard bottom nav bar.
 * All navigation is delegated to ControllerActivity via DashboardUI.Listener.
 */
public class DashboardFragment extends Fragment implements DashboardUI {

    private FragmentDashboardBinding binding;
    private DashboardUI.Listener listener;
    private EcoDataRetriever pendingRetriever;

    /** Grabs the controller as the listener when the fragment attaches to the activity. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashboardUI.Listener) {
            this.listener = (DashboardUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement DashboardUI.Listener");
        }
    }

    /** Clears the listener when the fragment detaches. */
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    /** Inflates the dashboard layout. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    /** Wires the nav buttons to the listener. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.articleFeedTab.setOnClickListener(v -> {
            if (listener != null) listener.onArticleTabClick();
        });
        binding.dashboardTab.setOnClickListener(v -> {
            // Already on dashboard — no-op
        });
        binding.searchTab.setOnClickListener(v -> {
            if (listener != null) listener.onSearchClick();
        });
        binding.profileTab.setOnClickListener(v -> {
            if (listener != null) listener.onProfileClick();
        });

        if (pendingRetriever != null) {
            onWeatherLoaded(pendingRetriever);
            pendingRetriever = null;
        }
    }

    /** Receives the retriever from the controller and binds it to the weather cards. */
    @SuppressLint("SetTextI18n")
    @Override
    public void onWeatherLoaded(EcoDataRetriever retriever) {
        if (binding == null) {
            pendingRetriever = retriever;
            return;
        }
        binding.textCurrentTemp.setText((int) retriever.getCurrentTemp() + "°C");
        binding.textCurrentWind.setText((int)retriever.getCurrentWind() + " km/h");
        binding.textPrecipitation.setText((int)retriever.getDailyPrecip()[0] + " mm");
        binding.textHumidity.setText((int) retriever.getHourlyHumidity()[0] + "%");

        binding.cardTemperature.setOnClickListener(v ->
                showPopup("Temperature Forecast",
                        "High: " + (int)retriever.getDailyTempMax()[0] + "°C\n" +
                                "Low:  " + (int)retriever.getDailyTempMin()[0] + "°C"));

        binding.cardWind.setOnClickListener(v ->
                showPopup("Wind Detail",
                        "Current: "   + (int)retriever.getCurrentWind()    + " km/h\n" +
                                "Max today: " + (int)retriever.getDailyWindMax()[0] + " km/h"));

        binding.cardPrecipitation.setOnClickListener(v ->
                showPopup("Precipitation",
                        "Today: "    + (int) retriever.getDailyPrecip()[0] + " mm\n" +
                                "Tomorrow: " + (int) retriever.getDailyPrecip()[1] + " mm"));

        binding.cardHumidity.setOnClickListener(v ->
                showPopup("Humidity",
                        "Current: " + (int) retriever.getHourlyHumidity()[0] + "%"));
    }

    private void showPopup(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    /** Sets the listener for nav button events. */
    @Override
    public void setListener(DashboardUI.Listener listener) {
        this.listener = listener;
    }
}
