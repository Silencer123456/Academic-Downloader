package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import db.loader.IDbLoadArgs;
import db.loader.MongoDbLoadArgs;
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
        LOGGER.info("Connecting to the MongoDB database " + DB_NAME);
        MongoClient mongoClient = MongoClients.create();
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    @Override
    public void insert(IDbLoadArgs loadArgs) {
        if (!(loadArgs instanceof MongoDbLoadArgs)) {
            LOGGER.severe("The data for loading is not correct. Expected instance of " + MongoDbLoadArgs.class.getName() + ". Exiting now.");
            throw new RuntimeException();
        }

        if (mongoDatabase == null) {
            connect();
        }

        InsertManyOptions options = new InsertManyOptions();
        options.ordered(false);

        MongoDbLoadArgs mongoArgs = (MongoDbLoadArgs) loadArgs;

        MongoCollection<Document> collection = mongoDatabase.getCollection(mongoArgs.getCollectionName());
        LOGGER.finer("Beginning insert");
        collection.insertMany(mongoArgs.getDocuments(), options);
        LOGGER.finer("Inserting done");
        LOGGER.info("Total documents: " + collection.countDocuments());
    }

    @Override
    public void disconnect() {
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
