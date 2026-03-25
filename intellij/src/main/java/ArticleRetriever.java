import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.HashMap;

public class ArticleRetriever {
    public HashMap<Integer, Article> databaseMap;
    public ArrayList<Article> articleList;


    public ArticleRetriever() throws XmlPullParserException {
        this.databaseMap = new ArticleDatabase().database;
        this.articleList = new ArticleDatabase().articles;
    }

    public Article getArticle(int id){
        return databaseMap.get(id);
    }

}
