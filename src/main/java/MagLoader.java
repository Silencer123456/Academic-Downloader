import com.mongodb.client.MongoCollection;
import db.DbConnection;
import db.MongoDbConnection;
import db.loader.DbLoader;
import db.loader.IDbLoadArgs;
import db.loader.MongoDbLoadArgs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.bson.Document;
import utils.DirectoryHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the loader of Microsoft Academic Graph data to the
 * database.
 */
public class MagLoader extends DbLoader {

    private static final String COLLECTION_NAME = "mag";

    public MagLoader(DbConnection dbConnection) {
        super(dbConnection);
    }

    @Override
    public void insertFromFile(File file) throws IOException {
        LOGGER.finer("Parsing file " + file.getCanonicalPath());
        parseJsonByLines(file);
        LOGGER.finer("Parsing done");
    }

    @Override
    public void loadFromDirectory(String dirPath, String[] extensions) throws IOException {
        List<File> files = DirectoryHandler.ListFilesFromDirectory(dirPath, extensions, true);
        for (File file : files) {
            LOGGER.info("Processing " + file.getCanonicalPath());
            insertFromFile(file);
        }
    }

    /**
     * Reads a JSON file line by line and adds them to the MongoDB collection.
     * Used only if the source JSON file contains one document per line.
     * The files are large, parse by 10 000 files increment
     * @param file - File to be loaded to the db
     */
    private void parseJsonByLines(File file) throws IOException {
        List<Document> docs = new ArrayList<>();
        try(LineIterator fileContents = FileUtils.lineIterator(file, StandardCharsets.UTF_8.name())) {
            while(fileContents.hasNext()) {
                Document doc = Document.parse( fileContents.nextLine());
                docs.add(doc);
                if (docs.size() == 10000) {
                    IDbLoadArgs loadArgs;
                    if (dbConnection instanceof MongoDbConnection) {
                        loadArgs = new MongoDbLoadArgs(COLLECTION_NAME, docs);
                    } else {
                        LOGGER.severe("Unknown connection specified. Exiting now.");
                        throw new RuntimeException();
                    }

                    dbConnection.insert(loadArgs);
                    docs.clear();
                }
            }

        } catch (IOException e) {
            LOGGER.warning("Error parsing file " + file.getCanonicalPath());
        }
    }
}
