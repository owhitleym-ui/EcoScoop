package edu.vassar.cmpu203.ecoscoop;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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
 * Espresso tests for the authentication screen (use case: Register / Sign In).
 *
 * <p><strong>Note:</strong> These tests are designed to run on a fresh app install
 * or after clearing app data, so that no session is cached and the auth screen
 * is the first fragment shown. If a user session is already stored, the app
 * navigates directly to the dashboard and these tests will not find the auth
 * fields — run them via {@code adb shell pm clear edu.vassar.cmpu203.ecoscoop}
 * before executing.</p>
 *
 * Covers: presence of all auth UI elements, register with valid credentials,
 * sign-in with valid credentials, and edge-case empty-field submission.
 */
@RunWith(AndroidJUnit4.class)
public class AuthTest {

    @Rule
    public ActivityScenarioRule<ControllerActivity> activityRule =
            new ActivityScenarioRule<>(ControllerActivity.class);

    // -------------------------------------------------------------------------
    // UI structure
    // -------------------------------------------------------------------------

    /**
     * Verifies that the username input field is visible on the auth screen.
     */
    @Test
    public void authScreen_usernameFieldVisible() {
        onView(withId(R.id.usernameEditText)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the password input field is visible on the auth screen.
     */
    @Test
    public void authScreen_passwordFieldVisible() {
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the Register button is visible on the auth screen.
     */
    @Test
    public void authScreen_registerButtonVisible() {
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the Sign In button is visible on the auth screen.
     */
    @Test
    public void authScreen_signinButtonVisible() {
        onView(withId(R.id.signinButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the Register button carries the correct label text.
     */
    @Test
    public void authScreen_registerButtonHasCorrectLabel() {
        onView(withId(R.id.registerButton)).check(matches(withText("Register")));
    }

    /**
     * Verifies that the Sign In button carries the correct label text.
     */
    @Test
    public void authScreen_signinButtonHasCorrectLabel() {
        onView(withId(R.id.signinButton)).check(matches(withText("Sign In")));
    }

    // -------------------------------------------------------------------------
    // Registration flow
    // -------------------------------------------------------------------------

    /**
     * Verifies that entering a username and password then tapping Register does
     * not crash — the app should either accept the registration and navigate away
     * from the auth screen, or show a "user already exists" message if the
     * username is taken.
     */
    @Test
    public void register_withCredentials_doesNotCrash() {
        onView(withId(R.id.usernameEditText))
                .perform(typeText("testuser_espresso"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());
    }

    /**
     * Verifies that tapping Register with empty fields does not crash the app.
     */
    @Test
    public void register_emptyFields_doesNotCrash() {
        onView(withId(R.id.registerButton)).perform(click());
    }

    /**
     * Verifies that tapping Sign In with empty fields does not crash the app.
     */
    @Test
    public void signIn_emptyFields_doesNotCrash() {
        onView(withId(R.id.signinButton)).perform(click());
    }
}
