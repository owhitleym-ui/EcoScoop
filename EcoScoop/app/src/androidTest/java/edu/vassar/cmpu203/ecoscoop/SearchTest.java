package edu.vassar.cmpu203.ecoscoop;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.vassar.cmpu203.ecoscoop.src.controller.ControllerActivity;

/**
 * Espresso tests for the search screen: keyword, tag, and author searches,
 * sort toggling, and opening an article from search results.
 *
 * Tests wait 10 seconds for the RSS feed to load before interacting.
 */
@RunWith(AndroidJUnit4.class)
public class SearchTest {

    @Rule
    public ActivityScenarioRule<ControllerActivity> activityRule =
            new ActivityScenarioRule<>(ControllerActivity.class);

    /** Signs in, navigates to the Search tab, and waits for articles to load. */
    @Before
    public void navigateToSearch() {
        EspressoTestHelper.loginAndWait();
        onView(ViewMatchers.withId(R.id.searchTab)).perform(click());
        SystemClock.sleep(20000);
    }

    //Keyword search
    /**
     * Verifies that a common keyword returns results (count label is not "No results found").
     */
    @Test
    public void keywordSearch_climate_returnsResults() {
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("climate"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        onView(ViewMatchers.withId(R.id.searchResultsCount))
                .check(matches(not(withText("No results found"))));
    }

    /**
     * Verifies that a nonsense keyword shows "No results found".
     */
    @Test
    public void keywordSearch_gibberish_showsNoResults() {
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("zzznomatch99999"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        onView(ViewMatchers.withId(R.id.searchResultsCount))
                .check(matches(withText("No results found")));
    }

    /**
     * Verifies the result count label is displayed after any search.
     */
    @Test
    public void keywordSearch_resultCountLabelIsShown() {
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("energy"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        onView(ViewMatchers.withId(R.id.searchResultsCount)).check(matches(isDisplayed()));
    }

    //Tag Search
    /**
     * Verifies that switching to the Tag chip and searching does not crash,
     * and the result count label is shown.
     */
    @Test
    public void tagSearch_showsResultCount() {
        onView(ViewMatchers.withId(R.id.chipTag)).perform(click());
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("climate"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        onView(ViewMatchers.withId(R.id.searchResultsCount)).check(matches(isDisplayed()));
    }

    //Author Search
    /**
     * Verifies that switching to the Author chip and searching does not crash.
     */
    @Test
    public void authorSearch_showsResultCount() {
        onView(ViewMatchers.withId(R.id.chipAuthor)).perform(click());
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("staff"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        onView(ViewMatchers.withId(R.id.searchResultsCount)).check(matches(isDisplayed()));
    }

    //Sorting Functionality
    /**
     * Verifies that toggling "Newest first" after a search keeps results visible.
     */
    @Test
    public void sortByDate_afterSearch_keepsResultsVisible() {
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("climate"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        onView(ViewMatchers.withId(R.id.chipSortDate)).perform(click());

        onView(ViewMatchers.withId(R.id.searchResultsCount))
                .check(matches(not(withText("No results found"))));
    }

    //Search and Article Test
    /**
     * Verifies that tapping the first search result card opens the article detail screen.
     */
    @Test
    public void tapSearchResult_opensArticleDetail() {
        onView(ViewMatchers.withId(R.id.searchInput))
                .perform(typeText("the"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.searchButton)).perform(click());

        SystemClock.sleep(500);

        onView(childAtPosition(ViewMatchers.withId(R.id.searchResultsRecyclerView), 0))
                .perform(click());

        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
    }

    /**
     * Matches the child view at a given position inside a parent view.
     * Used to tap a specific item in a RecyclerView without espresso-contrib.
     */
    private static Matcher<View> childAtPosition(Matcher<View> parentMatcher, int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("child at position " + position + " in: ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup
                        && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
