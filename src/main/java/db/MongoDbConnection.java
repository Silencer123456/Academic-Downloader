package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.logging.Logger;

/**
 * Holds a db connection to the MongoDB database
 */
public class MongoDbConnection implements DbConnection {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String DB_NAME = "diploma";

    private MongoDatabase mongoDatabase;

    @Override
    public void connect() {
        MongoClient mongoClient = MongoClients.create();
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    @Override
    public void insert(IDbLoadArgs loadArgs) {
        if (!(loadArgs instanceof MongoDbLoadArgs)) {
            LOGGER.severe("The data for loading is not correct. Expected instance of " + MongoDbLoadArgs.class.getName() + ". Exiting now.");
            throw new RuntimeException();
        }
        MongoDbLoadArgs mongoArgs = (MongoDbLoadArgs) loadArgs;

        if (mongoDatabase == null) {
            connect();
        }

        MongoCollection<Document> collection = mongoDatabase.getCollection(mongoArgs.getCollectionName());
        collection.insertMany(mongoArgs.getDocuments());
    }

    @Override
    public void disconnect() {
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
