package db;

/**
 * Specifies the connection to the database
 */
public interface DbConnection {

    /**
     * Connects to the database
     */
    void connect();

    /**
     * Disconnects from the database
     */
    void disconnect();
}
