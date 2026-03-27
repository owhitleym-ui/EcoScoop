package view;

import model.Article;

import java.util.List;

/**
 * Interface that all EcoScoop UI implementations must follow.
 * The Listener sub-interface is implemented by the Controller to handle user actions.
 * Each run method represents a screen or prompt the UI needs to be able to display.
 */
public interface UI {

    interface Listener {
        // Navigation Methods
        void onViewArticleTab();

        // model.Article Display Methods

        void onGetArticle(int id);

        void onDisplayArticle(Article article);

        void onChooseArticle();


        void onDisplayArticleList();

        // Article Search Methods
        void onSearchArticles();
        List<Article> onSearchQuery(String query, String type);
        List<Article> onSortResults(List<Article> results, String criteria);

        // model.Folder Methods
        void onSaveToFolder(int articleId, String folderName);

        // React Methods
        void onLikeArticle(int id);
        void onDislikeArticle(int id);
        void onCommentArticle(int id, String comment);

        /** Returns the total number of articles currently loaded. */
        int getArticleCount();



    }

    // Interface - Run Methods

    // Pre Run Methods
    void runMainMenu();
    void setListener(final Listener listener);

    // Navigation Methods
    void runArticleTab();

    // model.Article Display Methods
    void runDisplayArticle(Article article);
    void runChooseArticle();
    void runDisplayArticleList(List<Article> articles);

    // model.Article Search Methods
    void runSearchArticles();
    void runSearchInput();
    void runDisplaySearchResults(List<Article> results);
    void runSortOptions(List<Article> results);



}
