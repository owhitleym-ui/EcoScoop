package edu.vassar.cmpu203.ecoscoop.src.view;

import edu.vassar.cmpu203.ecoscoop.src.controller.EcoDataRetriever;

/**
 * Interface for the dashboard screen.
 * Handles navigation from the landing page to other tabs.
 */
public interface DashboardUI {

    interface Listener {
        /** Goes to the article feed tab. */
        void onArticleTabClick();
        /** Goes to the search tab. */
        void onSearchClick();
        /** Goes to the profile tab. */
        void onProfileClick();

        /** Requests a weather refresh using the device's current GPS location. */
        void onRequestGPSRefresh();

        /** Requests a weather refresh for the manually entered location query string. */
        void onSearchLocation(String query);
    }

    /** Sets the listener that handles nav button clicks. */
    void setListener(Listener listener);

    /** Called by the controller when fresh weather data is ready to be displayed. */
    void onWeatherLoaded(EcoDataRetriever retriever);
}
