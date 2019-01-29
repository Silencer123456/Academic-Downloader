import db.DbConnection;
import db.DbLoader;

/**
 * Implementation of the loader of Microsoft Academic Graph data to the
 * database.
 */
public class MagLoader extends DbLoader {

    public MagLoader(DbConnection dbConnection) {
        super(dbConnection);
    }

    @Override
    public void load() {

    }
}
