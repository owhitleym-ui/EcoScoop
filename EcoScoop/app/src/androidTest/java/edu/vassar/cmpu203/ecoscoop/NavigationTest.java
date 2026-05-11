package edu.vassar.cmpu203.ecoscoop;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.vassar.cmpu203.ecoscoop.src.controller.ControllerActivity;

/**
 * Espresso tests for tab navigation across all fragments.
 * Verifies that each nav button takes the user to the right screen.
 */
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<ControllerActivity> activityRule =
            new ActivityScenarioRule<>(ControllerActivity.class);

    /**
     * Verifies that the dashboard is shown on launch — the "EcoScoop" header
     * should be visible immediately without tapping anything.
     */
    @Test
    public void launch_showsDashboard() {
        onView(withText("EcoScoop")).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping the Articles tab shows the article feed RecyclerView.
     */
    @Test
    public void articleTab_showsFeed() {
        onView(withId(R.id.articleFeedTab)).perform(click());
        onView(withId(R.id.itemsRecView)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping the Search tab shows the search input field.
     */
    @Test
    public void searchTab_showsSearchInput() {
        onView(withId(R.id.searchTab)).perform(click());
        onView(withId(R.id.searchInput)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping the Profile tab shows the profile screen.
     */
    @Test
    public void profileTab_showsProfileScreen() {
        onView(withId(R.id.profileTab)).perform(click());
        onView(withId(R.id.settingsButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping Dashboard from another tab returns to the dashboard.
     */
    @Test
    public void dashboardTab_fromSearch_returnsToDashboard() {
        onView(withId(R.id.searchTab)).perform(click());
        onView(withId(R.id.dashboardTab)).perform(click());
        onView(withText("EcoScoop")).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping the Search tab from the feed shows the search screen,
     * then tapping Articles goes back to the feed.
     */
    @Test
    public void searchThenArticles_navigatesCorrectly() {
        onView(withId(R.id.articleFeedTab)).perform(click());
        onView(withId(R.id.searchTab)).perform(click());
        onView(withId(R.id.searchInput)).check(matches(isDisplayed()));

        onView(withId(R.id.articleFeedTab)).perform(click());
        onView(withId(R.id.itemsRecView)).check(matches(isDisplayed()));
    }
}
