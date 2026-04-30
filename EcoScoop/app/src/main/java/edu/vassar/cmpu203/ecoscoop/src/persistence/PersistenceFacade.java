package edu.vassar.cmpu203.ecoscoop.src.persistence;

import androidx.annotation.NonNull;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;

public interface PersistenceFacade {

    void loadFolder(@NonNull Listener listener);

    interface Listener {

        /**
         * Called when the receiver has been fully loaded/received.
         *
         * @param folder the ledger that was just received
         */
        void onLedgerReceived(@NonNull Folder folder);
    }

    /**
     * Issues a ledger save operation.
     * @param folder the ledger to be saved.
     */
    void saveFolder(@NonNull Folder folder);

    /**
     * Issues a ledger retrieval operation.
     * @return the retrieved ledger.
     */
    @NonNull
    Folder loadFolder();
}
