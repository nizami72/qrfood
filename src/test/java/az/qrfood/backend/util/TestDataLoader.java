package az.qrfood.backend.util;

import az.qrfood.backend.eatery.Eatery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;

public class TestDataLoader {

    public static List<Eatery> getTestEateriesFromFile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = TestDataLoader.class
                    .getClassLoader()
                    .getResourceAsStream("eateries.json");
            if (inputStream == null) {
                throw new IllegalStateException("File not found eateries.json");
            }
            return objectMapper.readValue(inputStream, new TypeReference<List<Eatery>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error loading json data", e);
        }
    }
}
