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
        /** Removes the comment at the given index and persists the change. */
        void onRemoveComment(int index);
        /** Opens the detail view for the tapped article. */
        void onArticleClicked(String id);
        /** Deletes the named folder and persists the change. */
        void onDeleteFolder(String folderName);
        /** Renames a folder and persists the change. */
        void onRenameFolder(String oldName, String newName);
        /** Removes an article from the named folder and persists the change. */
        void onRemoveArticle(String folderName, String articleId);
        /** Persists the user's display/location settings. */
        void onSettingChanged(boolean useMetric, boolean useLocalLocation);
        /** Returns the user's current unit setting (true = metric). */
        boolean getUserSettingMetric();
        /** Returns the user's current location setting (true = local). */
        boolean getUserSettingLocal();
        /** Returns the user's activity stats: [read, liked, disliked]. */
        int[] getUserStats();
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
