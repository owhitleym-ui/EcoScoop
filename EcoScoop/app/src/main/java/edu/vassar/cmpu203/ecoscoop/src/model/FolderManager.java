package edu.vassar.cmpu203.ecoscoop.src.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.controller.ArticleRetriever;

/**
 * Manages all user-created folders.
 * Standalone — not coupled to article retrieval or search.
 */
public class FolderManager implements Serializable {

    private final List<Folder> folders;
    private transient ArticleRetriever articleRetriever;

    /**
     * Creates a new FolderManager with no folders.
     *
     * @param articleRetriever the article data source used to validate IDs when saving
     */
    public FolderManager(ArticleRetriever articleRetriever) {
        this.folders = new ArrayList<>();
        this.articleRetriever = articleRetriever;
    }

    /**
     * Creates a new empty folder and adds it to the collection.
     *
     * @param name the folder name
     * @return the newly created Folder
     */
    public Folder createFolder(String name) {
        Folder folder = new Folder(name, articleRetriever);
        folders.add(folder);
        return folder;
    }

    /**
     * Deletes the folder with the given name.
     *
     * @param name the name of the folder to delete
     * @return true if a folder was found and removed, false if it didn't exist
     */
    public boolean deleteFolder(String name) {
        Folder folder = getFolder(name);
        if (folder == null) {
            return false;
        }
        folders.remove(folder);
        return true;
    }

    /**
     * Returns the folder with the given name, or null if it doesn't exist.
     *
     * @param name the folder name to look up
     */
    public Folder getFolder(String name) {
        for (Folder f : folders) {
            if (f.getFolderName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Renames the folder with the given name.
     *
     * @param oldName current folder name
     * @param newName new folder name
     * @return true if the folder was found and renamed
     */
    public boolean renameFolder(String oldName, String newName) {
        Folder folder = getFolder(oldName);
        if (folder == null) return false;
        folder.rename(newName);
        return true;
    }

    /**
     * Saves an article to a named folder. Creates the folder if it doesn't exist yet.
     *
     * @param articleId  the UUID of the article to save
     * @param folderName the name of the target folder
     */
    public void saveToFolder(String articleId, String folderName) {
        Folder folder = getFolder(folderName);
        if (folder == null) {
            folder = createFolder(folderName);
        }
        folder.addArticle(articleId);
    }

    /** Returns the list of all folders. */
    public List<Folder> getFolders() {
        return folders;
    }

    /** Updates the retriever and propagates it to all existing folders. */
    public void updateRetriever(ArticleRetriever newArticleRetriever) {
        this.articleRetriever = newArticleRetriever;
        for (Folder f : folders) {
            f.setArticleRetriever(newArticleRetriever);
        }
    }

    /** Replaces the current folder list with folders loaded from persistence. */
    public void restoreFolders(List<Folder> loaded) {
        folders.clear();
        for (Folder f : loaded) {
            f.setArticleRetriever(articleRetriever);
            folders.add(f);
        }
    }
}
