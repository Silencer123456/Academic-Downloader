import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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

    public void convertLarge(String pathToXmlFile) {
        String jsonString = "";
        try {
            StringBuilder s = new StringBuilder();
            LineIterator fileContents= FileUtils.lineIterator(new File(pathToXmlFile), StandardCharsets.UTF_8.name());
            int counter = 0;

            int fileCounter = 0;
            while(fileContents.hasNext()) {
                counter++;
                String line = fileContents.nextLine();
                s.append(line);

                if (counter >= 1000000 && line.startsWith("</")) {
                    JSONObject xmlJSONObj = XML.toJSONObject(s.toString());
                    jsonString = xmlJSONObj.toString(4);

                    save(jsonString, "output-" + fileCounter++ + ".json");

                    s.setLength(0);
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
}
