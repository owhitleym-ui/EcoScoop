import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        ArticleRetriever retriever = new ArticleRetriever();
        ArrayList<Article> articles = retriever.articleList;

        for (Article article : articles) {
            System.out.println(article.getSummary());
            System.out.println("---------------------------------------------------");
        }
    }
}
