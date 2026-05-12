package edu.vassar.cmpu203.ecoscoop;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
 * Espresso tests for article interactions: like/dislike toggling,
 * mutual exclusion between reactions, commenting, and saving.
 *
 * Tests wait 10 seconds on setup for the RSS feed to finish loading
 * before navigating into an article.
 */
@RunWith(AndroidJUnit4.class)
public class ArticleInteractionTest {

    @Rule
    public ActivityScenarioRule<ControllerActivity> activityRule =
            new ActivityScenarioRule<>(ControllerActivity.class);

    /**
     * Signs in, navigates to the feed, waits for articles to load, then opens the first card.
     */
    @Before
    public void openFirstArticle() {
        EspressoTestHelper.loginAndWait();
        onView(ViewMatchers.withId(R.id.articleFeedTab)).perform(click());
        SystemClock.sleep(20000);
        onView(childAtPosition(ViewMatchers.withId(R.id.itemsRecView), 0)).perform(click());
        SystemClock.sleep(2000);
    }

    //Liking and Disliking

    /**
     * Verifies the like button starts at "Likes: 0" before any interaction.
     */
    @Test
    public void likeButton_initiallyShowsZero() {
        onView(ViewMatchers.withId(R.id.likeButton)).check(matches(withText("Likes: 0")));
    }

    /**
     * Verifies that tapping Like once increments the count and changes
     * the label to "Liked: 1".
     */
    @Test
    public void likeButton_firstTap_incrementsAndMarksLiked() {
        onView(ViewMatchers.withId(R.id.likeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.likeButton)).check(matches(withText("Liked: 1")));
    }

    /**
     * Verifies that tapping Like twice toggles it back off:
     * count returns to 0 and label reverts to "Likes: 0".
     */
    @Test
    public void likeButton_secondTap_togglesOff() {
        onView(ViewMatchers.withId(R.id.likeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.likeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.likeButton)).check(matches(withText("Likes: 0")));
    }

    /**
     * Verifies the dislike button starts at "Dislikes: 0".
     */
    @Test
    public void dislikeButton_initiallyShowsZero() {
        onView(ViewMatchers.withId(R.id.dislikeButton)).check(matches(withText("Dislikes: 0")));
    }

    /**
     * Verifies that tapping Dislike once changes the label to "Disliked: 1".
     */
    @Test
    public void dislikeButton_firstTap_incrementsAndMarksDisliked() {
        onView(ViewMatchers.withId(R.id.dislikeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.dislikeButton)).check(matches(withText("Disliked: 1")));
    }

    /**
     * Verifies that tapping Dislike twice toggles it off.
     */
    @Test
    public void dislikeButton_secondTap_togglesOff() {
        onView(ViewMatchers.withId(R.id.dislikeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.dislikeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.dislikeButton)).check(matches(withText("Dislikes: 0")));
    }

    /**
     * Verifies that liking after disliking removes the dislike and adds the like.
     */
    @Test
    public void like_afterDislike_removesDislikeAndAddsLike() {
        onView(ViewMatchers.withId(R.id.dislikeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.likeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.likeButton)).check(matches(withText("Liked: 1")));
        onView(ViewMatchers.withId(R.id.dislikeButton)).check(matches(withText("Dislikes: 0")));
    }

    /**
     * Verifies that disliking after liking removes the like and adds the dislike.
     */
    @Test
    public void dislike_afterLike_removesLikeAndAddsDislike() {
        onView(ViewMatchers.withId(R.id.likeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.dislikeButton)).perform(click());
        onView(ViewMatchers.withId(R.id.dislikeButton)).check(matches(withText("Disliked: 1")));
        onView(ViewMatchers.withId(R.id.likeButton)).check(matches(withText("Likes: 0")));
    }

    //Commenting on Articles

    /**
     * Verifies that typing a comment and tapping Post makes the comment
     * appear in the list and clears the input field.
     */
    @Test
    public void postComment_appearsInList_andClearsInput() {
        onView(ViewMatchers.withId(R.id.commentInput))
                .perform(scrollTo(), typeText("Great article!"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.submitCommentButton)).perform(scrollTo(), click());

        onView(withText("• Great article!")).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.commentInput)).check(matches(withText("")));
    }

    /**
     * Verifies that multiple posted comments all appear in the list.
     */
    @Test
    public void multipleComments_allAppearInList() {
        onView(ViewMatchers.withId(R.id.commentInput))
                .perform(scrollTo(), typeText("First"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.submitCommentButton)).perform(scrollTo(), click());

        onView(ViewMatchers.withId(R.id.commentInput))
                .perform(scrollTo(), typeText("Second"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.submitCommentButton)).perform(scrollTo(), click());

        onView(withText("• First")).check(matches(isDisplayed()));
        onView(withText("• Second")).check(matches(isDisplayed()));
    }

    // Saving Articles

    /**
     * Verifies the save button is visible on the article detail screen.
     */
    @Test
    public void saveButton_isDisplayed() {
        onView(ViewMatchers.withId(R.id.saveButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping Save opens a dialog without crashing.
     */
    @Test
    public void saveButton_click_doesNotCrash() {
        onView(ViewMatchers.withId(R.id.saveButton)).perform(click());
        androidx.test.espresso.Espresso.pressBack();
    }

    // Navigation from article

    /**
     * Verifies that tapping the Articles nav tab from the article detail screen
     * returns the user to the article feed RecyclerView.
     */
    @Test
    public void articleFeedTab_fromArticleDetail_returnToFeed() {
        onView(ViewMatchers.withId(R.id.articleFeedTab)).perform(click());
        onView(ViewMatchers.withId(R.id.itemsRecView)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping the Search nav tab from the article detail screen
     * shows the search input without crashing.
     */
    @Test
    public void searchTab_fromArticleDetail_showsSearchInput() {
        onView(ViewMatchers.withId(R.id.searchTab)).perform(click());
        onView(ViewMatchers.withId(R.id.searchInput)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that tapping the Profile nav tab from the article detail screen
     * shows the profile settings button without crashing.
     */
    @Test
    public void profileTab_fromArticleDetail_showsProfile() {
        onView(ViewMatchers.withId(R.id.profileTab)).perform(click());
        onView(ViewMatchers.withId(R.id.settingsButton)).check(matches(isDisplayed()));
    }

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
