import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        ArticleRetriever articleRetriever = new ArticleRetriever();
        articleRetriever.articleList.get(0);
        System.out.println(articleRetriever.articleList.get(0).getId());
    }
}
