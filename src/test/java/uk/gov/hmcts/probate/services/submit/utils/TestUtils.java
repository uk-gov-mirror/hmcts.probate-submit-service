package uk.gov.hmcts.probate.services.submit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class TestUtils {

    public String getJSONFromFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("src/test/resources", fileName)));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public JsonNode getJsonNodeFromFile(String fileName) {
        try {
            return new ObjectMapper().readTree(getJSONFromFile(fileName));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    public Map<String, JsonNode> getJsonMapFromFile(String fileName) {
        try {
            String json = getJSONFromFile (fileName);
            return new ObjectMapper().readValue(json, new TypeReference<HashMap<String, JsonNode>>(){});
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

