package edu.vassar.cmpu203.ecoscoop.src.persistence;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.User;

/**
 * Implements persistence via Google Cloud Firestore.
 * Folders are stored as a subcollection under each user's document:
 *   users/{username}/folders/{folderName}
 */
public class FirestoreFacade implements PersistenceFacade {

    private static final String USERS_COLLECTION = "users";
    private static final String FOLDERS_SUB = "folders";
    private static final String ARTICLES_COLLECTION = "articles";

    private final CollectionReference usersCref =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION);

    private String currentUsername;

    @Override
    public void setCurrentUser(@NonNull String username) {
        this.currentUsername = username;
    }

    @Override
    public void saveFolderManager(@NonNull FolderManager folderManager) {
        if (currentUsername == null) return;
        CollectionReference foldersRef = usersCref.document(currentUsername).collection(FOLDERS_SUB);
        for (Folder folder : folderManager.getFolders()) {
            foldersRef.document(folder.getFolderName()).set(folder.toMap());
        }
    }

    @Override
    public void loadFolderManager(@NonNull DataListener<FolderManager> listener) {
        if (currentUsername == null) {
            listener.onNoDataFound();
            return;
        }
        usersCref.document(currentUsername).collection(FOLDERS_SUB)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {
                        if (snapshots.isEmpty()) {
                            listener.onNoDataFound();
                            return;
                        }
                        List<Folder> folders = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            if (doc.getData() != null) {
                                folders.add(Folder.fromMap(doc.getData()));
                            }
                        }
                        FolderManager fm = new FolderManager(null);
                        fm.restoreFolders(folders);
                        listener.onDataReceived(fm);
                    }
                });
    }

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

    @Override
    public void saveUser(@NonNull User user) {
        usersCref.document(user.getUsername()).set(user.toMap());
    }

    @Override
    public void saveArticle(@NonNull Article article) {
        FirebaseFirestore.getInstance()
                .collection(ARTICLES_COLLECTION)
                .document(article.getId())
                .set(article.toMap());
    }

    @Override
    public void loadSavedArticles(@NonNull DataListener<Map<String, Article>> listener) {
        FirebaseFirestore.getInstance()
                .collection(ARTICLES_COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {
                        if (snapshots.isEmpty()) {
                            listener.onNoDataFound();
                            return;
                        }
                        Map<String, Article> articles = new HashMap<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            if (doc.getData() != null) {
                                Article article = Article.fromMap(doc.getData());
                                articles.put(article.getId(), article);
                            }
                        }
                        listener.onDataReceived(articles);
                    }
                });
    }
}
