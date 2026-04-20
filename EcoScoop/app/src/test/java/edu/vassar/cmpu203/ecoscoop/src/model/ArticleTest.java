package edu.vassar.cmpu203.ecoscoop.src.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for non-trivial {@link Article} methods.
 *
 * Covers: like/dislike toggle behaviour (including mutual exclusion),
 * comment add and retrieval, and {@code getContent()} normalisation.
 * Getter/setter-only methods are not tested.
 */
public class ArticleTest {

    /** Creates an Article with only the body field set, for getContent() tests. */
    private Article body(String content) {
        return new Article(1, "", "", new ArrayList<>(), new ArrayList<>(),
                new Source("", "", ""), content, "");
    }

    // -------------------------------------------------------------------------
    // Like / Dislike — toggle behaviour
    // -------------------------------------------------------------------------

    /**
     * Verifies that calling {@code addLike()} once sets the reaction to liked
     * and increments the like count to 1.
     */
    @Test
    public void testAddLike_firstLikeIncrementsCount() {
        Article a = body("");
        a.addLike();
        assertEquals(1, a.getLikes());
        assertEquals("liked", a.getUserReaction());
    }

    /**
     * Verifies that calling {@code addLike()} twice toggles the reaction back to
     * none and returns the like count to 0.
     */
    @Test
    public void testAddLike_secondLikeTogglesOff() {
        Article a = body("");
        a.addLike();
        a.addLike();
        assertEquals(0, a.getLikes());
        assertEquals("none", a.getUserReaction());
    }

    /**
     * Verifies that calling {@code addDislike()} once sets the reaction to
     * disliked and increments the dislike count to 1.
     */
    @Test
    public void testAddDislike_firstDislikeIncrementsCount() {
        Article a = body("");
        a.addDislike();
        assertEquals(1, a.getDislikes());
        assertEquals("disliked", a.getUserReaction());
    }

    /**
     * Verifies that calling {@code addDislike()} twice toggles the reaction back
     * to none and returns the dislike count to 0.
     */
    @Test
    public void testAddDislike_secondDislikeTogglesOff() {
        Article a = body("");
        a.addDislike();
        a.addDislike();
        assertEquals(0, a.getDislikes());
        assertEquals("none", a.getUserReaction());
    }

    /**
     * Verifies mutual exclusion: liking an already-disliked article removes the
     * dislike, adds the like, and leaves the dislike count at 0.
     */
    @Test
    public void testLike_afterDislike_switchesReaction() {
        Article a = body("");
        a.addDislike();
        a.addLike();
        assertEquals(1, a.getLikes());
        assertEquals(0, a.getDislikes());
        assertEquals("liked", a.getUserReaction());
    }

    /**
     * Verifies mutual exclusion: disliking an already-liked article removes the
     * like, adds the dislike, and leaves the like count at 0.
     */
    @Test
    public void testDislike_afterLike_switchesReaction() {
        Article a = body("");
        a.addLike();
        a.addDislike();
        assertEquals(0, a.getLikes());
        assertEquals(1, a.getDislikes());
        assertEquals("disliked", a.getUserReaction());
    }

    /**
     * Verifies that liking then toggling off leaves both counts at 0
     * and reaction as "none".
     */
    @Test
    public void testLike_toggleOff_leavesNoReaction() {
        Article a = body("");
        a.addLike();
        a.addLike(); // toggle off
        assertEquals(0, a.getLikes());
        assertEquals(0, a.getDislikes());
        assertEquals("none", a.getUserReaction());
    }

    // -------------------------------------------------------------------------
    // Comments
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code getComments()} returns an empty list before any comments
     * are added.
     */
    @Test
    public void testGetComments_emptyBeforeAnyAdded() {
        assertTrue(body("").getComments().isEmpty());
    }

    /**
     * Verifies that a single added comment can be retrieved.
     */
    @Test
    public void testAddComment_singleCommentRetrievable() {
        Article a = body("");
        a.addComment("Great read!");
        List<String> comments = a.getComments();
        assertEquals(1, comments.size());
        assertEquals("Great read!", comments.get(0));
    }

    /**
     * Verifies that multiple comments are stored and returned in insertion order.
     */
    @Test
    public void testAddComment_multipleCommentsRetainInsertionOrder() {
        Article a = body("");
        a.addComment("First");
        a.addComment("Second");
        a.addComment("Third");
        List<String> comments = a.getComments();
        assertEquals("First",  comments.get(0));
        assertEquals("Second", comments.get(1));
        assertEquals("Third",  comments.get(2));
    }

    // -------------------------------------------------------------------------
    // getContent() — normalisation
    // -------------------------------------------------------------------------

    /** Verifies that {@code &nbsp;} entities are replaced with a regular space. */
    @Test
    public void testGetContent_stripsNbspEntity() {
        assertTrue(body("hello&nbsp;world").getContent().contains("hello world"));
    }

    /** Verifies that consecutive spaces and tabs on the same line are collapsed to one space. */
    @Test
    public void testGetContent_collapsesInlineWhitespace() {
        assertEquals("a b", body("a   b").getContent());
    }

    /** Verifies that a single blank line between two paragraphs is preserved. */
    @Test
    public void testGetContent_preservesSingleParagraphBreak() {
        String result = body("Para one.\n\nPara two.").getContent();
        assertTrue(result.contains("Para one.\n\nPara two."));
    }

    /** Verifies that three or more consecutive newlines are capped to two (one blank line). */
    @Test
    public void testGetContent_capsRunOfNewlinesToTwo() {
        String result = body("a\n\n\n\nb").getContent();
        assertFalse(result.contains("\n\n\n"));
        assertTrue(result.contains("a\n\nb"));
    }

    /** Verifies that spaces flanking newlines are stripped. */
    @Test
    public void testGetContent_stripsSpacesAroundNewlines() {
        assertEquals("a\nb", body("a  \n  b").getContent());
    }

    /** Verifies that leading and trailing whitespace is trimmed. */
    @Test
    public void testGetContent_trimsLeadingTrailingWhitespace() {
        assertEquals("hello", body("  hello  ").getContent());
    }

    /** Verifies that an empty body returns an empty string without throwing. */
    @Test
    public void testGetContent_emptyBodyReturnsEmptyString() {
        assertEquals("", body("").getContent());
    }

    /** Verifies that all normalisation rules apply correctly together. */
    @Test
    public void testGetContent_combinedNormalisation() {
        String input = "  Hello&nbsp;world.  \n\n\n\n  Second paragraph.  ";
        String result = body(input).getContent();
        assertTrue(result.startsWith("Hello world."));
        assertTrue(result.contains("\n\nSecond paragraph."));
        assertFalse(result.contains("\n\n\n"));
    }
}
