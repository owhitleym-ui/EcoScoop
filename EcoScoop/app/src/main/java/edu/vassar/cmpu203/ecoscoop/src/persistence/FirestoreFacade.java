package edu.vassar.cmpu203.ecoscoop.src.persistence;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.User;

/**
 * Class that implements the persistence facade by saving/loading data to/from a Google Cloud
 * Firestore database.
 */
public class FirestoreFacade implements PersistenceFacade {

    private static final String FOLDERS_COLLECTION = "folders";
    private static final String USERS_COLLECTION = "users";

    private final CollectionReference folderCref = FirebaseFirestore.getInstance().collection(FOLDERS_COLLECTION);
    private final CollectionReference usersCref = FirebaseFirestore.getInstance().collection(USERS_COLLECTION);

    /**
     * Saves the folder manager to the underlying persistence subsystem.
     *
     * @param folderManager the folder manager to be saved.
     */
    @Override
    public void saveFolderManager(@NonNull FolderManager folderManager) {
        // TODO: implement Firestore save
    }

    /**
     * Issues a folder manager retrieval operation.
     *
     * @param listener the observer to be notified of query result.
     */
    @Override
    public void loadFolderManager(@NonNull DataListener<FolderManager> listener) {
        // TODO: implement Firestore load
        listener.onNoDataFound();
    }

    /**
     * Retrieves the user with the specified username from Firestore.
     *
     * @param username the username of the user to retrieve.
     * @param listener the observer to be notified of query result.
     */
    @Override
    public void loadUser(@NonNull String username, @NonNull DataListener<User> listener) {
        this.usersCref
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dsnap) {
                        if (dsnap.exists()) {
                            User user = User.fromMap(dsnap.getData());
                            listener.onDataReceived(user);
                        } else {
                            listener.onNoDataFound();
                        }
                    }
                });
    }

    /**
     * Creates an entry for the specified user in Firestore if one does not already exist.
     *
     * @param user the user to be created.
     * @param listener the observer to be notified of query result. onYesResult() is called if a
     *                 new user was created. onNoResult() is called if the username already existed.
     */
    @Override
    public void createUserIfNotExists(@NonNull User user, @NonNull BinaryResultListener listener) {
        this.loadUser(user.getUsername(), new DataListener<User>() {
            @Override
            public void onDataReceived(@NonNull User data) {
                listener.onNoResult();
            }

            @Override
            public void onNoDataFound() {
                usersCref.document(user.getUsername())
                        .set(user.toMap())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                listener.onYesResult();
                            }
                        });
            }
        });
    }
}
