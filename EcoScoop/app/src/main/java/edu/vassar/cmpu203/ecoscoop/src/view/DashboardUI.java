package edu.vassar.cmpu203.ecoscoop.src.view;

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
    }

    /** Sets the listener that handles nav button clicks. */
    void setListener(Listener listener);
}
