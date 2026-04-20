package edu.vassar.cmpu203.ecoscoop.src.view;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

/**
 * Interface for the profile screen.
 * Shows the user's saved articles and handles nav button clicks.
 */
public interface ProfileUI {

    interface Listener {
        /** Returns all articles the user has saved across all folders. */
        List<Article> onGetSavedArticles();
        /** Opens the detail view for the tapped article. */
        void onArticleClicked(int id);
        /** Goes to the article feed tab. */
        void onArticleTabClick();
        /** Goes to the dashboard tab. */
        void onDashBoardClick();
        /** Goes to the search tab. */
        void onSearchClick();
    }

    /** Sets the listener that handles user events. */
    void setListener(Listener listener);
    /** Shows the given list of saved articles. */
    void runShowSavedArticles(List<Article> articles);
}
