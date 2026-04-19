package edu.vassar.cmpu203.ecoscoop.src.view;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

/**
 * Interface for the search screen.
 * The user can search articles by keyword, tag, or author and sort the results.
 */
public interface SearchArticleUI {

    interface Listener {
        /** Runs a search; type is "keyword", "tag", or "author". */
        List<Article> onSearchQuery(String query, String type);

        /** Sorts the current results by "date" or "relevance". */
        List<Article> onSortResults(List<Article> results, String criteria);

        /** Opens the article detail view for the tapped card. */
        void onArticleClicked(int id);

        // Navigation tabs
        /** Goes to the article feed tab. */
        void onArticleTabClick();
        /** Goes to the dashboard tab. */
        void onDashBoardClick();
        /** Goes to the profile tab. */
        void onProfileClick();
    }

    /** Sets the listener that handles user events. */
    void setListener(Listener listener);

    /** Shows the search results in the list. */
    void runShowResults(List<Article> results);
}
