package view;

import model.Article;

import java.util.ArrayList;

/**
 * Interface that all EcoScoop view.UI implementations must follow.
 * The Listener sub-interface is implemented by the controller.Controller to handle user actions.
 * Each run method represents a screen or prompt the view.UI needs to be able to display.
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
        ArrayList<Article> onSearchQuery(String query, String type);
        ArrayList<Article> onSortResults(ArrayList<Article> results, String criteria);

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
    void runDisplayArticleList(ArrayList<Article> articles);

    // model.Article Search Methods
    void runSearchArticles();
    void runSearchInput();
    void runDisplaySearchResults(ArrayList<Article> results);
    void runSortOptions(ArrayList<Article> results);



}
