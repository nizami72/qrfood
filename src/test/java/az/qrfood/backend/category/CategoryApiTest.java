package az.qrfood.backend.category;

import az.qrfood.backend.dto.Category;
import az.qrfood.backend.dto.Dish;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.api.eateries}")
    String segmentApiCategory;
    @Value("${segment.api.dish}")
    String segmentApiDish;
    List<Category> dishes;
    String jwtToken;
    Long userId;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/categories.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeEach
    void login() {
        String authPayload = """
                {
                  "email": "nizami.budagov@gmail.com",
                  "password": "qqqq1111"
                }
                """;

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

    }

    // ==================== GET Methods ====================

    /**
     * GET test for retrieving all categories.
     */
    @Test
    void getAllCategories() {
        given()
                .when()
                .get("/api/category")
                .then()
                .statusCode(200)
                .log().body();
    }

    /**
     * GET test for retrieving a category by its ID.
     */
    @Test
    void getCategoryById() {
        given()
                .when()
                .get("/api/category/2")
                .then()
                .statusCode(200)
                .log().body();
    }

    /**
     * GET test for retrieving categories for a specific eatery.
     */
    @Test
    void getCategoriesForEatery() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/eateries/2/categories")
                .then()
                .log().all()
                .statusCode(200)
                .log().body();
    }

    // ==================== POST Methods ====================

    /**
     * POST test for creating a category with image.
     */
    @Test
    void createCategoryWithImage() {
        File image = new File("src/test/resources/image/soup.webp");
        String json;
        List<Dish> d = dishes.get(1).dishes();

        given()
                .multiPart("data", "{\n" +
                        "  \"nameAz\": \"Şorba\",\n" +
                        "  \"nameEn\": \"Soup\",\n" +
                        "  \"nameRu\": \"Суп\"\n" +
                        "}", "application/json")
                .multiPart("image", image, "image/webp")
                .contentType("multipart/form-data")
                .when()
                .post("/api/category/create/eatery/1")
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(201)))
                .log().body();
    }

    /**
     * POST test for adding a category to an eatery without an image.
     */
    @Test
    void addCategoryToEateryWithoutImage() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nameAz\": \"a\",\n" +
                        "  \"nameEn\": \"b\",\n" +
                        "  \"nameRu\": \"c\"\n" +
                        "}")
                .when()
                .post("/api/category/create/eatery/22")
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .log().body();
    }

    /**
     * POST test for adding a category with random data.
     */
    @Test
    void addCategoryRandomly() {
        int eateryId = 10;
        String nameAz = "Yeni Kateqoriya";
        String nameEn = "New Category";
        String nameRu = "Новая Категория";

        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nameAz\": \"" + nameAz + "\",\n" +
                        "  \"nameEn\": \"" + nameEn + "\",\n" +
                        "  \"nameRu\": \"" + nameRu + "\"\n" +
                        "}")
                .when()
                .post("/api/category/create/eatery/" + eateryId)
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .log().body();
    }

    // ==================== DELETE Methods ====================

    /**
     * DELETE test for removing a category.
     */
    @Test
    void deleteCategory() {
        given()
                .when()
                .delete("/api/category/34")
                .then()
                .statusCode(anyOf(is(200), is(204)))
                .log().body();
    }
}
