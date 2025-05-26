package az.qrfood.backend.category;

import az.qrfood.backend.dto.Category;
import az.qrfood.backend.dto.Dish;
import az.qrfood.backend.util.TestDataLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeAll;
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
    String BASE_URL;
    @Value("${segment.api.eatery}")
    String segmentApiCategory;
    @Value("${segment.api.dish}")
    String segmentApiDish;
    List<Category> dishes;

     @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("categories.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
        dishes = TestDataLoader.loadJsonListFromResource(
                "dishes.json",
                new TypeReference<>() {});
    }

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
                .statusCode(anyOf(is(200), is(201)))
                .log().body();
    }

    @Test
    void getCategoriesForEatery() {
        given()
                .when()
                .get("/api/category/eatery/1")
                .then()
                .statusCode(200)
                .log().body();
    }

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

    @Test
    void getAllCategories() {
        given()
                .when()
                .get("/api/category")
                .then()
                .statusCode(200)
                .log().body();
    }

    @Test
    void getCategoryById() {
        given()
                .when()
                .get("/api/category/2")
                .then()
                .statusCode(200)
                .log().body();
    }

    @Test
    void deleteCategory() {
        given()
                .when()
                .delete("/api/category/34")
                .then()
                .statusCode(anyOf(is(200), is(204)))
                .log().body();
    }

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
}
