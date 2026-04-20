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
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.Source;

/**
 * Unit tests for the non-trivial behaviour of {@link FolderManager}.
 *
 * Focuses on: {@code createFolder()} adding to the collection,
 * {@code deleteFolder()} true/false return values, {@code getFolder()} null
 * when missing, and {@code saveToFolder()} auto-creating folders on demand.
 *
 * Uses an anonymous {@link ArticleDatabase} implementation to inject test data
 * without making any network calls.
 */
public class FolderManagerTest {

    private ArticleDatabase database;
    private FolderManager manager;
    private Article article;

    @Before
    public void setUp() {
        article = new Article(1, "Title", "Desc",
                new ArrayList<>(), new ArrayList<>(),
                new Source("Grist", "https://grist.org", "2024-01-01"), "Body.", "");

        Map<Integer, Article> db = new HashMap<>();
        db.put(1, article);

        List<Article> list = new ArrayList<>();
        list.add(article);

        database = new ArticleDatabase() {
            @Override public Map<Integer, Article> getDatabase() { return db; }
            @Override public List<Article> getArticles() { return list; }
        };

        manager = new FolderManager(database);
    }

    // -------------------------------------------------------------------------
    // createFolder
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code createFolder()} returns a non-null Folder and that the
     * folder appears in {@code getFolders()}.
     */
    @Test
    public void testCreateFolder_appearsInGetFolders() {
        Folder f = manager.createFolder("Climate");
        assertNotNull(f);
        assertEquals(1, manager.getFolders().size());
        assertSame(f, manager.getFolders().get(0));
    }

    /**
     * Verifies that creating multiple folders results in all of them being tracked.
     */
    @Test
    public void testCreateFolder_multipleFoldersAllTracked() {
        manager.createFolder("Alpha");
        manager.createFolder("Beta");
        manager.createFolder("Gamma");
        assertEquals(3, manager.getFolders().size());
    }

    // -------------------------------------------------------------------------
    // deleteFolder
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code deleteFolder()} returns {@code true} and removes the folder
     * when the named folder exists.
     */
    @Test
    public void testDeleteFolder_existingFolderReturnsTrue() {
        manager.createFolder("ToDelete");
        boolean result = manager.deleteFolder("ToDelete");
        assertTrue(result);
        assertEquals(0, manager.getFolders().size());
    }

    /**
     * Verifies {@code deleteFolder()} returns {@code false} when the named folder
     * does not exist, leaving the collection unchanged.
     */
    @Test
    public void testDeleteFolder_nonExistentFolderReturnsFalse() {
        boolean result = manager.deleteFolder("Ghost");
        assertFalse(result);
    }

    // -------------------------------------------------------------------------
    // getFolder
    // -------------------------------------------------------------------------

    /**
     * Verifies {@code getFolder()} returns the correct folder when it exists.
     */
    @Test
    public void testGetFolder_existingNameReturnsFolder() {
        Folder f = manager.createFolder("Science");
        assertSame(f, manager.getFolder("Science"));
    }

    /**
     * Verifies {@code getFolder()} returns {@code null} when no folder has the
     * given name.
     */
    @Test
    public void testGetFolder_missingNameReturnsNull() {
        assertNull(manager.getFolder("DoesNotExist"));
    }

    // -------------------------------------------------------------------------
    // saveToFolder
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@code saveToFolder()} auto-creates the folder when it does
     * not already exist, and that the article is retrievable via {@code open()}.
     */
    @Test
    public void testSaveToFolder_autoCreatesFolder() {
        manager.saveToFolder(1, "NewFolder");
        Folder f = manager.getFolder("NewFolder");
        assertNotNull(f);
        assertEquals(1, f.open().size());
    }

    /**
     * Verifies that {@code saveToFolder()} reuses an existing folder rather than
     * creating a second one with the same name.
     */
    @Test
    public void testSaveToFolder_usesExistingFolderWhenPresent() {
        manager.createFolder("Existing");
        manager.saveToFolder(1, "Existing");
        assertEquals(1, manager.getFolders().size());
        assertEquals(1, manager.getFolder("Existing").open().size());
    }
}
