import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.HashMap;

public class ArticleRetriever {
    public HashMap<Integer, Article> databaseMap;
    public ArrayList<Article> articleList;


    public ArticleRetriever() throws Exception {
        ArticleDatabase artData = new ArticleDatabase();
        this.databaseMap = artData.getDatabase();
        this.articleList = artData.articles;
    }

    public Article getArticle(int id){
        return databaseMap.get(id);
    }

}
