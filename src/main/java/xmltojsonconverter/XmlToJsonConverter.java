package xmltojsonconverter;

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
import java.util.List;
import java.util.stream.Stream;

public class XmlToJsonConverter {

    /**
     * Converts a XML file to JSON.  It supports dtd entities.
     * @param pathToXmlFile - The path to the XML file
     * @param destinationPath - The destination path, where the converted JSON file should be saved.
     *                        The path has to end with the trailing slash '/'.
     * @param overwrite - Flag specifying, whether the existing file in the destination path should be overwritten
     *                  if it exists.
     */
    private void convert(String pathToXmlFile, String destinationPath, boolean overwrite) {
        File xmlFile = new File(pathToXmlFile);
        if (!xmlFile.isFile()) {
            System.err.println("The path " + pathToXmlFile + " is not a file.");
        }
        String nameWithoutExt = FilenameUtils.getBaseName(xmlFile.getName());

        File destFile = new File(destinationPath);
        destFile.mkdirs();

        String jsonFilePath = destinationPath + nameWithoutExt + ".json";

        // If already exists, skip it if overwrite is false
        if (!overwrite && new File(jsonFilePath).exists()) {
            System.out.println("File " + jsonFilePath + " already exists. Skipping...");
            return;
        }

        try {
            String xml = FileUtils.readFileToString(new File(pathToXmlFile), "utf-8");

            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            String jsonString = xmlJSONObj.toString(4);

            save(jsonString, jsonFilePath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an error reading the XML file " + pathToXmlFile + ".");
        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("There was an error converting the XML file " + pathToXmlFile + " to JSON.");
        }
    }

    public void convertDirectory(String pathToDir, String destinationPath) throws IOException {
        // TODO: Extract listing all files to generic method
        File dir = new File(pathToDir);
        if (dir.isFile()) {
            System.err.println("The path " + pathToDir + " is not a directory.");
            return;
        }

        String[] extensions = new String[] { "xml" };
        String dirName = dir.getName();
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);

        for (File file : files) {
            String path = file.getParent();
            path = path.substring(path.lastIndexOf(dirName));
            path = destinationPath + path + "/";

            System.out.println(path);
            convert(file.getCanonicalPath(), path, false);
        }
    }

    public void testConvert(String xmlPath) throws XMLStreamException, FileNotFoundException {
        File xml = new File(xmlPath);
        //String xml = "<root><foo>foo string</foo><bar><x>1</x><y>5</y></bar></root>";
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new FileReader(xml));
        XMLEventWriter writer = new MappedXMLOutputFactory(new HashMap()).createXMLEventWriter(System.out);
        writer.add(reader);
        writer.close();
        reader.close();
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
