package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all user-created folders.
 * Handles creating, deleting, and finding folders so that
 * Folder objects themselves don't need to know about the collection they live in.
 */
public class FolderManager {

    private final ArrayList<Folder> folders;
    private final ArticleRetriever retriever;

    /**
     * Creates a new FolderManager with no folders.
     *
     * @param retriever the ArticleRetriever used to validate article IDs when saving
     */
    public FolderManager(ArticleRetriever retriever) {
        this.folders = new ArrayList<>();
        this.retriever = retriever;
    }

    /**
     * Creates a new empty folder and adds it to the collection.
     *
     * @param name the folder name
     * @return the newly created Folder
     */
    public Folder createFolder(String name) {
        Folder folder = new Folder(name, retriever);
        folders.add(folder);
        return folder;
    }

    /**
     * Deletes the folder with the given name from the collection.
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
     * Saves an article to a named folder. Creates the folder if it doesn't exist yet.
     *
     * @param articleId  the ID of the article to save
     * @param folderName the name of the target folder
     */
    public void saveToFolder(int articleId, String folderName) {
        Folder folder = getFolder(folderName);
        if (folder == null) {
            folder = createFolder(folderName);
        }
        folder.addArticle(articleId);
    }

    /**
     * Returns the list of all folders.
     */
    public List<Folder> getFolders() {
        return folders;
    }
}
