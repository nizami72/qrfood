package az.qrfood.backend.johnkimbereatery;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.dto.CategoryDto;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.util.TestDataLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * Test class that loads JSON data from CatAndDishes.json and uses REST API endpoints
 * to create a user, eatery, categories, and dishes.
 * 
 * NOTE: This test requires the application to be running on http://localhost:8081
 * before executing the test. It makes real HTTP requests to the API endpoints.
 * 
 * To run the application:
 * 1. Start the MySQL database
 * 2. Run the main application (az.qrfood.backend.App)
 * 3. Then run this test
 */
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JohnKimberEateryTest {

    private static PrintStream fileLog;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // API endpoints
    @Value("${localhost}")
    private String baseUrl;
    @Value("${admin.api.eatery}")
    private String fullAdminEatery;
    @Value("${eatery.id.category.id.dish}")
    private String urlPostDish;
    @Value("${eatery}")
    private String segmentEateries;
    @Value("${category}")
    private String componentCategories;

    private String jwtToken;
    private Long userId;
    private CatAndDishes kimberCredsAndEatery;

    @BeforeAll
    void setupLoggingAndLoadData() throws Exception {
        // Setup logging
        fileLog = new PrintStream(new FileOutputStream("logs/test/johnKimberEatery.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeAll
    void loadTestDate() throws Exception{

        // Load JSON data from file
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("fakeData/JohnKimberEatery/CatAndDishes.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: fakeData/JohnKimberEateryTest/CatAndDishes.json");
        }
        kimberCredsAndEatery = objectMapper.readValue(inputStream, CatAndDishes.class);
        log.debug("Loaded JSON data: {}", kimberCredsAndEatery);
    }

    @Test
    void createUserEateryCategoriesAndDishes() {
        fileLog.println("\n========== 📤 Starting John Kimber Eatery data creation ==========");

        // Step 1: Create a user
        RegisterRequest registerRequest = createRegisterRequest(kimberCredsAndEatery.getUser());
        Response registerResponse = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(registerRequest)
                .when()
                .post(fullAdminEatery)
                .then()
                .statusCode(201)
                .extract()
                .response();

        Long eateryId = ((Integer) registerResponse.as(Map.class).get("eateryId")).longValue();
        log.debug("Registered user [{}] with eateryId [{}]", kimberCredsAndEatery.getUser().getEmail(), eateryId);

        // Step 2: Login user
        login(kimberCredsAndEatery.getUser().getEmail(), kimberCredsAndEatery.getUser().getPassword(), eateryId);

        // Process each eatery from the JSON
        kimberCredsAndEatery.getEateries().forEach(eateryData -> {
            // Step 3: Create Eatery
            Map<String, Object> eateryRequestBody = Map.of(
                    "name", eateryData.getName(),
                    "address", eateryData.getAddress(),
                    "phones", eateryData.getPhones(),
                    "geoLat", eateryData.getGeoLat(),
                    "geoLng", eateryData.getGeoLng(),
                    "numberOfTables", eateryData.getNumberOfTables(),
                    "ownerProfileId", userId
            );

            Response eateryResponse = given()
                    .baseUri(baseUrl)
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType("application/json")
                    .body(eateryRequestBody)
                    .when()
                    .post(segmentEateries)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            Long createdEateryId = Long.valueOf(eateryResponse.getBody().asString());
            log.debug("Created eatery [{}] with ID [{}]", eateryData.getName(), createdEateryId);

            // Refresh token to change the eatery context
            login(kimberCredsAndEatery.getUser().getEmail(), kimberCredsAndEatery.getUser().getPassword(), createdEateryId);

            // Step 4: Create categories and dishes
            eateryData.getCategories().forEach(categoryData -> {
                // Create category
                String categoryJson = TestDataLoader.serializeToJsonString(
                        new CategoryDto(
                                null,
                                null,
                                categoryData.getNameAz(),
                                categoryData.getNameEn(),
                                categoryData.getNameRu(),
                                categoryData.getImage()
                        )
                );

                Response categoryResponse = given()
                        .baseUri(baseUrl)
                        .header("Authorization", "Bearer " + jwtToken)
                        .multiPart("data", "data.json", categoryJson.getBytes(StandardCharsets.UTF_8), "application/json")
                        .multiPart("image", new File("src/test/resources/image/" + categoryData.getImage()))
                        .when()
                        .post(segmentEateries + "/" + createdEateryId + componentCategories)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

                String createdCategoryId = categoryResponse.getBody().asString();
                log.debug("Created category [{}] with ID [{}]", categoryData.getNameEn(), createdCategoryId);

                // Create dishes for this category
                categoryData.getDishes().forEach(dishData -> {
                    String dishJson = TestDataLoader.serializeToJsonString(dishData);
                    String url = urlPostDish
                            .replace("{eateryId}", createdEateryId.toString())
                            .replace("{categoryId}", createdCategoryId);

                    Response dishResponse = given()
                            .baseUri(baseUrl)
                            .header("Authorization", "Bearer " + jwtToken)
                            .multiPart("data", "data.json", dishJson.getBytes(StandardCharsets.UTF_8), "application/json")
                            .multiPart("image", new File("src/test/resources/image/" + dishData.getImage()))
                            .when()
                            .post(url)
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();

                    String createdDishId = dishResponse.getBody().asString();
                    log.debug("Created dish [{}] with ID [{}]", dishData.getNameEn(), createdDishId);
                });
            });
        });

        fileLog.println("========== 📥 John Kimber Eatery data creation completed successfully ==========\n");
    }

    /**
     * Creates a RegisterRequest from the user data in the JSON file
     */
    private RegisterRequest createRegisterRequest(CatAndDishes.User user) {
        return RegisterRequest.builder()
                .user(RegisterRequest.UserDto.builder()
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .roles(Set.of(Role.fromString(user.getRoles().get(0))))
                        .build())
                .restaurant(RegisterRequest.RestaurantDto.builder()
                        .name("Initial Restaurant")
                        .build())
                .userProfileRequest(RegisterRequest.UserProfileRequest.builder()
                        .name(user.getName())
                        .phone(user.getPhone())
                        .build())
                .build();
    }

    /**
     * Logs in the user and gets a JWT token
     */
    private void login(String email, String password, Long eateryId) {
        LoginRequest loginRequest = new LoginRequest(email, password, eateryId);
        Response authResponse = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        jwtToken = authResponse.jsonPath().getString("jwt");
        userId = authResponse.jsonPath().getLong("userId");
        log.debug("New JWT for user [{}] was created", userId);
    }
}
