import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class XmlToJsonConverter {

    /**
     * Converts a XML document to JSON. It supports dtd entities.
     * @param pathToXmlFile - Path to the XML file
     */
    public String convert(String pathToXmlFile) {
        String jsonString = "";
        try {
            String xml = FileUtils.readFileToString(new File(pathToXmlFile), "utf-8");

            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            jsonString = xmlJSONObj.toString(4);

            System.out.println(jsonString);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an error reading the XML file " + pathToXmlFile + ".");
        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("There was an error converting the XML file " + pathToXmlFile + " to JSON.");
        }

        return jsonString;
    }

    // TODO: make generic xml to json parser for large XML files
    public void convertPatent(String pathToXmlFile) {
        String location = "JSON Data/Patent/";
        try {
            File xmlFile = new File(pathToXmlFile);
            String fileString = FileUtils.readFileToString(xmlFile, "utf-8");

            JSONObject json = XML.toJSONObject(fileString);
            String jsonString = json.toString(4);


            save(jsonString, location + "patent-" + FilenameUtils.getBaseName(pathToXmlFile) + ".json");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertDblpLarge(String pathToXmlFile) {
        String location = "JSON Data/DBLP/";

        int linesPerFile = 1000000;
        StringBuilder fileSb = new StringBuilder();
        String jsonString;
        try {
            LineIterator fileContents= FileUtils.lineIterator(new File(pathToXmlFile), StandardCharsets.UTF_8.name());
            int counter = 0;

            int fileCounter = 0;
            while(fileContents.hasNext()) {
                counter++;
                String line = fileContents.nextLine();
                fileSb.append(line);

                if (counter >= linesPerFile && line.startsWith("</") &&  StringUtils.countMatches(line, "<") < 2) {
                    //if (fileCounter == 0) {
                        fileSb.append("</dblp>");
                    //}
                    JSONObject xmlJSONObj = XML.toJSONObject(fileSb.toString());
                    jsonString = xmlJSONObj.toString(4);

                    save(jsonString, location + "output-" + fileCounter++ + ".json");

                    fileSb = new StringBuilder();
                    fileSb.append("<dblp>");
                    counter = 0;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an error reading the XML file " + pathToXmlFile + ".");
        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("There was an error converting the XML file " + pathToXmlFile + " to JSON.");
        }
    }

    public void save(String json, String filepath) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(filepath)) {
            out.println(json);
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private int getNextFileIndexInDirectory(String pathToDirectory) {
        File folder = new File(pathToDirectory);
        File[] listOfFiles = folder.listFiles();

        int fileIndex = 0;
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                int lastDash = file.getName().lastIndexOf('-');
                int lastDot = file.getName().lastIndexOf('.');
                fileIndex = Integer.parseInt(file.getName().substring(lastDash+1, lastDot));
            }
        }
        return ++fileIndex;
    }
}
