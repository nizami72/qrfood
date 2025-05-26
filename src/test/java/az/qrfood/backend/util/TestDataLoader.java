package az.qrfood.backend.util;

import az.qrfood.backend.dto.Eatery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestDataLoader {

    static ObjectMapper objectMapper = new ObjectMapper();

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

    /**
     * Загружает список объектов из JSON файла, расположенного в ресурсах приложения (classpath).
     *
     * @param resourcePath Путь к JSON файлу в ресурсах (например, "eateries.json").
     * @param typeReference TypeReference, указывающий на тип данных, в который нужно десериализовать JSON.
     * @param <T> Тип элементов списка.
     * @return Список объектов указанного типа.
     * @throws RuntimeException Если ресурс не найден или произошла ошибка при загрузке/парсинге JSON.
     */
    public static <T> List<T> loadJsonListFromResource(String resourcePath, TypeReference<List<T>> typeReference) {
        InputStream inputStream = TestDataLoader.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        return loadJsonListFromStream(inputStream, typeReference);
    }

    /**
     * Загружает список объектов из JSON файла, переданного в виде InputStream.
     *
     * @param inputStream InputStream, содержащий JSON-данные.
     * @param typeReference TypeReference, указывающий на тип данных, в который нужно десериализовать JSON (например, new TypeReference<List<MyClass>>() {}).
     * @param <T> Тип элементов списка.
     * @return Список объектов указанного типа.
     * @throws RuntimeException Если произошла ошибка при загрузке или парсинге JSON.
     */
    public static <T> List<T> loadJsonListFromStream(InputStream inputStream, TypeReference<List<T>> typeReference) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try (inputStream) {
            try {
                return objectMapper.readValue(inputStream, typeReference);
            } catch (IOException e) {
                throw new RuntimeException("Error loading JSON data from stream: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            System.err.println("Error closing input stream: " + e.getMessage());
        }
        return List.of();
    }

    /**
     * Serializes a given object to a JSON file.
     *
     * @param object The object to serialize.
     * @param <T> The type of the object.
     * @throws RuntimeException if an error occurs during serialization or file writing.
     */
    public static <T> String serializeToJsonString(T object) {
        if (object == null) {
            return null; // Or throw IllegalArgumentException, depending on desired behavior
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // Обертываем JsonProcessingException в RuntimeException,
            // чтобы вызывающий код мог обработать его или позволить ему распространиться.
            // В реальном приложении здесь лучше использовать специализированное исключение
            // или логировать ошибку более подробно.
            throw new RuntimeException("Error serializing object to JSON: " + e.getMessage(), e);
        }
    }

}
