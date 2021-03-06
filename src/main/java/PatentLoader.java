import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.*;
import db.loader.DbLoader;
import db.loader.IDbLoadArgs;
import db.loader.MongoDbLoadArgs;
import org.bson.Document;
import utils.DirectoryHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the loader of patent data to the database.
 */
public class PatentLoader extends DbLoader {

    private static final String COLLECTION_NAME = "test";

    PatentLoader(DbConnection dbConnection) {
        super(dbConnection);
    }

    // TODO: Maybe remove from parent and make private
    @Override
    public void insertFromFile(File file) throws IOException {
        LOGGER.finer("Parsing file " + file.getCanonicalPath());
        List<Document> docs = parseFileStreaming(file);
        LOGGER.finer("Parsing done");

        if (docs.isEmpty()) {
            LOGGER.warning("List is empty, skipping...");
            return;
        }

        IDbLoadArgs loadArgs;
        if (dbConnection instanceof MongoDbConnection) {
            loadArgs = new MongoDbLoadArgs(COLLECTION_NAME, docs);
        } else {
            LOGGER.severe("Unknown connection specified. Exiting now.");
            throw new RuntimeException();
        }

        dbConnection.insert(loadArgs);
    }

    @Override
    public void loadFromDirectory(String dirPath, String[] extensions) throws IOException {
        List<File> files = DirectoryHandler.ListFilesFromDirectory(dirPath, extensions, true);
        for (File file : files) {
            LOGGER.info("Processing " + file.getCanonicalPath());
            insertFromFile(file);
        }
    }

    /**
     * TODO: Move to separate class, so that this class does not depend on the concrete file loading (JSON here)
     * Streams the file and from its contents creates a list of documents to be added to the database.
     *
     * @param file - File to be parsed
     * @return - List of parsed documents to be added to the database. If there is an error parsing
     * the file, an empty list is returned
     */
    private List<Document> parseFileStreaming(File file) throws IOException {
        List<Document> documents = new ArrayList<>();

        JsonFactory factory = new MappingJsonFactory();
        try (JsonParser parser = factory.createParser(file)) {
            JsonToken current = parser.nextToken();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                current = parser.nextToken();
                if (fieldName.equals("us-patent-grant")) {
                    if (current == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            JsonNode node = parser.readValueAsTree();
                            preprocessNode(node);
                            documents.add(Document.parse(node.toString()));
                        }
                    }
                }
            }
        } catch (JsonParseException e) {
            LOGGER.warning("Error parsing file " + file.getCanonicalPath());
            return Collections.emptyList();
        }

        return documents;
    }

    // TODO: Read from mapping file
    private void preprocessNode(JsonNode node) {
        JsonNode titleNode = node.at("/us-bibliographic-data-grant/invention-title/content");
        JsonNode abstractNode = node.at("/abstract/p/content");
        StringBuilder abstractText = new StringBuilder();
        if (abstractNode.isArray()) { // Sometimes the abstract is separated by new line into array. Do not know why
            for (JsonNode curNode : abstractNode) {
                abstractText.append(curNode.textValue());
            }
        } else {
            abstractText.append(abstractNode.textValue());
        }

        JsonNode yearNode = node.at("/date-publ");
        JsonNode authorsNode = node.at("/us-bibliographic-data-grant/us-parties/inventors/inventor");

        List<String> authors = new ArrayList<>();
        if (authorsNode.isArray()) {
            for (JsonNode authorNode : authorsNode) {
                JsonNode firstName = authorNode.at("/addressbook/first-name");
                JsonNode lastName = authorNode.at("/addressbook/last-name");
                authors.add(firstName.textValue() + " " + lastName.textValue());
            }
        } else {
            JsonNode firstName = authorsNode.at("/addressbook/first-name");
            JsonNode lastName = authorsNode.at("/addressbook/last-name");
            authors.add(firstName.textValue() + " " + lastName.textValue());
        }

        ((ObjectNode)node).put("title", titleNode.textValue());

        if (!abstractText.toString().equals("null")) {
            ((ObjectNode)node).put("abstract", abstractText.toString());
        }
        ((ObjectNode)node).put("year", yearNode.toString().substring(0, 4));

        //ArrayNode authorsArray = new ArrayNode(factory);
        ArrayNode authorsArray = ((ObjectNode)node).putArray("authors");

        JsonNodeFactory f = JsonNodeFactory.instance;
        for (String authorName : authors) {
            ObjectNode att = f.objectNode();
            att.put("name", authorName);
            authorsArray.add(att);
        }
    }
}
