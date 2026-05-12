package edu.vassar.cmpu203.ecoscoop;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.vassar.cmpu203.ecoscoop.src.controller.ControllerActivity;

/**
 * Espresso tests for the Profile screen (use cases: View Profile and Configure Settings).
 *
 * Covers: profile screen structural elements (stats, settings button, folders
 * section), navigating into and back out of an open folder, and verifying that
 * the Settings button is interactive without crashing.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    @Rule
    public ActivityScenarioRule<ControllerActivity> activityRule =
            new ActivityScenarioRule<>(ControllerActivity.class);

    /** Navigates to the Profile tab before each test. */
    @Before
    public void navigateToProfile() {
        onView(ViewMatchers.withId(R.id.profileTab)).perform(click());
    }

    // -------------------------------------------------------------------------
    // Profile screen structure
    // -------------------------------------------------------------------------

    /**
     * Verifies that the Settings button is visible on the profile screen landing page.
     */
    @Test
    public void profileScreen_settingsButtonVisible() {
        onView(withId(R.id.settingsButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the "articles read" stat counter is visible.
     */
    @Test
    public void profileScreen_statReadVisible() {
        onView(withId(R.id.statRead)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the "articles liked" stat counter is visible.
     */
    @Test
    public void profileScreen_statLikedVisible() {
        onView(withId(R.id.statLiked)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the "articles disliked" stat counter is visible.
     */
    @Test
    public void profileScreen_statDislikedVisible() {
        onView(withId(R.id.statDisliked)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the folders RecyclerView is visible on the profile screen,
     * confirming the saved-articles section is rendered.
     */
    @Test
    public void profileScreen_foldersRecyclerViewVisible() {
        onView(withId(R.id.foldersRecyclerView)).check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // Settings
    // -------------------------------------------------------------------------

    /**
     * Verifies that tapping the Settings button does not crash the app, and that
     * pressing back dismisses any dialog it opens and returns to the profile screen.
     */
    @Test
    public void settingsButton_click_doesNotCrash() {
        onView(withId(R.id.settingsButton)).perform(click());
        androidx.test.espresso.Espresso.pressBack();
        onView(withId(R.id.settingsButton)).check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // Navigation back to profile from another tab
    // -------------------------------------------------------------------------

    /**
     * Verifies that navigating away to the Articles tab and then tapping Profile
     * again brings the profile screen back with its contents intact.
     */
    @Test
    public void profileTab_afterLeavingAndReturning_isDisplayed() {
        onView(withId(R.id.articleFeedTab)).perform(click());
        onView(withId(R.id.profileTab)).perform(click());
        onView(withId(R.id.settingsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.statRead)).check(matches(isDisplayed()));
    }
}
