package edu.vassar.cmpu203.ecoscoop.src.view;

import androidx.fragment.app.FragmentManager;

/**
 * Utility class that owns top-level fragment transactions.
 * The controller calls methods here to swap the root screen.
 */
public class MainUI {

    private final FragmentManager fragmentManager;

    /**
     * @param fragmentManager the fragment manager from the activity
     */
    public MainUI(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Replaces the fragment container with a fresh DashboardFragment.
     * The DashboardFragment picks up its listener automatically via onAttach.
     *
     * @param containerId the R.id of the fragment container view
     */
    public void showDashboard(int containerId) {
        fragmentManager.beginTransaction()
                .replace(containerId, new DashboardFragment())
                .commitNow();
    }
}
