import db.DbConnection;
import db.loader.DbInserter;
import fileloader.ElasticFileLoader;

import java.io.IOException;

public class ElasticLoader extends DbInserter {

    public ElasticLoader(DbConnection dbConnection, ElasticFileLoader fileLoader) {
        super(dbConnection, fileLoader);
    }

    @Override
    public void load(String dirPath) throws IOException {

    }
}
