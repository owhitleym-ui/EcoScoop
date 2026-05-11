package edu.vassar.cmpu203.ecoscoop.src.view;

import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.vassar.cmpu203.ecoscoop.databinding.ActivityMainBinding;

/**
 * Top-level view helper that owns the activity's root binding and {@link androidx.fragment.app.FragmentManager},
 * exposing a single {@link #displayFragment} method to swap the visible screen.
 */
public class MainUI {

    private final ActivityMainBinding binding;
    private final FragmentManager fmanager;

    /** Creates the MainUI, inflates the activity layout, and enables edge-to-edge rendering. */
    public MainUI (@NonNull FragmentActivity factivity) {
        this.binding = ActivityMainBinding.inflate(LayoutInflater.from(factivity));
        this.fmanager = factivity.getSupportFragmentManager();

        // eliminates colored bar at top of screen
        EdgeToEdge.enable(factivity);
        ViewCompat.setOnApplyWindowInsetsListener(this.getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Replaces the contents of the screen's fragment container with the one passed in as an argument.
     *
     * @param frag The fragment to be displayed.
     */
    public void displayFragment(@NonNull Fragment frag) {
        FragmentTransaction ftrans = this.fmanager.beginTransaction();
        ftrans.replace(this.binding.fragmentContainer.getId(), frag);
        ftrans.commit();
    }

    /** Returns the root view of the activity layout, needed to set it as the content view. */
    @NonNull
    public View getRootView() { return this.binding.getRoot(); }
}
