package edu.vassar.cmpu203.ecoscoop.src.view;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;

/**
 * Interface for the profile screen.
 * Shows the user's folders of saved articles and their comment history.
 */
public interface ProfileUI {

    interface Listener {
        /** Returns all user-created folders. */
        List<Folder> onGetFolders();
        /** Returns the articles in the named folder. */
        List<Article> onGetFolderContents(String folderName);
        /** Returns comments the user has posted. */
        List<String> onGetUserComments();
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
}
