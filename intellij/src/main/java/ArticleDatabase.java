import org.xmlpull.v1.XmlPullParserException;
import java.util.ArrayList;
import java.util.HashMap;

public class ArticleDatabase {
    public final HashMap<Integer, Article> database = new HashMap<>();
    public ArrayList<Article> articles;
    ArticleParser app;

    public ArticleDatabase() throws XmlPullParserException {
         app = new ArticleParser();
        }

    public HashMap<Integer, Article> getDatabase(){
        this.articles = app.loadArticles();

        for (Article a : articles) {
            database.put(a.getId(), a);
        }

        return this.database;
    }
}
