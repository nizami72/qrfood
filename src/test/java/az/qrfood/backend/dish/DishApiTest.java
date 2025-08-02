package az.qrfood.backend.dish;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dto.Category;
import az.qrfood.backend.dto.CategoryDto;
import az.qrfood.backend.dto.Dish;
import az.qrfood.backend.util.TestDataLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.api.category}")
    String segmentApiCategory;
    @Value("${segment.api.dish}")
    String segmentApiDish;
    @Value("${eatery.id.category.id.dish}")
    String eateryIdCategoryIdDish;
    @Value("${eatery.id.category.id.dish.id}")
    String eateryIdCategoryIdDishId;
    @Value("${auth.refresh}")
    String uriRefreshToken;

    List<Category> dishes;
    String jwtToken;
    Long userId;
    Long eateryId = 1L; // Default eatery ID for tests
    Long categoryId = 1L; // Default category ID for tests
    Long dishId; // Will be set during test execution

    String email = "NatalieReed@qaz.az";
    String password = "qqqq1111";

    @BeforeAll
    void setup() throws Exception {
        // Setup logging
        fileLog = new PrintStream(new FileOutputStream("logs/test/dish.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );

        // Load test data
        dishes = TestDataLoader.loadJsonListFromResource(
                "dishes.json",
                new TypeReference<>() {
                });

        // Authenticate
        String authPayload = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        Response authResponse = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(authPayload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        jwtToken = authResponse.jsonPath().getString("jwt");
        userId = authResponse.jsonPath().getLong("userId");

        log.debug("Authenticated as user ID: {}", userId);

        // Refresh token with eateryId context if needed
//        Response refreshResponse = given()
//                .baseUri(baseUrl)
//                .contentType("application/json")
//                .body(new LoginRequest(eateryId))
//                .when()
//                .post(uriRefreshToken)
//                .then()
//                .statusCode(200)
//                .extract()
//                .response();

        // Update token with the refreshed one that includes eatery context
//        String refreshedToken = refreshResponse.jsonPath().getString("jwt");
//        if (refreshedToken != null && !refreshedToken.isEmpty()) {
//            jwtToken = refreshedToken;
//            log.debug("Token refreshed with eatery context: {}", eateryId);
//        }
    }


    @Test
    @Order(1)
    void shouldCreateCategory() {
        fileLog.println("\n========== 📤 Creating a category ==========");

        Category category = dishes.get(0);
        String json = TestDataLoader.serializeToJsonString(
                new CategoryDto(category.nameAz(), category.nameEn(), category.nameRu(), category.image()));

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .multiPart("data", "data.json", json.getBytes(StandardCharsets.UTF_8), "application/json")
                .multiPart("image", new File("src/test/resources/image/" + category.image()))
                .when()
                .post("/api/eatery/" + eateryId + "/category")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        categoryId = Long.parseLong(response.getBody().asString());
        log.debug("Created category ID: {}", categoryId);

        fileLog.println("========== 📥 Category created successfully ==========\n");
    }

    @Test
    @Order(2)
    void shouldCreateDish() {
        fileLog.println("\n========== 📤 Creating a dish ==========");

        Dish dish = dishes.get(0).dishes().get(0);
        String json = TestDataLoader.serializeToJsonString(dish);

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .multiPart("data", "data.json", json.getBytes(StandardCharsets.UTF_8), "application/json")
                .multiPart("image", new File("src/test/resources/image/" + dish.image()))
                .when()
                .post(eateryIdCategoryIdDish.replace("{eateryId}", eateryId.toString())
                                           .replace("{categoryId}", categoryId.toString()))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        dishId = Long.parseLong(response.getBody().asString());
        log.debug("Created dish ID: {}", dishId);

        fileLog.println("========== 📥 Dish created successfully ==========\n");
    }


    @Test
    @Order(3)
    void shouldGetDishById() {
        fileLog.println("\n========== 📤 Getting dish by ID ==========");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(eateryIdCategoryIdDishId.replace("{eateryId}", eateryId.toString())
                                           .replace("{categoryId}", categoryId.toString())
                                           .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        fileLog.println("========== 📥 Dish retrieved successfully ==========\n");
    }

    @Test
    @Order(4)
    void shouldGetAllDishesInCategory() {
        fileLog.println("\n========== 📤 Getting all dishes in category ==========");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(eateryIdCategoryIdDish.replace("{eateryId}", eateryId.toString())
                                          .replace("{categoryId}", categoryId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        fileLog.println("========== 📥 Dishes retrieved successfully ==========\n");
    }

    @Test
    @Order(6)
    void shouldDeleteDishById() {
        fileLog.println("\n========== 📤 Deleting dish by ID ==========");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(eateryIdCategoryIdDishId.replace("{eateryId}", eateryId.toString())
                                              .replace("{categoryId}", categoryId.toString())
                                              .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        fileLog.println("========== 📥 Dish deleted successfully ==========\n");
    }

    @Test
    @Order(5)
    void shouldUpdateDish() {
        fileLog.println("\n========== 📤 Updating dish ==========");

        String json = TestDataLoader.serializeToJsonString(
                DishDto.builder()
                        .dishId(dishId)
                        .categoryId(categoryId)
                        .nameAz("Updated Test Dish Az")
                        .nameEn("Updated Test Dish En")
                        .nameRu("Updated Test Dish Ru")
                        .price(BigDecimal.valueOf(15.99))
                        .descriptionAz("Updated Dish Description Az")
                        .descriptionEn("Updated Dish Description En")
                        .descriptionRu("Updated Dish Description Ru")
                        .isAvailable(true)
                        .build());

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .multiPart("data", "data.json", json.getBytes(StandardCharsets.UTF_8), "application/json")
                .when()
                .put(eateryIdCategoryIdDishId.replace("{eateryId}", eateryId.toString())
                                           .replace("{categoryId}", categoryId.toString())
                                           .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        fileLog.println("========== 📥 Dish updated successfully ==========\n");
    }

    @Test
    @Order(7)
    void shouldTestInterceptorValidation() {
        fileLog.println("\n========== 📤 Testing interceptor validation ==========");

        // Try to access a dish with incorrect eateryId (should be rejected by interceptor)
        Long incorrectEateryId = eateryId + 100;

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(eateryIdCategoryIdDishId.replace("{eateryId}", incorrectEateryId.toString())
                                           .replace("{categoryId}", categoryId.toString())
                                           .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(403); // Expecting forbidden due to interceptor validation

        fileLog.println("========== 📥 Interceptor validation test completed ==========\n");
    }

    @Test
    @Order(8)
    void shouldGetCommonDishes() {
        fileLog.println("\n========== 📤 Getting common dishes ==========");

        // Test the CommonDishController endpoint
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/dish/common/Breakfast")
                .then()
                .log().all()
                .statusCode(200);

        fileLog.println("========== 📥 Common dishes retrieved successfully ==========\n");
    }

    @Test
    @Order(9)
    void shouldCreateDishesFromTemplates() {
        fileLog.println("\n========== 📤 Creating dishes from templates ==========");

        // Create a simple request body with common dish templates
        String requestBody = """
                [
                  {
                    "nameAz": "Template Dish Az",
                    "nameEn": "Template Dish En",
                    "nameRu": "Template Dish Ru",
                    "descriptionAz": "Template Description Az",
                    "descriptionEn": "Template Description En",
                    "descriptionRu": "Template Description Ru",
                    "price": 12.99,
                    "image": "dish1.jpg"
                  }
                ]
                """;

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/dish/common/" + categoryId)
                .then()
                .log().all()
                .statusCode(200);

        fileLog.println("========== 📥 Dishes created from templates successfully ==========\n");
    }

}
