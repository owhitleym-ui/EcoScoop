package edu.vassar.cmpu203.ecoscoop.src.persistence;

import android.util.Log;
import android.content.Context;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;

public class LocalStorageFacade implements PersistenceFacade {

    private final File folderFile;
    private static final String FOLDER_FNAME = "folder.ngp";

    public LocalStorageFacade(Context context) {
        this.folderFile = new File(context.getFilesDir(),FOLDER_FNAME);
    }

    /**
     * Issues a ledger retrieval operation.
     *
     * @return the retrieved ledger.
     */
    @Override
    public void saveFolder(@NonNull Folder folder) {
        try {
            FileOutputStream fostream = new FileOutputStream(folderFile);
            ObjectOutputStream oostream = new ObjectOutputStream(fostream);
            oostream.writeObject(folderFile);
        }
        catch (IOException e){
            final String emsg = String.format("I/O error writing to %s", folderFile);
            Log.e("EcoScoop", emsg, e);
        }
    }

    @NonNull
    @Override
    public Folder loadFolder() {
        Folder folder = new Folder(); // empty to begin with for negative outcome

        if (folderFile.isFile()) { // must check that the file actually exists
            try {
                FileInputStream fistream = new FileInputStream(folderFile);
                ObjectInputStream oistream = new ObjectInputStream(fistream);
                folder = (Folder) oistream.readObject(); // must downcast from Object

            } catch (IOException e) {
                final String emsg = String.format("I/O error reading from %s", folderFile);
                Log.e("NextGen POS", emsg, e);

            } catch (ClassNotFoundException e) {
                final String emsg = String.format("Can't find class of object from %s", folderFile);
                Log.e("NextGen POS", emsg, e);
            }
        }
        return folder;
    }
}
