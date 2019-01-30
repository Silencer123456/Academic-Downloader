package db;

import db.loader.IDbLoadArgs;

/**
 * Specifies the connection to the database
 */
public interface DbConnection {

    /**
     * Connects to the database
     */
    void connect();

    /**
     * Inserts the provided data to the target database.
     * @param loadArgs - The data structure to be loaded into the target database
     */
    void insert(IDbLoadArgs loadArgs);

    /**
     * Disconnects from the database
     */
    void disconnect();
}
