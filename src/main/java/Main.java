import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        new Main();
        //XmlToJsonConverter xmlToJsonConverter = new XmlToJsonConverter();
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20180102.xml");

    }

    public Main() {
        loadPatent();
    }

    private void loadPatent() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        MongoCollection<Document> collection = database.getCollection("patent");
        MongoTest mongoTest = new MongoTest();
        mongoTest.addPatentToCollection("JSON Data/Patent/patent-ipgb20180102.json", collection);

    }

    private void loadDblp() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        MongoCollection<Document> collection = database.getCollection("dblp");

        MongoTest mongoTest = new MongoTest();
        for (int i = 0; i < 40; i++) {
            mongoTest.addDblpToCollection("output-" + i + ".json", collection);
        }
    }

    private void loadMag() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        MongoCollection<Document> collection = database.getCollection("dblp");
        MongoTest mongoTest = new MongoTest();
        mongoTest.parseJsonByLines("mag_papers_0.txt", collection);
    }
}
