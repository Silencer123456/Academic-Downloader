import com.mongodb.BasicDBList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Main {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        XmlToJsonConverter converter = new XmlToJsonConverter();
        String json = converter.convertToJson("dblpSample.xml");

        MongoCollection<Document> collection = database.getCollection("test");

        Document doc = Document.parse(json);

        collection.insertOne(doc);


    }
}
