package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Source;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Unit tests for the non-trivial behaviour of {@link Article}.
 *
 * Focuses on: like/dislike counters, comment management, and the
 * {@code getContent()} normalisation pipeline (entity stripping,
 * inline-whitespace collapsing, paragraph-break preservation/capping).
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
                "Body text.", "");
    }

    // -------------------------------------------------------------------------
    // Likes & dislikes
    // -------------------------------------------------------------------------
    /**
     * Verifies that likes and dislikes accumulate independently when both
     * are called on the same article.
     */
    @Test
    public void testLikesAndDislikes() {
        article.addLike();
        article.addDislike();
        article.addDislike();
        assertEquals(1, article.getLikes());
        assertEquals(2, article.getDislikes());
    }

    // -------------------------------------------------------------------------
    // Comments
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code getComments()} returns an empty list before any
     * comments are added.
     */
    @Test
    public void testGetComments_emptyBeforeAnyAdded() {
        assertTrue(article.getComments().isEmpty());
    }

    /**
     * Verifies that multiple comments are stored in order.
     */
    @Test
    public void testAddComment_multipleComments() {
        article.addComment("First");
        article.addComment("Second");
        article.addComment("Third");
        List<String> c = article.getComments();
        assertEquals(3, c.size());
        assertEquals("First",  c.get(0));
        assertEquals("Second", c.get(1));
        assertEquals("Third",  c.get(2));
    }

    /**
     * Verifies {@code &nbsp;} HTML entities are replaced with a regular space.
     */
    @Test
    public void testGetContent_stripsNbspEntity() {
        assertEquals("Hello world", body("Hello&nbsp;world").getContent());
    }

    /**
     * Verifies {@code getContent()} returns an empty string when the article
     * body is empty — no exceptions thrown.
     */
    @Test
    public void testGetContent_emptyBodyReturnsEmptyString() {
        assertEquals("", body("").getContent());
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
