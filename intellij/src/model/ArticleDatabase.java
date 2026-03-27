package model;

import org.xmlpull.v1.XmlPullParserException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the collection of articles fetched from RSS feeds.
 * getDatabase() triggers the network fetch and returns all articles mapped by ID.
 */
public class ArticleDatabase {
    public final HashMap<Integer, Article> database = new HashMap<>();
    public ArrayList<Article> articles;
    ArticleParser app;

    /** Sets up the XML parser used to process feed data. */
    public ArticleDatabase() throws XmlPullParserException {
         app = new ArticleParser();
        }

    /**
     * Fetches articles from all configured RSS feeds and returns them as a map of ID to model.Article.
     *
     * @throws Exception if a feed fails to load or parse
     */
    public HashMap<Integer, Article> getDatabase() throws Exception{
        Map<String, String> feeds = new LinkedHashMap<>();
        feeds.put("Grist", "https://grist.org/feed/");
        feeds.put("Carbon Brief", "https://www.carbonbrief.org/feed/");


        FeedFetcher fetcher = new FeedFetcher();
        this.articles = fetcher.fetchAll(feeds);

        for (Article a : articles) {
            database.put(a.getId(), a);
        }

        return this.database;
    }

    /** Adds a list of articles into the database map by their IDs. */
    public void saveArticles(ArrayList<Article>  articles){
        for (Article a : articles) {
            database.put(a.getId(), a);
        }
    }
}
