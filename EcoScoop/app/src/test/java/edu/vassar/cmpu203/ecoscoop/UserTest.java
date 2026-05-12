package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.User;

/**
 * Unit tests for the non-trivial behaviour of {@link User}.
 *
 * Focuses on: comment management (add, remove by index), settings toggles
 * (metric/imperial, local/global), and activity stat counters
 * (articles read, liked, disliked).
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User("testuser", "password123");
    }

    // -------------------------------------------------------------------------
    // Comments
    // -------------------------------------------------------------------------

    /**
     * Verifies that comments are added and stored in insertion order.
     */
    @Test
    public void testAddComment_storedInOrder() {
        user.addComment("Alpha");
        user.addComment("Beta");
        List<String> comments = user.getComments();
        assertEquals(2, comments.size());
        assertEquals("Alpha", comments.get(0));
        assertEquals("Beta",  comments.get(1));
    }

    /**
     * Verifies that {@code removeComment()} removes the comment at the given
     * index and shifts remaining comments down.
     */
    @Test
    public void testRemoveComment_removesAtIndex() {
        user.addComment("First");
        user.addComment("Second");
        user.addComment("Third");
        user.removeComment(1);
        List<String> comments = user.getComments();
        assertEquals(2, comments.size());
        assertEquals("First", comments.get(0));
        assertEquals("Third", comments.get(1));
    }

    /**
     * Verifies that removing the last comment leaves an empty list.
     */
    @Test
    public void testRemoveComment_lastCommentLeavesEmptyList() {
        user.addComment("Only");
        user.removeComment(0);
        assertTrue(user.getComments().isEmpty());
    }

    /**
     * Verifies {@code getComments()} returns an empty list on a fresh user.
     */
    @Test
    public void testGetComments_emptyByDefault() {
        assertTrue(user.getComments().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Settings — metric
    // -------------------------------------------------------------------------

    /**
     * Verifies metric preference defaults to {@code false} (imperial).
     */
    @Test
    public void testUseMetric_defaultFalse() {
        assertFalse(user.isUseMetric());
    }

    /**
     * Verifies {@code setUseMetric(true)} is reflected by {@code isUseMetric()}.
     */
    @Test
    public void testSetUseMetric_togglesToTrue() {
        user.setUseMetric(true);
        assertTrue(user.isUseMetric());
    }

    /**
     * Verifies toggling metric on then off returns to {@code false}.
     */
    @Test
    public void testSetUseMetric_roundTrip() {
        user.setUseMetric(true);
        user.setUseMetric(false);
        assertFalse(user.isUseMetric());
    }

    // -------------------------------------------------------------------------
    // Settings — local location
    // -------------------------------------------------------------------------

    /**
     * Verifies local-location preference defaults to {@code false} (global).
     */
    @Test
    public void testUseLocalLocation_defaultFalse() {
        assertFalse(user.isUseLocalLocation());
    }

    /**
     * Verifies {@code setUseLocalLocation(true)} is reflected correctly.
     */
    @Test
    public void testSetUseLocalLocation_togglesToTrue() {
        user.setUseLocalLocation(true);
        assertTrue(user.isUseLocalLocation());
    }

    // -------------------------------------------------------------------------
    // Activity stats
    // -------------------------------------------------------------------------

    /**
     * Verifies all stat counters start at zero.
     */
    @Test
    public void testStats_defaultZero() {
        assertEquals(0, user.getArticlesRead());
        assertEquals(0, user.getArticlesLiked());
        assertEquals(0, user.getArticlesDisliked());
    }

    /**
     * Verifies {@code incrementRead()} increases the read count by one each call.
     */
    @Test
    public void testIncrementRead_countsCorrectly() {
        user.incrementRead();
        user.incrementRead();
        user.incrementRead();
        assertEquals(3, user.getArticlesRead());
    }

    /**
     * Verifies {@code incrementLiked()} increases the liked count independently.
     */
    @Test
    public void testIncrementLiked_countsCorrectly() {
        user.incrementLiked();
        assertEquals(1, user.getArticlesLiked());
        assertEquals(0, user.getArticlesRead());
        assertEquals(0, user.getArticlesDisliked());
    }

    /**
     * Verifies {@code incrementDisliked()} increases the disliked count independently.
     */
    @Test
    public void testIncrementDisliked_countsCorrectly() {
        user.incrementDisliked();
        user.incrementDisliked();
        assertEquals(2, user.getArticlesDisliked());
        assertEquals(0, user.getArticlesLiked());
    }

    /**
     * Verifies all three counters accumulate independently.
     */
    @Test
    public void testStats_allCountersIndependent() {
        user.incrementRead();
        user.incrementRead();
        user.incrementLiked();
        user.incrementDisliked();
        user.incrementDisliked();
        user.incrementDisliked();
        assertEquals(2, user.getArticlesRead());
        assertEquals(1, user.getArticlesLiked());
        assertEquals(3, user.getArticlesDisliked());
    }
}
