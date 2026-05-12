package edu.vassar.cmpu203.ecoscoop;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;

/**
 * Shared login helper for Espresso tests that need to get past the auth screen.
 *
 * The test account (testuser_espresso / password123) must already exist in Firestore.
 * Run AuthTest.register_withCredentials_doesNotCrash once on a fresh install to create it.
 */
class EspressoTestHelper {

    static final String TEST_USER = "testuser_espresso";
    static final String TEST_PASS = "password123";

    /**
     * Types the test credentials, taps Sign In, and sleeps 4 seconds
     * for the Firestore round-trip and fragment transition to complete.
     */
    static void loginAndWait() {
        onView(withId(R.id.usernameEditText))
                .perform(typeText(TEST_USER), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText(TEST_PASS), closeSoftKeyboard());
        onView(withId(R.id.signinButton)).perform(click());
        SystemClock.sleep(4000);
    }
}
