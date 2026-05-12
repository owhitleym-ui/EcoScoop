package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Source;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Unit tests for the non-trivial behaviour of {@link Article}.
 *
 * Covers: like/dislike toggle with mutual exclusion, comment management,
 * the {@code getContent()} normalisation pipeline (entity stripping,
 * inline-whitespace collapsing, paragraph-break capping), and the
 * {@code toMap()}/{@code fromMap()} Firestore serialisation round-trip.
 * Trivial getters are not tested individually.
 */
public class ArticleTest {

    private Article article;

    @Before
    public void setUp() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("Jane Doe"));
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("climate"));
        article = new Article("id-1", "Climate Report", "A summary.", authors, tags,
                new Source("Grist", "https://grist.org", "2024-01-15"),
                "Body text.", "https://img.example.com/pic.jpg");
    }

    // -------------------------------------------------------------------------
    // Like / Dislike — toggle behaviour
    // -------------------------------------------------------------------------

    /**
     * Verifies that calling {@code addLike()} once sets userReaction to "liked"
     * and increments the like count to 1.
     */
    @Test
    public void testAddLike_firstLike_incrementsAndSetsReaction() {
        article.addLike();
        assertEquals(1, article.getLikes());
        assertEquals("liked", article.getUserReaction());
    }

    /**
     * Verifies that calling {@code addLike()} twice toggles the reaction back to
     * "none" and returns the like count to 0.
     */
    @Test
    public void testAddLike_secondLike_togglesOff() {
        article.addLike();
        article.addLike();
        assertEquals(0, article.getLikes());
        assertEquals("none", article.getUserReaction());
    }

    /**
     * Verifies that calling {@code addDislike()} once sets userReaction to
     * "disliked" and increments the dislike count to 1.
     */
    @Test
    public void testAddDislike_firstDislike_incrementsAndSetsReaction() {
        article.addDislike();
        assertEquals(1, article.getDislikes());
        assertEquals("disliked", article.getUserReaction());
    }

    /**
     * Verifies that calling {@code addDislike()} twice toggles the reaction back
     * to "none" and returns the dislike count to 0.
     */
    @Test
    public void testAddDislike_secondDislike_togglesOff() {
        article.addDislike();
        article.addDislike();
        assertEquals(0, article.getDislikes());
        assertEquals("none", article.getUserReaction());
    }

    /**
     * Verifies mutual exclusion: liking an already-disliked article removes the
     * dislike, adds the like, and leaves dislike count at 0.
     */
    @Test
    public void testLike_afterDislike_switchesReaction() {
        article.addDislike();
        article.addLike();
        assertEquals(1, article.getLikes());
        assertEquals(0, article.getDislikes());
        assertEquals("liked", article.getUserReaction());
    }

    /**
     * Verifies mutual exclusion: disliking an already-liked article removes the
     * like, adds the dislike, and leaves like count at 0.
     */
    @Test
    public void testDislike_afterLike_switchesReaction() {
        article.addLike();
        article.addDislike();
        assertEquals(0, article.getLikes());
        assertEquals(1, article.getDislikes());
        assertEquals("disliked", article.getUserReaction());
    }

    /**
     * Verifies the default state: both counts and reaction start at neutral.
     */
    @Test
    public void testReactions_defaultState() {
        assertEquals(0, article.getLikes());
        assertEquals(0, article.getDislikes());
        assertEquals("none", article.getUserReaction());
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
        assertTrue(article.getComments().isEmpty());
    }

    /**
     * Verifies that a single added comment is retrievable at index 0.
     */
    @Test
    public void testAddComment_singleComment() {
        article.addComment("Great article!");
        assertEquals(1, article.getComments().size());
        assertEquals("Great article!", article.getComments().get(0));
    }

    /**
     * Verifies that multiple comments are stored in insertion order.
     */
    @Test
    public void testAddComment_multipleComments_insertionOrder() {
        article.addComment("First");
        article.addComment("Second");
        article.addComment("Third");
        List<String> c = article.getComments();
        assertEquals(3, c.size());
        assertEquals("First",  c.get(0));
        assertEquals("Second", c.get(1));
        assertEquals("Third",  c.get(2));
    }

    // -------------------------------------------------------------------------
    // getContent() — normalisation
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@code &nbsp;} HTML entities are replaced with a regular space.
     */
    @Test
    public void testGetContent_stripsNbspEntity() {
        assertEquals("Hello world", body("Hello&nbsp;world").getContent());
    }

    /**
     * Verifies that consecutive spaces and tabs on the same line are collapsed to
     * a single space.
     */
    @Test
    public void testGetContent_collapsesInlineWhitespace() {
        assertEquals("a b", body("a   b").getContent());
    }

    /**
     * Verifies that a single blank line between two paragraphs is preserved.
     */
    @Test
    public void testGetContent_preservesSingleParagraphBreak() {
        String result = body("Para one.\n\nPara two.").getContent();
        assertTrue(result.contains("Para one.\n\nPara two."));
    }

    /**
     * Verifies that three or more consecutive newlines are capped to two
     * (one blank line maximum).
     */
    @Test
    public void testGetContent_capsRunOfNewlines() {
        String result = body("a\n\n\n\nb").getContent();
        assertFalse(result.contains("\n\n\n"));
        assertTrue(result.contains("a\n\nb"));
    }

    /**
     * Verifies that spaces flanking a newline are stripped.
     */
    @Test
    public void testGetContent_stripsSpacesAroundNewlines() {
        assertEquals("a\nb", body("a  \n  b").getContent());
    }

    /**
     * Verifies that leading and trailing whitespace is trimmed from the result.
     */
    @Test
    public void testGetContent_trimsLeadingAndTrailingWhitespace() {
        assertEquals("hello", body("  hello  ").getContent());
    }

    /**
     * Verifies that an empty body returns an empty string without throwing.
     */
    @Test
    public void testGetContent_emptyBody() {
        assertEquals("", body("").getContent());
    }

    /**
     * Verifies that all normalisation rules compose correctly on a realistic
     * mixed input.
     */
    @Test
    public void testGetContent_combinedNormalisation() {
        String input  = "  Hello&nbsp;world.  \n\n\n\n  Second paragraph.  ";
        String result = body(input).getContent();
        assertTrue(result.startsWith("Hello world."));
        assertTrue(result.contains("\n\nSecond paragraph."));
        assertFalse(result.contains("\n\n\n"));
    }

    // -------------------------------------------------------------------------
    // toMap / fromMap — serialisation round-trip
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@code toMap()} followed by {@code fromMap()} faithfully
     * reconstructs the article's core fields (id, title, description, content,
     * authors, tags, source metadata).
     */
    @Test
    public void testToMap_fromMap_roundTrip() {
        Map<String, Object> map = article.toMap();
        Article restored = Article.fromMap(map);

        assertEquals(article.getId(),          restored.getId());
        assertEquals(article.getTitle(),       restored.getTitle());
        assertEquals(article.getDescription(), restored.getDescription());
        assertEquals(article.getContent(),     restored.getContent());
        assertEquals(article.getImageUrl(),    restored.getImageUrl());

        assertEquals(1, restored.getAuthors().size());
        assertEquals("Jane Doe", restored.getAuthors().get(0).getName());

        assertEquals(1, restored.getTagList().size());
        assertEquals("climate", restored.getTagList().get(0).getName());

        assertEquals("Grist",              restored.getSource().getWebsiteName());
        assertEquals("https://grist.org", restored.getSource().getUrl());
        assertEquals("2024-01-15",        restored.getSource().getPublishDate());
    }

    /**
     * Verifies that {@code fromMap()} handles a map with missing keys gracefully,
     * returning empty strings rather than throwing.
     */
    @Test
    public void testFromMap_missingKeys_defaultsToEmptyStrings() {
        Map<String, Object> emptyMap = new HashMap<>();
        Article restored = Article.fromMap(emptyMap);
        assertNotNull(restored);
        assertEquals("", restored.getId());
        assertEquals("", restored.getTitle());
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Creates an Article with only the body field set, for getContent() tests. */
    private Article body(String content) {
        return new Article("id-99", "T", "D", new ArrayList<>(), new ArrayList<>(),
                new Source("", "", ""), content, "");
    }
}
