import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import utils.xmltojsonconverter.XmlToJsonConverter;
import utils.zipextractor.ZipHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        new Main();
        //utils.xmltojsonconverter.XmlToJsonConverter xmlToJsonConverter = new utils.xmltojsonconverter.XmlToJsonConverter();
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20080101.xml");
        //xmlToJsonConverter.convertPatent("XML Data/ipgb20180102.xml");

    }

    private static final String DB_NAME = "diploma";

    public Main() {
        //convertData();
        //extractData();
        //loadPatent();
        try {
            loadPatent("E:/Patent");
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
        ZipUrlDownloader zipUrlExtractor = new ZipUrlDownloader();
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

    private void loadPatent(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (dir.isFile()) {
            System.err.println("The path " + dirPath + " is not a directory.");
            return;
        }

        String[] extensions = new String[] { "json" };
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);

        MongoCollection<Document> collection = database.getCollection("patent");
        MongoTest mongoTest = new MongoTest();

        for (File file : files) {
            System.out.println("Processing " + file.getCanonicalPath());
            mongoTest.addPatentToCollection(file.getCanonicalPath(), collection);
            System.out.println(collection.countDocuments());
        }

        //mongoTest.addPatentToCollection("JSON Data/Patent/patent-ipgb20180102.json", collection);
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

    private void loadMag(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (dir.isFile()) {
            System.err.println("The path " + dirPath + " is not a directory.");
            return;
        }

        String[] extensions = new String[] { "txt" };   // MAGs are txt files
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("diploma");

        MongoCollection<Document> collection = database.getCollection("publications");
        MongoTest mongoTest = new MongoTest();

        for (File file : files) {
            System.out.println("Processing " + file.getCanonicalPath());
            mongoTest.parseJsonByLines(file.getCanonicalPath(), collection);
            System.out.println(collection.countDocuments());
        }
    }
}
