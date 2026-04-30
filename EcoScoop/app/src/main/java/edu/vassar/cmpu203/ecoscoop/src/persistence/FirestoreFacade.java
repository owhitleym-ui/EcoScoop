package edu.vassar.cmpu203.ecoscoop.src.persistence;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import edu.vassar.cmpu203.ecoscoop.src.model.Folder;

/**
 * Class that implements the persistence facade by saving/loading data to/from a Google Cloud
 * Firestore database.
 */

public class FirestoreFacade implements PersistenceFacade {


    private static final String FOLDERS_COLLECTION = "folders";
    private final CollectionReference folderCref = FirebaseFirestore.getInstance().collection(FOLDERS_COLLECTION);

    /**
     * Saves new sale to underlying persistence subsystem.
     *
     * @param folder the folder object to be saved.
     */
   /* public void saveFolder(@NonNull Folder folder) {
        this.folderCref.add(folder.toMap());
    }
*/
    /**
     * Issues a ledger retrieval operation.
     *
     * @param listener the observer to be notified of query result.
     */

    @Override
    public void loadFolder(@NonNull final Listener listener) {
        this.folderCref
                .get()
                .addOnSuccessListener(new OnSuccessListener<>() {
                    // called when data comes back from database
                    @Override
                    public void onSuccess(QuerySnapshot qsnap) {
                        Folder folder = new Folder();
                        Log.i("NextGenPos", "database data received");
                        for (DocumentSnapshot dsnap : qsnap) {
                            //final Article article = Article.fromMap(dsnap.getData());
                            //folder.addArticle(article.getId());
                        }
                        listener.onLedgerReceived(folder);
                    }
                });
    }

    @Override
    public void saveFolder(@NonNull Folder folder) {

    }

    @NonNull
    @Override
    public Folder loadFolder() {
        return null;
    }
}