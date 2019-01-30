import db.DbConnection;
import db.loader.DbLoader;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of the loader of Microsoft Academic Graph data to the
 * database.
 */
public class MagLoader extends DbLoader {

    public MagLoader(DbConnection dbConnection) {
        super(dbConnection);
    }

    @Override
    public void insertFromFile(File file) throws IOException {

    }

    @Override
    public void loadFromDirectory(String dirPath, String[] extensions) throws IOException {

    }
}
