package edu.vassar.cmpu203.ecoscoop.src.persistence;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.User;

public class LocalStorageFacade implements PersistenceFacade {

    private static final String FOLDER_MANAGER_FNAME = "folder_manager.ngp";

    private final File folderManagerFile;

    public LocalStorageFacade(Context context) {
        this.folderManagerFile = new File(context.getFilesDir(), FOLDER_MANAGER_FNAME);
    }

    /**
     * Saves the folder manager to the underlying persistence subsystem.
     *
     * @param folderManager the folder manager to be saved.
     */
    @Override
    public void saveFolderManager(@NonNull FolderManager folderManager) {
        try (FileOutputStream foStream = new FileOutputStream(folderManagerFile);
             ObjectOutputStream ooStream = new ObjectOutputStream(foStream)) {
            ooStream.writeObject(folderManager);
        } catch (IOException e) {
            Log.e("EcoScoop", String.format("I/O error writing to %s", folderManagerFile), e);
        }
    }

    /**
     * Issues a folder manager retrieval operation.
     *
     * @param listener the observer to be notified of query result.
     */
    @Override
    public void loadFolderManager(@NonNull final DataListener<FolderManager> listener) {
        if (!folderManagerFile.isFile()) {
            listener.onNoDataFound();
            return;
        }
        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(folderManagerFile))) {
            FolderManager folderManager = (FolderManager) oiStream.readObject();
            listener.onDataReceived(folderManager);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("EcoScoop", String.format("Exception while reading folder manager: %s", e.getMessage()), e);
            listener.onNoDataFound();
        }
    }

    /**
     * Creates an entry for the specified user if one does not already exist.
     *
     * @param user the user to create.
     * @param listener the observer to be notified of query result.
     */
    @Override
    public void createUserIfNotExists(@NonNull User user, @NonNull BinaryResultListener listener) {
        // TODO: implement
    }

    /**
     * Retrieves the user with the specified username.
     *
     * @param username the username of the user to retrieve.
     * @param listener the observer to be notified of query result.
     */
    @Override
    public void loadUser(@NonNull String username, @NonNull DataListener<User> listener) {
        // TODO: implement
    }
}
