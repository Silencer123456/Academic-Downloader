import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import db.DbConnection;
import db.loader.DbLoader;
import db.MongoDbConnection;
import log.MyLogger;
import org.bson.Document;
import scripts.xmltojsonconverter.XmlToJsonConverter;
import scripts.zipextractor.ZipHandler;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            MyLogger.setup("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Main();
        //scripts.xmltojsonconverter.XmlToJsonConverter xmlToJsonConverter = new scripts.xmltojsonconverter.XmlToJsonConverter();
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20080101.xml");
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20180102.xml");

    }

    private static final String DB_NAME = "diploma";

    public Main() {
        //convertData();
        //extractData();
        //loadPatent();
        try {
            loadMag();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ZipHandler zipHandler = new ZipHandler();
        try {
            zipHandler.extractDirectory("F:/DP/Data/Patent", "F:/DP/Data Extracted/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadPatents() {
        ZipHandler zipUrlExtractor = new ZipHandler();
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

    private void loadPatent() throws IOException {
        DbConnection mongoConnection = new MongoDbConnection();
        mongoConnection.connect();
        DbLoader patentLoader = new PatentLoader(mongoConnection);
        patentLoader.loadFromDirectory("E:/Patent/2018", new String[]{"json"});
    }

    private void loadDblp() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);

        MongoCollection<Document> collection = database.getCollection("dblp");

        MongoTest mongoTest = new MongoTest();
        for (int i = 0; i < 40; i++) {
            mongoTest.addDblpToCollection("output-" + i + ".json", collection);
        }
    }

    private void loadMag() throws IOException {
        DbConnection mongoConnection = new MongoDbConnection();
        mongoConnection.connect();
        DbLoader magLoader = new MagLoader(mongoConnection);
        for (int i = 2; i < 8; i++) {
            magLoader.loadFromDirectory("E:/MAG/" + i, new String[]{"txt"});
        }
    }
}
