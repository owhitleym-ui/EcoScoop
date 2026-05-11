package edu.vassar.cmpu203.ecoscoop.src.view;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

/**
 * Interface for the article detail screen.
 * The controller handles user actions; the fragment displays the article.
 */
public interface DisplayArticleUI {

    interface Listener {
        // Navigation
        /** Goes back to the previous screen. */
        void onReturnClick();
        /** Goes to the article feed tab. */
        void onArticleTabClick();
        /** Goes to the dashboard tab. */
        void onDashBoardClick();
        /** Goes to the search tab. */
        void onSearchClick();
        /** Goes to the profile tab. */
        void onProfileClick();

        /** Asks the controller to load the article with the given id. */
        void onRequestArticle(String id, DisplayArticleUI ui);
        /** Called when the user saves an article to a folder. */
        void onSaveClick(String id, String folderName);
        /** Called when the user taps the Like button. */
        void onLikeClick(String id);
        /** Called when the user taps the Dislike button. */
        void onDislikeClick(String id);
        /** Called when the user posts a comment. */
        void onCommentSubmit(String id, String comment);
    }

    /** Sets the listener that handles user events. */
    void setListener(Listener listener);
    /** Displays the given article on screen. */
    void runShowArticle(Article article);

}
