package db.loader;

import db.DbConnection;
import fileloader.FileLoader;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Abstract class for inserting data to the database.
 */
public abstract class DbInserter {
    protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    protected DbConnection dbConnection;
    protected FileLoader fileLoader;

    public DbInserter(DbConnection dbConnection, FileLoader fileLoader) {
        this.dbConnection = dbConnection;
        this.fileLoader = fileLoader;
    }

    public abstract void load(String dirPath) throws IOException;

//    /**
//     * Inserts to the target database.
//     * @param file - File to be loaded into the database
//     * @throws IOException if the file loading fails
//     */
//    public abstract void insertFromFile(File file) throws IOException;
//
//    /**
//     * Loads all the files with the specified extension from the directory (including
//     * its subdirectories).
//     * @param dirPath - The directory path from which to get the files
//     * @param extensions - The extensions of the files to get
//     * @throws IOException if there was error reading or accessing files
//     */
//    public abstract void loadFromDirectory(String dirPath, String[] extensions) throws IOException;
}
