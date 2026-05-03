package edu.vassar.cmpu203.ecoscoop.src.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vassar.cmpu203.ecoscoop.src.controller.ArticleRetriever;

/**
 * Represents a user-created folder for saving articles.
 * Stores article IDs and uses an ArticleRetriever to look them up when the folder is opened.
 */
public class Folder implements Serializable {

    private String name;
    private List<Integer> articleIds;
    private transient ArticleRetriever articleRetriever;

    /**
     * Creates a new empty folder.
     *
     * @param name     the folder name
     * @param articleRetriever used to look up articles when the folder is opened
     */
    public Folder(String name, ArticleRetriever articleRetriever) {
        this.name = name;
        this.articleRetriever = articleRetriever;
        this.articleIds = new ArrayList<>();
    }

    public Folder() {
        this.name = "";
        this.articleIds = new ArrayList<>();
    }

    public void setArticleRetriever(ArticleRetriever retriever) {
        this.articleRetriever = retriever;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("articleIds", new ArrayList<>(articleIds));
        return map;
    }

    public static Folder fromMap(Map<String, Object> map) {
        Folder folder = new Folder();
        folder.name = (String) map.getOrDefault("name", "");
        Object ids = map.get("articleIds");
        if (ids instanceof List) {
            for (Object id : (List<?>) ids) {
                if (id instanceof Long) {
                    folder.articleIds.add(((Long) id).intValue());
                }
            }
        }
        return folder;
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
        if (articleRetriever.getArticle(id) == null) {
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
        if (articleRetriever == null) return new ArrayList<>();
        List<Article> contents = new ArrayList<>();
        for (int id : articleIds) {
            Article a = articleRetriever.getArticle(id);
            if (a != null) contents.add(a);
        }
        return contents;
    }

    /** Returns the number of articles in this folder without full retrieval. */
    public int size() {
        return articleIds.size();
    }

}
