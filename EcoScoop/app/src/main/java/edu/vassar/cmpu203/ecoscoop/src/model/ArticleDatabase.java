package edu.vassar.cmpu203.ecoscoop.src.model;

import java.util.List;
import java.util.Map;

/**
 * Data source interface for articles.
 * Any class that provides articles (RSS feeds, local files, mock data) implements this.
 * Makes it easy to swap out where articles come from without changing the rest of the app.
 */
public interface ArticleDatabase {

    /** Returns all articles mapped by their ID. */
    Map<Integer, Article> getDatabase();

    /** Returns all articles as a flat list. */
    List<Article> getArticles();
}
