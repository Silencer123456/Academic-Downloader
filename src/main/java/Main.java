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
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        //XmlToJsonConverter xmlToJsonConverter = new XmlToJsonConverter();
        //xmlToJsonConverter.convertLarge("dblp.xml");

//        try {
//            xmlToJsonConverter.save(json, "output.json");
//        } catch (FileNotFoundException e) {
//            System.err.println("Error saving file.");
//            e.printStackTrace();
//        }

        MongoTest mongoTest = new MongoTest();
        mongoTest.addMagToCollection("mag_papers_0.txt");


    }
}
