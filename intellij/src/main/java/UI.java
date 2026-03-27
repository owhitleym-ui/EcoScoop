import java.util.ArrayList;

public interface UI {

    interface Listener {
        // Navigation Methods
        void onViewArticleTab();

        // Article Display Methods
        void onGetArticle(int id);
        void onDisplayArticle(Article article);
        void onChooseArticle();
        void onDisplayArticleList();

        // Article Search Methods
        void onSearchArticles();
        ArrayList<Article> onSearchQuery(String query, String type);
        ArrayList<Article> onSortResults(ArrayList<Article> results, String criteria);



    }

    // Interface - Run Methods

    // Pre Run Methods
    void runMainMenu();
    void setListener(final Listener listener);

    // Navigation Methods
    void runArticleTab();

    // Article Display Methods
    void runDisplayArticle(Article article);
    void runChooseArticle();
    void runDisplayArticleList(ArrayList<Article> articles);

    // Article Search Methods
    void runSearchArticles();
    void runSearchInput();
    void runDisplaySearchResults(ArrayList<Article> results);
    void runSortOptions(ArrayList<Article> results);



}
