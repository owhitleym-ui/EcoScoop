package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleParser;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Unit tests for the non-trivial behaviour of {@link ArticleParser}.
 *
 * Focuses on: article count after parsing, correct extraction of title, author
 * ({@code dc:creator}), tag ({@code category}), and description from a
 * minimal RSS XML string — without hitting any network.
 *
 * Each test parses a small, hand-crafted RSS snippet so the expected values
 * are exact and deterministic.
 */
public class ArticleParserTest {

    /** Minimal RSS feed containing two {@code <item>} entries used by most tests. */
    private static final String TWO_ITEM_FEED =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<rss version=\"2.0\" " +
            "  xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
            "  xmlns:content=\"http://purl.org/rss/1.0/modules/content/\">" +
            "<channel>" +
            "  <item>" +
            "    <title>Climate crisis deepens</title>" +
            "    <link>https://grist.org/1</link>" +
            "    <description>A look at rising temperatures.</description>" +
            "    <pubDate>Mon, 15 Jan 2024 00:00:00 GMT</pubDate>" +
            "    <dc:creator>Alice Smith</dc:creator>" +
            "    <category>climate</category>" +
            "    <category>policy</category>" +
            "  </item>" +
            "  <item>" +
            "    <title>Solar power surge</title>" +
            "    <link>https://grist.org/2</link>" +
            "    <description>Renewables hit record highs.</description>" +
            "    <pubDate>Fri, 02 Feb 2024 00:00:00 GMT</pubDate>" +
            "    <dc:creator>Bob Jones</dc:creator>" +
            "    <category>energy</category>" +
            "  </item>" +
            "</channel>" +
            "</rss>";

    private ArticleParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new ArticleParser();
    }

    // -------------------------------------------------------------------------
    // Article count
    // -------------------------------------------------------------------------

    /**
     * Verifies that parsing a feed with two {@code <item>} elements produces
     * exactly two Article objects.
     */
    @Test
    public void testParse_twoItemsProducesTwoArticles() throws Exception {
        parser.parse(new String[]{}, TWO_ITEM_FEED, "Grist");
        assertEquals(2, parser.loadArticles().size());
    }

    /**
     * Verifies that parsing an empty channel (no {@code <item>} elements) results
     * in an empty article list rather than throwing an exception.
     */
    @Test
    public void testParse_emptyFeedProducesNoArticles() throws Exception {
        String emptyFeed =
                "<?xml version=\"1.0\"?>" +
                "<rss version=\"2.0\"><channel></channel></rss>";
        parser.parse(new String[]{}, emptyFeed, "Grist");
        assertTrue(parser.loadArticles().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Field extraction
    // -------------------------------------------------------------------------

    /**
     * Verifies that the article's title is extracted correctly from the
     * {@code <title>} element inside each {@code <item>}.
     */
    @Test
    public void testParse_titleExtractedCorrectly() throws Exception {
        parser.parse(new String[]{}, TWO_ITEM_FEED, "Grist");
        List<Article> articles = parser.loadArticles();
        assertEquals("Climate crisis deepens", articles.get(0).getTitle());
        assertEquals("Solar power surge",      articles.get(1).getTitle());
    }

    /**
     * Verifies that the author name is extracted from the {@code dc:creator}
     * element and attached to the correct article.
     */
    @Test
    public void testParse_authorExtractedFromDcCreator() throws Exception {
        parser.parse(new String[]{}, TWO_ITEM_FEED, "Grist");
        List<Article> articles = parser.loadArticles();
        List<Author> authors = articles.get(0).getAuthors();
        assertEquals(1, authors.size());
        assertEquals("Alice Smith", authors.get(0).getName());
    }

    /**
     * Verifies that all {@code <category>} elements in an item are collected
     * as tags on the resulting Article.
     */
    @Test
    public void testParse_multipleTagsExtracted() throws Exception {
        parser.parse(new String[]{}, TWO_ITEM_FEED, "Grist");
        List<Article> articles = parser.loadArticles();
        List<Tag> tags = articles.get(0).getTagList();
        assertEquals(2, tags.size());
        assertEquals("climate", tags.get(0).getName());
        assertEquals("policy",  tags.get(1).getName());
    }

    /**
     * Verifies that the {@code <description>} text is stored on the article and
     * is non-empty (confirming the field survives the cleaning pipeline).
     */
    @Test
    public void testParse_descriptionExtracted() throws Exception {
        parser.parse(new String[]{}, TWO_ITEM_FEED, "Grist");
        List<Article> articles = parser.loadArticles();
        String desc = articles.get(0).getDescription();
        assertFalse(desc.isEmpty());
        assertTrue(desc.contains("rising temperatures"));
    }

    // -------------------------------------------------------------------------
    // Source website
    // -------------------------------------------------------------------------

    /**
     * Verifies that the website name passed to {@code parse()} is stored as the
     * source website name on every article produced from that feed.
     */
    @Test
    public void testParse_sourceWebsiteNameAttachedToArticles() throws Exception {
        parser.parse(new String[]{}, TWO_ITEM_FEED, "Grist");
        for (Article a : parser.loadArticles()) {
            assertEquals("Grist", a.getSource().getWebsiteName());
        }
    }
}
