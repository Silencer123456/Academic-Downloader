import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
}
