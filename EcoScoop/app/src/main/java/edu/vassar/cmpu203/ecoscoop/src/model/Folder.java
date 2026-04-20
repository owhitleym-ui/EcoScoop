package edu.vassar.cmpu203.ecoscoop.src.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user-created folder for saving articles.
 * Stores article IDs and uses an ArticleDatabase to look them up when the folder is opened.
 */
public class Folder {

    private String name;
    private List<Integer> articleIds;
    private ArticleDatabase database;

    /**
     * Creates a new empty folder.
     *
     * @param name     the folder name
     * @param database used to look up articles when the folder is opened
     */
    public Folder(String name, ArticleDatabase database) {
        this.name = name;
        this.database = database;
        this.articleIds = new ArrayList<>();
    }

    /** Returns the folder's name. */
    public String getFolderName() {
        return name;
    }

    /**
     * Renames the folder.
     *
     * @param newName the new name to give the folder
     */
    public void rename(String newName) {
        if (newName == null) {
            throw new IllegalArgumentException("Folder name cannot be blank.");
        }
        this.name = newName;
    }

    /**
     * Adds an article to this folder by ID. Does nothing if it's already saved here.
     *
     * @param id the article ID to add
     */
    public void addArticle(int id) {
        if (database.getDatabase().get(id) == null) {
            throw new IllegalArgumentException("Article ID not found in database.");
        }
        if (!articleIds.contains(id)) {
            articleIds.add(id);
        }
    }

    /**
     * Removes an article from this folder by ID.
     *
     * @param id the article ID to remove
     */
    public void removeArticle(int id) {
        articleIds.remove((Integer) id);
    }

    /** Returns the articles currently saved in this folder. */
    public List<Article> open() {
        List<Article> contents = new ArrayList<>();

        for (int id : articleIds) {
            Article a = database.getDatabase().get(id);
            if (a != null) {
                contents.add(a);
            }
        }

        return contents;
    }
}
