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
    private List<String> articleIds;
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

    /** Creates an empty folder with no name; used by {@link #fromMap} during deserialization. */
    public Folder() {
        this.name = "";
        this.articleIds = new ArrayList<>();
    }

    /** Sets the {@link ArticleRetriever} used to resolve article IDs after deserialization. */
    public void setArticleRetriever(ArticleRetriever retriever) {
        this.articleRetriever = retriever;
    }

    /** Serializes this folder to a Firestore-compatible map. */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("articleIds", new ArrayList<>(articleIds));
        return map;
    }

    /** Reconstructs a Folder from a Firestore document map (retriever must be set separately). */
    public static Folder fromMap(Map<String, Object> map) {
        Folder folder = new Folder();
        folder.name = (String) map.getOrDefault("name", "");
        Object ids = map.get("articleIds");
        if (ids instanceof List) {
            for (Object id : (List<?>) ids) {
                if (id instanceof String) {
                    folder.articleIds.add((String) id);
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
     * Adds an article to this folder by UUID. Does nothing if it's already saved here.
     *
     * @param id the article UUID to add
     */
    public void addArticle(String id) {
        if (articleRetriever == null || articleRetriever.getArticle(id) == null) {
            throw new IllegalArgumentException("Article ID not found in database.");
        }
        if (!articleIds.contains(id)) {
            articleIds.add(id);
        }
    }

    /**
     * Removes an article from this folder by UUID.
     *
     * @param id the article UUID to remove
     */
    public void removeArticle(String id) {
        articleIds.remove(id);
    }

    /** Returns the articles currently saved in this folder. */
    public List<Article> open() {
        if (articleRetriever == null) return new ArrayList<>();
        List<Article> contents = new ArrayList<>();
        for (String id : articleIds) {
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
