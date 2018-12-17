import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import xmltojsonconverter.XmlToJsonConverter;
import zipextractor.ZipExtractor;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        new Main();
        //xmltojsonconverter.XmlToJsonConverter xmlToJsonConverter = new xmltojsonconverter.XmlToJsonConverter();
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20080101.xml");
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20180102.xml");

    }

    public Main() {
        convertData();
        //extractData();
        //loadPatent();
    }

    private void convertData() {
        XmlToJsonConverter converter = new XmlToJsonConverter();
        try {
            converter.convertDirectory("F:/DP/Data Extracted/Patent/", "F:/DP/Extracted JSON/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractData() {
        ZipExtractor zipExtractor = new ZipExtractor();
        try {
            zipExtractor.extractDirectory("F:/DP/Data/Patent", "F:/DP/Data Extracted/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadPatents() {
        ZipUrlExtractor zipUrlExtractor = new ZipUrlExtractor();
        try {
            int year = 2014;
            for (int i = 0; i < 14; i++) {
                zipUrlExtractor.extractZipFromUrl("https://bulkdata.uspto.gov/data/patent/grant/redbook/bibliographic/" + year + "/", "F:/DP/Data/Patent/" + year);
                year--;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
