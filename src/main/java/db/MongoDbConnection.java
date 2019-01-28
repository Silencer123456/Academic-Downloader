package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Holds a db connection to the MongoDB database
 */
public class MongoDbConnection implements DbConnection {

    private static final String DB_NAME = "diploma";

    private MongoDatabase mongoDatabase;

    @Override
    public void connect() {
        MongoClient mongoClient = MongoClients.create();
        mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    @Override
    public void disconnect() {
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
