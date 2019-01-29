import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.mongodb.client.MongoCollection;
import db.*;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the loader of patent data to the database.
 */
public class PatentLoader extends DbLoader {

    private static final String COLLECTION_NAME = "patent";

    public PatentLoader(DbConnection dbConnection) {
        super(dbConnection);
    }

    @Override
    public void insertFromFile(String filePath) throws IOException {
        List<Document> docs = parseFileStreaming(filePath);

        IDbLoadArgs loadArgs;
        if (dbConnection instanceof MongoDbConnection) {
            loadArgs = new MongoDbLoadArgs(COLLECTION_NAME, docs);
        }
        else {
            LOGGER.severe("Unknown connection specified. Exiting now.");
            throw new RuntimeException();
        }

        dbConnection.insert(loadArgs);
    }

    /**
     * TODO: Move to separate class, so that this class does not depend on the concrete file loading (JSON here)
     * Parses the file and from its contents creates a list of documents to be added to the database.
     * @param filePath - Path to the file to be parsed
     * @return - List of parsed documents to be added to the database
     */
    private List<Document> parseFileStreaming(String filePath) throws IOException {
        JsonFactory factory = new MappingJsonFactory();
        JsonParser parser = factory.createParser(new File(filePath));

        JsonToken current = parser.nextToken();
        if (current != JsonToken.START_OBJECT) {

        }
    }

    /**
     * Adds a patent file to the MongoDB collection.
     * @param jsonFile - The path to the JSON file containing the patent data
     * @param collection - The target MongoDB collection
     */
    // TODO: make generic method!!!
    public void addPatentToCollection(String jsonFile, MongoCollection<Document> collection) {
        List<Document> docs = new ArrayList<>();
        try {
            String json = FileUtils.readFileToString(new File(jsonFile), "utf-8");
            /*JSONObject patent = new JSONObject(json);

            JSONArray patentRoot = patent.getJSONArray("us-patent-grant");

            for (int i = 0; i < patentRoot.length(); i++) {
                JSONObject record = patentRoot.getJSONObject(i);
                docs.insert(Document.parse(record.toString()));
            }

            collection.insertMany(docs);
            System.out.println(collection.countDocuments());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
