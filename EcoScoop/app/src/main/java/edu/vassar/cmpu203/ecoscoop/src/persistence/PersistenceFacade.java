package edu.vassar.cmpu203.ecoscoop.src.persistence;

import androidx.annotation.NonNull;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;

public interface PersistenceFacade {

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
