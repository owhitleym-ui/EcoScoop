import java.util.ArrayList;

public interface UI {

    interface Listener {
        // Navigation Methods
        void onViewArticleTab();

        // Article Tab Methods
        void onGetArticle(int id);
        void onDisplayArticle(Article article);
        void onChooseArticle();
        void onDisplayArticleList();

    }

    // Interface - Run Methods

    // Pre Run Methods
    void runMainMenu();
    void setListener(final Listener listener);

    // Navigation Methods
    void runArticleTab();

    // Article Tab Methods
    void runDisplayArticle(Article article);
    void runChooseArticle();
    void runDisplayArticleList(ArrayList<Article> articles);



}
