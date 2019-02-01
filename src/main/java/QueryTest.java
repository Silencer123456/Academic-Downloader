import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import db.MongoDbConnection;
import org.bson.Document;

public class QueryTest {

    private MongoDbConnection dbConnection;
    public QueryTest(MongoDbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void testFullText() {
        DBObject searchCommand = new BasicDBObject(
                "$text", new BasicDBObject("$search", "keyword")
        );

        //DBCursor result = dbConnection.getCollection("patent").find(searchCommand);
        //collection.find(eq("i", 71)).first();
        MongoCollection<Document> collection = dbConnection.getCollection("patent");
        FindIterable<Document> docs = collection.find(Filters.text("USD0500415"));
        for (Document doc : docs) {
            System.out.println(doc.toString());
        }
    }
}
