package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.controller.ArticleRetriever;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Source;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Unit tests for the non-trivial behaviour of {@link ArticleRetriever}.
 *
 * Focuses on: keyword search (match logic and relevance sort), tag search,
 * author search, date sort, and {@code getArticle()} valid/invalid lookup.
 *
 * Uses an anonymous {@link ArticleDatabase} implementation to inject test data
 * without making any network calls.
 */
public class ArticleServiceTest {

    private ArticleRetriever service;
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

        climate = new Article("a1", "Climate change overview", "A summary of climate.",
                smithAuthors, climateTags,
                new Source("Grist", "https://grist.org", "2024-03-01"),
                "Climate change is accelerating.", "");

        energy = new Article("a2", "Renewable energy report", "Clean energy trends.",
                new ArrayList<>(), energyTags,
                new Source("Carbon Brief", "https://carbonbrief.org", "2024-01-15"),
                "Solar and wind power are growing fast.", "");

        ocean = new Article("a3", "Ocean health update", "Marine ecosystem news.",
                new ArrayList<>(), oceanTags,
                new Source("Earth911", "https://earth911.com", "2024-06-20"),
                "Coral reefs are under threat.", "");

        Map<String, Article> db = new HashMap<>();
        db.put("a1", climate);
        db.put("a2", energy);
        db.put("a3", ocean);

        List<Article> list = new ArrayList<>(Arrays.asList(climate, energy, ocean));

        ArticleDatabase mockDatabase = new ArticleDatabase() {
            @Override public Map<String, Article> getDatabase() { return db; }
            @Override public List<Article> getArticles() { return list; }
        };

        service = new ArticleRetriever(mockDatabase);
    }

    // searchArticles — keyword

    /**
     * Verifies that a keyword search returns only articles whose title,
     * description, or content contains the query term.
     */
    @Test
    public void testSearchByKeyword_matchesRelevantArticles() {
        List<Article> results = service.searchArticles("climate", "keyword");
        assertTrue(results.contains(climate));
        assertFalse(results.contains(ocean));
    }

    /**
     * Verifies that keyword search returns an empty list when the query is blank.
     */
    @Test
    public void testSearchByKeyword_emptyQueryReturnsEmpty() {
        List<Article> results = service.searchArticles("   ", "keyword");
        assertTrue(results.isEmpty());
    }

    /**
     * Verifies that keyword search returns an empty list when the query is null.
     */
    @Test
    public void testSearchByKeyword_nullQueryReturnsEmpty() {
        List<Article> results = service.searchArticles(null, "keyword");
        assertTrue(results.isEmpty());
    }

    /**
     * Verifies that when multiple articles match, the one with more keyword hits
     * is ranked first.
     */
    @Test
    public void testSearchByKeyword_sortedByRelevance() {
        List<Article> results = service.searchArticles("climate", "keyword");
        assertFalse(results.isEmpty());
        assertEquals(climate, results.get(0));
    }

    // searchArticles — tag

    /**
     * Verifies that tag search returns only articles carrying a matching tag.
     */
    @Test
    public void testSearchByTag_matchesCorrectArticles() {
        List<Article> results = service.searchArticles("solar", "tag");
        assertEquals(1, results.size());
        assertEquals(energy, results.get(0));
    }

    /**
     * Verifies that a tag query with no matches returns an empty list.
     */
    @Test
    public void testSearchByTag_noMatchReturnsEmpty() {
        List<Article> results = service.searchArticles("nonexistenttag", "tag");
        assertTrue(results.isEmpty());
    }

    // searchArticles — author

    /**
     * Verifies that author search returns articles by a matching author name
     * (case-insensitive substring match).
     */
    @Test
    public void testSearchByAuthor_matchesCorrectArticles() {
        List<Article> results = service.searchArticles("alice", "author");
        assertEquals(1, results.size());
        assertEquals(climate, results.get(0));
    }

    /**
     * Verifies that author search returns an empty list when no author matches.
     */
    @Test
    public void testSearchByAuthor_noMatchReturnsEmpty() {
        List<Article> results = service.searchArticles("zzzunknown", "author");
        assertTrue(results.isEmpty());
    }

    // sortArticles — date

    /**
     * Verifies that sorting by date places newer articles first.
     */
    @Test
    public void testSortByDate_newestFirst() {
        List<Article> all = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> sorted = service.sortArticles(all, "date");
        assertEquals(ocean,   sorted.get(0));
        assertEquals(climate, sorted.get(1));
        assertEquals(energy,  sorted.get(2));
    }

    /**
     * Verifies that {@code sortArticles()} does not mutate the original list.
     */
    @Test
    public void testSortByDate_doesNotMutateInput() {
        List<Article> original = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> copy = new ArrayList<>(original);
        service.sortArticles(original, "date");
        assertEquals(copy, original);
    }

    // sortArticles — oldest first

    /**
     * Verifies that sorting by "oldest" places the article with the earliest
     * publishDate first (ascending order).
     */
    @Test
    public void testSortByOldest_oldestFirst() {
        List<Article> all = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> sorted = service.sortArticles(all, "oldest");
        // energy = 2024-01-15, climate = 2024-03-01, ocean = 2024-06-20
        assertEquals(energy,  sorted.get(0));
        assertEquals(climate, sorted.get(1));
        assertEquals(ocean,   sorted.get(2));
    }

    /**
     * Verifies "oldest" sort is the exact reverse of "date" (newest-first) sort.
     */
    @Test
    public void testSortByOldest_reverseOfNewest() {
        List<Article> all = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> newest = service.sortArticles(all, "date");
        List<Article> oldest = service.sortArticles(all, "oldest");
        assertEquals(newest.get(0), oldest.get(oldest.size() - 1));
        assertEquals(newest.get(newest.size() - 1), oldest.get(0));
    }

    /**
     * Verifies that "oldest" sort does not mutate the input list.
     */
    @Test
    public void testSortByOldest_doesNotMutateInput() {
        List<Article> original = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> copy = new ArrayList<>(original);
        service.sortArticles(original, "oldest");
        assertEquals(copy, original);
    }

    // sortArticles — source A-Z

    /**
     * Verifies that sorting by "source" orders articles alphabetically by
     * source website name (case-insensitive).
     */
    @Test
    public void testSortBySource_alphabeticalOrder() {
        List<Article> all = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> sorted = service.sortArticles(all, "source");
        // Carbon Brief < Earth911 < Grist
        assertEquals(energy,  sorted.get(0)); // Carbon Brief
        assertEquals(ocean,   sorted.get(1)); // Earth911
        assertEquals(climate, sorted.get(2)); // Grist
    }

    /**
     * Verifies that "source" sort does not mutate the input list.
     */
    @Test
    public void testSortBySource_doesNotMutateInput() {
        List<Article> original = new ArrayList<>(Arrays.asList(climate, energy, ocean));
        List<Article> copy = new ArrayList<>(original);
        service.sortArticles(original, "source");
        assertEquals(copy, original);
    }

    // getArticle

    /**
     * Verifies {@code getArticle()} returns the correct article for a valid ID.
     */
    @Test
    public void testGetArticle_validIdReturnsArticle() {
        assertSame(climate, service.getArticle("a1"));
    }

    /**
     * Verifies {@code getArticle()} returns {@code null} for an unknown ID.
     */
    @Test
    public void testGetArticle_invalidIdReturnsNull() {
        assertNull(service.getArticle("unknown"));
    }
}
