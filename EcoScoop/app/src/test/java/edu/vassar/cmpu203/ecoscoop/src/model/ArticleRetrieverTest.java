package edu.vassar.cmpu203.ecoscoop.src.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the non-trivial behaviour of {@link ArticleRetriever}.
 *
 * Focuses on: keyword search (match logic and relevance sort), tag search,
 * author search, date sort, and {@code getArticle()} valid/invalid lookup.
 *
 * Uses the package-private test constructor to inject pre-built data and bypass
 * all network calls.
 */
public class ArticleRetrieverTest {

    private ArticleRetriever retriever;
    private Article climate;
    private Article energy;
    private Article ocean;

    @Before
    public void setUp() {
        List<Author> smithAuthors = new ArrayList<>();
        smithAuthors.add(new Author("Alice Smith"));

        List<Tag> climateTags = new ArrayList<>();
        climateTags.add(new Tag("climate"));

        List<Tag> energyTags = new ArrayList<>();
        energyTags.add(new Tag("energy"));
        energyTags.add(new Tag("solar"));

        List<Tag> oceanTags = new ArrayList<>();
        oceanTags.add(new Tag("ocean"));

        // "climate" keyword appears in title and body — two fields → high relevance
        climate = new Article(1, "Climate change overview", "A summary of climate.",
                smithAuthors, climateTags,
                new Source("Grist", "https://grist.org", "2024-03-01"),
                "Climate change is accelerating.");

        // "energy" keyword appears only in title — one field
        energy = new Article(2, "Renewable energy report", "Clean energy trends.",
                new ArrayList<>(), energyTags,
                new Source("Carbon Brief", "https://carbonbrief.org", "2024-01-15"),
                "Solar and wind power are growing fast.");

        // no matching keywords for most queries
        ocean = new Article(3, "Ocean health update", "Marine ecosystem news.",
                new ArrayList<>(), oceanTags,
                new Source("Earth911", "https://earth911.com", "2024-06-20"),
                "Coral reefs are under threat.");

        Map<Integer, Article> db = new HashMap<>();
        db.put(1, climate);
        db.put(2, energy);
        db.put(3, ocean);

        List<Article> list = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        retriever = new ArticleRetriever(db, list);
    }

    // -------------------------------------------------------------------------
    // searchArticles — keyword
    // -------------------------------------------------------------------------

    /**
     * Verifies that a keyword search returns only articles whose title,
     * description, or content contains the query term.
     */
    @Test
    public void testSearchByKeyword_matchesRelevantArticles() {
        List<Article> results = retriever.searchArticles("climate", "keyword");
        assertTrue(results.contains(climate));
        assertFalse(results.contains(ocean));
    }

    /**
     * Verifies that keyword search returns an empty list when the query string
     * is blank (whitespace only).
     */
    @Test
    public void testSearchByKeyword_emptyQueryReturnsEmpty() {
        List<Article> results = retriever.searchArticles("   ", "keyword");
        assertTrue(results.isEmpty());
    }

    /**
     * Verifies that keyword search returns an empty list when the query is null.
     */
    @Test
    public void testSearchByKeyword_nullQueryReturnsEmpty() {
        List<Article> results = retriever.searchArticles(null, "keyword");
        assertTrue(results.isEmpty());
    }

    /**
     * Verifies that when two articles both match a keyword query, the article
     * matching more of the keywords is ranked first (higher relevance).
     */
    @Test
    public void testSearchByKeyword_sortedByRelevance() {
        // "climate" hits climate article in title + body (2 fields); energy article misses
        // so climate article should come first
        List<Article> results = retriever.searchArticles("climate", "keyword");
        assertFalse(results.isEmpty());
        assertEquals(climate, results.get(0));
    }

    // -------------------------------------------------------------------------
    // searchArticles — tag
    // -------------------------------------------------------------------------

    /**
     * Verifies that tag search returns only articles carrying a tag that
     * contains the query string.
     */
    @Test
    public void testSearchByTag_matchesCorrectArticles() {
        List<Article> results = retriever.searchArticles("solar", "tag");
        assertEquals(1, results.size());
        assertEquals(energy, results.get(0));
    }

    /**
     * Verifies that a tag query with no matching articles returns an empty list
     * rather than throwing an exception.
     */
    @Test
    public void testSearchByTag_noMatchReturnsEmpty() {
        List<Article> results = retriever.searchArticles("nonexistenttag", "tag");
        assertTrue(results.isEmpty());
    }

    // -------------------------------------------------------------------------
    // searchArticles — author
    // -------------------------------------------------------------------------

    /**
     * Verifies that author search returns articles written by an author whose
     * name contains the query string (case-insensitive substring match).
     */
    @Test
    public void testSearchByAuthor_matchesCorrectArticles() {
        List<Article> results = retriever.searchArticles("alice", "author");
        assertEquals(1, results.size());
        assertEquals(climate, results.get(0));
    }

    /**
     * Verifies that author search returns an empty list when no author name
     * contains the query string.
     */
    @Test
    public void testSearchByAuthor_noMatchReturnsEmpty() {
        List<Article> results = retriever.searchArticles("zzzunknown", "author");
        assertTrue(results.isEmpty());
    }

    // -------------------------------------------------------------------------
    // sortArticles — date
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@code sortArticles()} with criteria "date" places newer
     * articles first (descending publish date order).
     */
    @Test
    public void testSortByDate_newestFirst() {
        List<Article> all = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> sorted = retriever.sortArticles(all, "date");
        // ocean: 2024-06-20, climate: 2024-03-01, energy: 2024-01-15
        assertEquals(ocean,   sorted.get(0));
        assertEquals(climate, sorted.get(1));
        assertEquals(energy,  sorted.get(2));
    }

    /**
     * Verifies that {@code sortArticles()} does not mutate the original list
     * passed to it.
     */
    @Test
    public void testSortByDate_doesNotMutateInput() {
        List<Article> original = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> copy = new ArrayList<>(original);
        retriever.sortArticles(original, "date");
        assertEquals(copy, original);
    }

    // -------------------------------------------------------------------------
    // getArticle
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code getArticle()} returns the correct article for a valid ID.
     */
    @Test
    public void testGetArticle_validIdReturnsArticle() {
        assertSame(climate, retriever.getArticle(1));
    }

    /**
     * Verifies {@code getArticle()} returns {@code null} for an ID that does
     * not exist in the database, so callers can handle the missing-article case.
     */
    @Test
    public void testGetArticle_invalidIdReturnsNull() {
        assertNull(retriever.getArticle(999));
    }
}
