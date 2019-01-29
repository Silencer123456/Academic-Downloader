package db;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Abstract class for loading data to the database.
 */
public abstract class DbLoader {
    protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    protected DbConnection dbConnection;

    public DbLoader(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Inserts to the target database.
     * @param filePath - Path to the file to be loaded into the database
     * @throws IOException if the file loading fails
     */
    public abstract void insertFromFile(String filePath) throws IOException;
}
