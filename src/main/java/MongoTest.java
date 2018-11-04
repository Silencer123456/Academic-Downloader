import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoTest {

    List<String> publicationTypes = new ArrayList<>(Arrays.asList("book", "phdthesis"));

    public void addToCollection() {

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        List<Document> docs = new ArrayList<>();

        try {
            String json = FileUtils.readFileToString(new File("output.json"), "utf-8");
            JSONObject dblp = new JSONObject(json);

            JSONObject dblpRoot = dblp.getJSONObject("dblp");

            for (String publicationType : publicationTypes) {
                JSONArray arr = dblpRoot.getJSONArray(publicationType);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject record = arr.getJSONObject(i);
                    docs.add(Document.parse(record.toString()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        MongoCollection<Document> collection = database.getCollection("test");
        collection.insertMany(docs);
    }

    public void addMagToCollection(String json) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        List<Document> docs = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("test");
        try {
            LineIterator fileContents= FileUtils.lineIterator(new File(json), StandardCharsets.UTF_8.name());
            int count = 0;
            while(fileContents.hasNext()) {
            //for (int i = 0; i < 1000; i++) {
                Document doc = Document.parse( fileContents.nextLine());
                docs.add(doc);
                if (count == 100000) {
                    collection.insertMany(docs);
                    docs.clear();
                    count = 0;
                }
                count++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
