package edu.vassar.cmpu203.ecoscoop.src.view;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

/**
 * Interface for the article feed screen.
 * The controller listens for user actions, the fragment shows the data.
 */
public interface ArticleFeedUI {

    interface Listener {
        // Navigation
        /** Goes to the article feed tab. */
        void onArticleTabClick();
        /** Goes to the dashboard tab. */
        void onDashBoardClick();
        /** Goes to the search tab. */
        void onSearchClick();
        /** Goes to the profile tab. */
        void onProfileClick();

        /** Called when the user taps an article card. */
        void onArticleClicked(String id);
        /** Called to load articles into the feed. */
        void onShowFeed(List<Article> ArticleList, ArticleFeedUI ui);
    }

    /** Sets the listener that handles user events. */
    void setListener(Listener listener);

    /** Shows the given list of articles in the feed. */
    void runShowFeed(List<Article> ArticleList);
    /** Called after an article card is tapped; currently handled by the controller. */
    void runArticleClicked(String id);
}
