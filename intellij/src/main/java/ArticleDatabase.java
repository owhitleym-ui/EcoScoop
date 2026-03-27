import org.xmlpull.v1.XmlPullParserException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArticleDatabase {
    public final HashMap<Integer, Article> database = new HashMap<>();
    public ArrayList<Article> articles;
    ArticleParser app;

    public ArticleDatabase() throws XmlPullParserException {
         app = new ArticleParser();
        }

    public HashMap<Integer, Article> getDatabase() throws Exception{
        Map<String, String> feeds = new LinkedHashMap<>();
        feeds.put("Earth911", "https://earth911.com/feed/");
        feeds.put("Grist", "https://grist.org/feed/");
        feeds.put("Inside Climate News",   "https://insideclimatenews.org/feed/");


        FeedFetcher fetcher = new FeedFetcher();
        this.articles = fetcher.fetchAll(feeds);

        for (Article a : articles) {
            database.put(a.getId(), a);
        }

        return this.database;
    }

    public void saveArticles(ArrayList<Article>  articles){
        for (Article a : articles) {
            database.put(a.getId(), a);
        }
    }
}
