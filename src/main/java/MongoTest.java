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
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

    public void addDblpToCollection(String jsonFile, MongoCollection<Document> collection) {
        List<Document> docs = new ArrayList<>();
        try {
            String json = FileUtils.readFileToString(new File(jsonFile), "utf-8");
            JSONObject dblp = new JSONObject(json);

            JSONObject dblpRoot = dblp.getJSONObject("dblp");

            Iterator<?> keys = dblpRoot.keys();
            while(keys.hasNext() ) {
                String key = (String)keys.next();
                if ( dblpRoot.get(key) instanceof JSONArray ) {
                    JSONArray array = new JSONArray(dblpRoot.get(key).toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject record = array.getJSONObject(i);
                        record.put("type", key);    // Add the information about the publication type
                        docs.add(Document.parse(record.toString()));
                    }

                    collection.insertMany(docs);
                    docs.clear();
                    System.out.println(collection.countDocuments());
                }
            }
        } catch (Exception e) {
            System.err.println("Key: ");
            e.printStackTrace();
        }
    }

    // TODO: make generic method!!!
    public void addPatentToCollection(String jsonFile, MongoCollection<Document> collection) {
        List<Document> docs = new ArrayList<>();
        try {
            String json = FileUtils.readFileToString(new File(jsonFile), "utf-8");
            JSONObject patent = new JSONObject(json);

            JSONArray patentRoot = patent.getJSONArray("us-patent-grant");

            for (int i = 0; i < patentRoot.length(); i++) {
                JSONObject record = patentRoot.getJSONObject(i);
                docs.add(Document.parse(record.toString()));
            }

            collection.insertMany(docs);
            docs.clear();
            System.out.println(collection.countDocuments());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJsonByLines(String jsonFile, MongoCollection<Document> collection) {
        List<Document> docs = new ArrayList<>();
        try(LineIterator fileContents = FileUtils.lineIterator(new File(jsonFile), StandardCharsets.UTF_8.name())) {
            int count = 0;
            while(fileContents.hasNext()) {
                Document doc = Document.parse( fileContents.nextLine());
                docs.add(doc);
                if (count == 100000) {
                    collection.insertMany(docs);
                    System.out.println(collection.countDocuments());
                    docs.clear();
                    count = 0;
                }
                count++;
            }
            collection.insertMany(docs);
            docs.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
