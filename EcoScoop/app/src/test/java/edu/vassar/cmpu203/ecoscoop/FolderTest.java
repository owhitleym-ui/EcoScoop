package edu.vassar.cmpu203.ecoscoop;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.Source;

/**
 * Unit tests for the non-trivial behaviour of {@link Folder}.
 *
 * Focuses on: {@code addArticle()} duplicate prevention and invalid-ID rejection,
 * {@code removeArticle()}, {@code open()} contents, and {@code rename()} null guard.
 *
 * Uses an anonymous {@link ArticleDatabase} implementation to inject test data
 * without making any network calls.
 */
public class FolderTest {

    private ArticleDatabase database;
    private Folder folder;
    private Article article1;
    private Article article2;

    @Before
    public void setUp() {
        article1 = new Article(1, "Title One", "Desc one",
                new ArrayList<>(), new ArrayList<>(),
                new Source("Grist", "https://grist.org", "2024-01-01"), "Body one.", "");
        article2 = new Article(2, "Title Two", "Desc two",
                new ArrayList<>(), new ArrayList<>(),
                new Source("Grist", "https://grist.org", "2024-02-01"), "Body two.", "");

        Map<Integer, Article> db = new HashMap<>();
        db.put(1, article1);
        db.put(2, article2);

        List<Article> list = new ArrayList<>();
        list.add(article1);
        list.add(article2);

        database = new ArticleDatabase() {
            @Override public Map<Integer, Article> getDatabase() { return db; }
            @Override public List<Article> getArticles() { return list; }
        };

        folder = new Folder("Favourites", database);
    }

    // -------------------------------------------------------------------------
    // addArticle
    // -------------------------------------------------------------------------

    /**
     * Verifies that adding a valid article ID makes the article appear in
     * {@code open()}.
     */
    @Test
    public void testAddArticle_validIdAppearsInOpen() {
        folder.addArticle(1);
        List<Article> contents = folder.open();
        assertEquals(1, contents.size());
        assertEquals(article1, contents.get(0));
    }

    /**
     * Verifies that adding the same article ID twice results in only one entry
     * (no duplicates stored).
     */
    @Test
    public void testAddArticle_duplicateIgnored() {
        folder.addArticle(1);
        folder.addArticle(1);
        assertEquals(1, folder.open().size());
    }

    /**
     * Verifies that adding an ID that does not exist in the database
     * throws {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddArticle_invalidIdThrows() {
        folder.addArticle(999);
    }

    // -------------------------------------------------------------------------
    // removeArticle
    // -------------------------------------------------------------------------

    /**
     * Verifies that removing a previously added article results in an empty
     * folder.
     */
    @Test
    public void testRemoveArticle_removesCorrectEntry() {
        folder.addArticle(1);
        folder.addArticle(2);
        folder.removeArticle(1);
        List<Article> contents = folder.open();
        assertEquals(1, contents.size());
        assertEquals(article2, contents.get(0));
    }

    /**
     * Verifies that calling {@code removeArticle()} with an ID that was never
     * added does not throw and leaves the folder unchanged.
     */
    @Test
    public void testRemoveArticle_nonExistentIdNoOp() {
        folder.addArticle(1);
        folder.removeArticle(999);
        assertEquals(1, folder.open().size());
    }

    // -------------------------------------------------------------------------
    // open
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code open()} returns an empty list when no articles have been
     * added to the folder.
     */
    @Test
    public void testOpen_emptyFolderReturnsEmptyList() {
        assertTrue(folder.open().isEmpty());
    }

    /**
     * Verifies {@code open()} returns articles in the order they were added.
     */
    @Test
    public void testOpen_preservesInsertionOrder() {
        folder.addArticle(1);
        folder.addArticle(2);
        List<Article> contents = folder.open();
        assertEquals(article1, contents.get(0));
        assertEquals(article2, contents.get(1));
    }

    // -------------------------------------------------------------------------
    // rename
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code rename()} updates the folder's name as returned by
     * {@code getFolderName()}.
     */
    @Test
    public void testRename_updatesName() {
        folder.rename("Climate Reads");
        assertEquals("Climate Reads", folder.getFolderName());
    }

    /**
     * Verifies {@code rename(null)} throws {@link IllegalArgumentException},
     * preventing the folder from being left nameless.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRename_nullThrows() {
        folder.rename(null);
    }
}
