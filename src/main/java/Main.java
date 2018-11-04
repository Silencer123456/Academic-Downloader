import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("local");

        /*XmlToJsonConverter xmlToJsonConverter = new XmlToJsonConverter();
        String json = xmlToJsonConverter.convert("dblpSample.xml");

        try {
            xmlToJsonConverter.save(json, "output.json");
        } catch (FileNotFoundException e) {
            System.err.println("Error saving file.");
            e.printStackTrace();
        }*/
        List<Document> docs = new ArrayList<>();

        try {
            String json = FileUtils.readFileToString(new File("output.json"), "utf-8");
            JSONObject dblp = new JSONObject(json);

            JSONArray arr = dblp.getJSONArray("phdthesis");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject record = arr.getJSONObject(i);

                docs.add(Document.parse(record.toString()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        MongoCollection<Document> collection = database.getCollection("test");
        collection.insertMany(docs);
    }
}
