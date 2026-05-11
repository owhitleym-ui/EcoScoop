package edu.vassar.cmpu203.ecoscoop.src.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Handles article lookup, search, and sorting.
 * Reads article data from an ArticleDatabase — does not store a duplicate copy.
 * Falls back to savedArticles (loaded from Firestore) for IDs not in the live feed.
 */
public class ArticleRetriever {

    private final ArticleDatabase database;
    private final Map<String, Article> savedArticles = new HashMap<>();

    // Search type constants
    public static final String SEARCH_KEYWORD = "keyword";
    public static final String SEARCH_TAG     = "tag";
    public static final String SEARCH_AUTHOR  = "author";

    // Sort criteria constants
    public static final String SORT_RELEVANCE = "relevance";
    public static final String SORT_DATE      = "date";
    public static final String SORT_RATING    = "rating";
    public static final String SORT_TRENDING  = "trending";

    /** Creates the retriever backed by the given data source. */
    public ArticleRetriever(ArticleDatabase database) {
        this.database = database;
    }

    /** Returns List of Articles in Database */
    public List<Article> returnDatabase() {return database.getArticles();}

    /** Returns Database Size  */
    public int getDatabaseSize() {return database.getArticles().size();}

    /** Returns the article with the given UUID, checking the live feed then saved articles. */
    public Article getArticle(String id) {
        Article a = database.getDatabase().get(id);
        if (a == null) a = savedArticles.get(id);
        return a;
    }

    /** Merges Firestore-loaded articles into the fallback map so saved folders still open. */
    public void injectSavedArticles(Map<String, Article> saved) {
        savedArticles.putAll(saved);
    }

    /**
     * Searches all articles by the given query and search type.
     *
     * @param query      the search string entered by the user
     * @param searchType one of "keyword", "tag", or "author"
     * @return list of matching articles, sorted by relevance for keyword searches
     */
    public List<Article> searchArticles(String query, String searchType) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase().trim();

        switch (searchType.toLowerCase()) {
            case SEARCH_TAG:
                return searchByTag(lowerQuery);
            case SEARCH_AUTHOR:
                return searchByAuthor(lowerQuery);
            case SEARCH_KEYWORD:
            default:
                return searchByKeyword(lowerQuery);
        }
    }

    /** Returns articles whose title, description, or content contain any of the query keywords, sorted by hit count. */
    private List<Article> searchByKeyword(String query) {
        List<Article> results = new ArrayList<>();
        String[] keywords = query.split("\\s+");

        for (Article article : database.getArticles()) {
            String title       = article.getTitle().toLowerCase();
            String description = article.getDescription().toLowerCase();
            String content     = article.getContent().toLowerCase();

            int matchCount = 0;
            for (String keyword : keywords) {
                if (title.contains(keyword)
                        || description.contains(keyword)
                        || content.contains(keyword)) {
                    matchCount++;
                }
            }

            if (matchCount > 0) {
                results.add(article);
            }
        }

        results.sort((a, b) -> Integer.compare(
                countKeywordHits(b, keywords),
                countKeywordHits(a, keywords)));

        return results;
    }

    /** Returns articles that have at least one tag containing the query string. */
    private List<Article> searchByTag(String query) {
        List<Article> results = new ArrayList<>();

        for (Article article : database.getArticles()) {
            for (Tag tag : article.getTagList()) {
                if (tag.getName().toLowerCase().contains(query)) {
                    results.add(article);
                    break;
                }
            }
        }
        return results;
    }

    /** Returns articles written by an author whose name contains the query string. */
    private List<Article> searchByAuthor(String query) {
        List<Article> results = new ArrayList<>();

        for (Article article : database.getArticles()) {
            for (Author author : article.getAuthors()) {
                if (author.getName().toLowerCase().contains(query)) {
                    results.add(article);
                    break;
                }
            }
        }
        return results;
    }

    /**
     * Returns a sorted copy of the given article list.
     *
     * @param articles the list to sort
     * @param criteria one of "date", "rating", "trending", or "relevance"
     * @return a new sorted list
     */
    public List<Article> sortArticles(List<Article> articles, String criteria) {
        List<Article> sorted = new ArrayList<>(articles);

        switch (criteria.toLowerCase()) {
            case SORT_DATE:
                sorted.sort((a, b) -> b.getSource().getPublishDate()
                        .compareTo(a.getSource().getPublishDate()));
                break;
            case SORT_RATING:
            case SORT_TRENDING:
            case SORT_RELEVANCE:
            default:
                break;
        }
        return sorted;
    }

    /** Counts how many keywords appear in the article's title, description, or content. */
    private int countKeywordHits(Article article, String[] keywords) {
        String title       = article.getTitle().toLowerCase();
        String description = article.getDescription().toLowerCase();
        String content     = article.getContent().toLowerCase();

        int hits = 0;
        for (String kw : keywords) {
            if (title.contains(kw) || description.contains(kw) || content.contains(kw)) {
                hits++;
            }
        }
        return hits;
    }
}
