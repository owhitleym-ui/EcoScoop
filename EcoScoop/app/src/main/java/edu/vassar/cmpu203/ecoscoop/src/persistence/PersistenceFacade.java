package edu.vassar.cmpu203.ecoscoop.src.persistence;

import androidx.annotation.NonNull;

import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.User;

public interface PersistenceFacade {

    interface DataListener<T> {
        /**
         * Called when the requested data is successfully received.
         * @param data the data that was received from the persistence subsystem.
         */
        void onDataReceived(@NonNull T data);

        /**
         * Called when the requested data isn't found in the underlying persistence subsystem.
         */
        void onNoDataFound();
    }

    /**
     * Interface that classes interested in being notified of binary (i.e., true vs false) events
     * from the persistence layer should implement.
     */
    interface BinaryResultListener {
        /**
         * Called when the answer to the issued query is positive.
         */
        void onYesResult();
        /**
         * Called when the answer to the issued query is negative.
         */
        void onNoResult();
    }

    /**
     * Saves the folder manager (and all folders within it) to the underlying persistence subsystem.
     *
     * @param folderManager the folder manager to be saved.
     */
    void saveFolderManager(@NonNull final FolderManager folderManager);

    /**
     * Issues a folder manager retrieval operation.
     *
     * @param listener the observer to be notified of query result.
     */
    void loadFolderManager(@NonNull final DataListener<FolderManager> listener);

    /**
     * Creates an entry for the specified user if one does not already exist.
     *
     * @param user the user to create.
     * @param listener the observer to be notified of query result. onYesResult() is called if a
     *                 new user was created. onNoResult() is called if the username already exists.
     */
    void createUserIfNotExists(@NonNull User user, @NonNull BinaryResultListener listener);

    /**
     * Retrieves the user with the specified username.
     *
     * @param username the username of the user to retrieve.
     * @param listener the observer to be notified of query result.
     */
    void loadUser(@NonNull String username, @NonNull DataListener<User> listener);
}
