import java.util.ArrayList;
import java.util.HashMap;

public class ArticleRetriever {
    public HashMap<Integer, Article> databaseMap;
    public ArrayList<Article> articleList;
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

    public ArticleRetriever() throws Exception {
        ArticleDatabase artData = new ArticleDatabase();
        this.databaseMap = artData.getDatabase();
        this.articleList = artData.articles;
        this.folderManager = new FolderManager(this);
    }

    // --- Folder delegation methods ---

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
    public ArrayList<Folder> getFolders() {
        return folderManager.getFolders();
    }

    public Article getArticle(int id) {
        return databaseMap.get(id);
    }

    public ArrayList<Article> searchArticles(String query, String searchType) {
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

    private ArrayList<Article> searchByKeyword(String query) {
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

    private ArrayList<Article> searchByTag(String query) {
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

    private ArrayList<Article> searchByAuthor(String query) {
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

    public ArrayList<Article> sortArticles(ArrayList<Article> articles, String criteria) {
        ArrayList<Article> sorted = new ArrayList<>(articles);

        switch (criteria.toLowerCase()) {
            case SORT_DATE:
                sorted.sort((a, b) -> b.getSource().getPublishDate()
                        .compareTo(a.getSource().getPublishDate()));
                break;

            case SORT_RATING:
                // sorted.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                break;

            case SORT_TRENDING:
                // sorted.sort((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()));
                break;

            case SORT_RELEVANCE:
            default:
                // already relevance-sorted from the search step
                break;
        }
        return sorted;
    }

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
