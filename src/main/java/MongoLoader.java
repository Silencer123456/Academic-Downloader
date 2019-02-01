import db.DbConnection;
import db.loader.DbInserter;
import db.loader.IDbLoadArgs;
import fileloader.MongoFileLoader;
import utils.DirectoryHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MongoLoader extends DbInserter {

    private String[] extensions = new String[] {"json"};
    private String collectionName;

    public MongoLoader(DbConnection dbConnection, MongoFileLoader fileLoader, String collectionName) {
        super(dbConnection, fileLoader);
        this.collectionName = collectionName;
    }

    @Override
    public void load(String dirPath) throws IOException {
        List<File> files = DirectoryHandler.ListFilesFromDirectory(dirPath, extensions, true);
        for (File file : files) {
            LOGGER.info("Processing " + file.getCanonicalPath());
            //insertFromFile(file);
            IDbLoadArgs loadArgs = fileLoader.load(file);
            LOGGER.finer("Parsing done");

            dbConnection.insert(loadArgs);
        }
    }
}
