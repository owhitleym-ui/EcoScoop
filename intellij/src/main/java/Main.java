import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        Map<String, String> feeds = new LinkedHashMap<>();
        feeds.put("Earth911", "https://earth911.com/feed/");
        feeds.put("Grist", "https://grist.org/feed/");
        feeds.put("Inside Climate News",   "https://insideclimatenews.org/feed/");
        feeds.put("Yale Climate Connections", "https://yaleclimateconnections.org/feed/");


        FeedFetcher fetcher = new FeedFetcher();
        ArrayList<Article> articles = fetcher.fetchAll(feeds);

        ArticleDatabase database = new ArticleDatabase();
        database.saveArticles(articles);

        for (Article article : articles) {
            System.out.println(article);
            System.out.println("--------------------------------------------------");
        }
    }
}
