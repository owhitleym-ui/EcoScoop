import java.util.ArrayList;

/**
 * Simple test entry point that prints all article summaries to the console.
 * Use Controller.main() to run the full interactive app.
 */
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
