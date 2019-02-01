package fileloader;

import db.loader.IDbLoadArgs;

import java.io.File;
import java.io.IOException;

/**
 * Class from loading the data from file system
 */
public interface FileLoader {
    IDbLoadArgs load(File file) throws IOException;
}
