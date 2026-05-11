package edu.vassar.cmpu203.ecoscoop.src.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.vassar.cmpu203.ecoscoop.src.controller.FeedFetcher;

/**
 * Fetches articles from all configured RSS feeds and implements ArticleDatabase.
 * This is the live data source — swap it out with another ArticleDatabase implementation
 * to change where articles come from (e.g. local files, a mock for tests).
 */
public class ArticleRepository implements ArticleDatabase {

    private final Map<String, Article> database;
    private final List<Article> articles;

    /**
     * Fetches all articles from the configured RSS feeds on construction.
     *
     * @throws Exception if any feed fails to load or parse
     */
    public ArticleRepository() throws Exception {
        Map<String, String> feeds = new LinkedHashMap<>();
        feeds.put("Grist", "https://grist.org/feed/");
        feeds.put("Carbon Brief", "https://www.carbonbrief.org/feed/");
        feeds.put("Earth911", "https://www.earth911.com/feed/");

        FeedFetcher fetcher = new FeedFetcher();
        this.articles = fetcher.fetchAll(feeds);

        Map<String, Article> db = new HashMap<>();
        for (Article a : this.articles) {
            db.put(a.getId(), a);
        }
        this.database = db;
    }

    @Override
    public Map<String, Article> getDatabase() {
        return database;
    }

    @Override
    public List<Article> getArticles() {
        return articles;
    }
}
