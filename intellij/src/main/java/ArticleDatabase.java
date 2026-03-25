import java.util.ArrayList;

public class ArticleDatabase {
    private ArrayList<Article> articleList;
    private ArrayList<Integer> articleIds;

    public ArticleDatabase() {
        articleList = new ArrayList<>();
        articleIds = new ArrayList<>();
    }

    public void saveArticles(ArrayList<Article> articles){
        articleList.addAll(articles);

        for(Article article : articleList){
            articleIds.add(article.getId());
        }
    }
}
