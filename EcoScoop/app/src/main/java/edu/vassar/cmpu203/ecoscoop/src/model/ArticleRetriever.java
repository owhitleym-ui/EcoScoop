package edu.vassar.cmpu203.ecoscoop.src.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles article lookup, search, sorting, and folder management.
 * Loads all articles from the database on construction and acts as the main data access point for the Controller.
 */
public class ArticleRetriever {
    public Map<Integer, Article> databaseMap;
    public List<Article> articleList;
    private FolderManager folderManager;

    // Search type constants
    public final String SEARCH_KEYWORD = "keyword";
    public final String SEARCH_TAG     = "tag";
    public final String SEARCH_AUTHOR  = "author";

    // Sort criteria constants
    public final String SORT_RELEVANCE = "relevance";
    public final String SORT_DATE      = "date";
    public final String SORT_RATING    = "rating";
    public final String SORT_TRENDING  = "trending";

    /** Loads articles from all RSS feeds and sets up the folder manager. */
    public ArticleRetriever() throws Exception {
        ArticleDatabase artData = new ArticleDatabase();
        this.databaseMap = artData.getDatabase();
        this.articleList = artData.articles;
        this.folderManager = new FolderManager(this);
    }

    /**
     * Test-only constructor. Accepts pre-built data and bypasses all network calls.
     * Package-private so only test classes in the same package can use it.
     *
     * @param database pre-populated article map (id → article)
     * @param articles pre-populated article list
     */
    ArticleRetriever(Map<Integer, Article> database, List<Article> articles) {
        this.databaseMap = database;
        this.articleList = articles;
        this.folderManager = new FolderManager(this);
    }

    // --- Folder Methods ---

    /** Creates a new empty folder. */
    public Folder createFolder(String name) {
        return folderManager.createFolder(name);
    }

    /** Deletes the folder with the given name. Returns true if it existed. */
    public boolean deleteFolder(String name) {
        return folderManager.deleteFolder(name);
    }

    /** Returns the folder with the given name, or null. */
    public Folder getFolder(String name) {
        return folderManager.getFolder(name);
    }

    /** Saves an article to a folder, creating the folder if needed. */
    public void saveToFolder(int articleId, String folderName) {
        folderManager.saveToFolder(articleId, folderName);
    }

    /** Returns all user-created folders. */
    public List<Folder> getFolders() {
        return folderManager.getFolders();
    }

    /** Returns the article with the given ID, or null if not found. */
    public Article getArticle(int id) {
        return databaseMap.get(id);
    }

    /**
     * Searches all loaded articles by the given query and search type.
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
        ArrayList<Article> results = new ArrayList<>();
        String[] keywords = query.split("\\s+");

        for (Article article : articleList) {
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

        String[] keywords2 = query.split("\\s+");
        results.sort((a, b) -> Integer.compare(
                countKeywordHits(b, keywords2),
                countKeywordHits(a, keywords2)));

        return results;
    }

    /** Returns articles that have at least one tag containing the query string. */
    private List<Article> searchByTag(String query) {
        ArrayList<Article> results = new ArrayList<>();

        for (Article article : articleList) {
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
        ArrayList<Article> results = new ArrayList<>();

        for (Article article : articleList) {
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
     * @return a new sorted list (rating, trending, and relevance not yet implemented)
     */
    public List<Article> sortArticles(List<Article> articles, String criteria) {
        List<Article> sorted = new ArrayList<>(articles);

        switch (criteria.toLowerCase()) {
            case SORT_DATE:
                sorted.sort((a, b) -> b.getSource().getPublishDate()
                        .compareTo(a.getSource().getPublishDate()));
                break;

            case SORT_RATING:
                // NOT YET IMPLEMENTED
                break;

            case SORT_TRENDING:
                // NOT YET IMPLEMENTED
                break;

            case SORT_RELEVANCE:
            default:
                // NOT YET IMPLEMENTED
                break;
        }
        return sorted;
    }

    /** Counts how many of the given keywords appear in the article's title, description, or content. */
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
