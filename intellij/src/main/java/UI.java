import java.util.ArrayList;

public interface UI {

    interface Listener {
        void onChooseArticle(int id);
        void onCancel();
        void onDisplayArticleList(ArrayList<Article> articles);
    }

    void setListener(final Listener listener);

    void runMainMenu();

    void runCancel();

    void runClickArticle(Article article);



}
