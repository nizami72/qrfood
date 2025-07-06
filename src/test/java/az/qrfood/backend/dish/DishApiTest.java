package az.qrfood.backend.dish;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.dto.Category;
import az.qrfood.backend.dto.CategoryDto;
import az.qrfood.backend.dto.Dish;
import az.qrfood.backend.util.TestDataLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DishApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.api.category}")
    String segmentApiCategory;

    @Value("${segment.api.dish}")
    String segmentApiDish;
    List<Category> dishes;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logs/test/eatery.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
        dishes = TestDataLoader.loadJsonListFromResource(
                "dishes.json",
                new TypeReference<>() {
                });
    }


    @Test
    void shouldCreateCategoryWithImageAndDishWithImage() {
        fileLog.println("\n========== 📤 Запрос: загрузка блюда с изображением ==========");

        dishes.forEach(category -> {
            int i = 1;
            String json1 = TestDataLoader.serializeToJsonString(
                    new CategoryDto(category.nameAz(), category.nameEn(), category.nameRu(), category.image()));

            Response response = given()
                    .baseUri(baseUrl)
                    .multiPart("data", "data.json", json1.getBytes(StandardCharsets.UTF_8), "application/json")
                    .multiPart("image", new File("src/test/resources/image/" + category.image()))
                    .when()
                    .post(segmentApiCategory + "/create/eatery/" + i++)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            String createdCategoryId = response.getBody().asString();

            List<Dish> d = category.dishes();
            d.forEach(dish -> {
                String json2 = TestDataLoader.serializeToJsonString(dish);

                given()
                        .baseUri(baseUrl)
                        .multiPart("data", "data.json", json2.getBytes(StandardCharsets.UTF_8), "application/json")
                        .multiPart("image", new File("src/test/resources/image/" + dish.image()))
                        .when()
                        .post(segmentApiDish + "/category-id/" + createdCategoryId)
                        .then()
                        .statusCode(200);

            });
        });
        fileLog.println("========== 📥 Ответ: блюдо успешно загружено ==========\n");
    }


    @Test
    void shouldCreateDishWithImage() {
        fileLog.println("\n========== 📤 Запрос: загрузка блюда с изображением ==========");
        int categoryId = 1;
        List<Dish> d = dishes.get(categoryId).dishes();

        d.forEach(dish -> {
            String json1 = TestDataLoader.serializeToJsonString(dish);

            given()
                    .baseUri(baseUrl)
                    .multiPart("data", "data.json", json1.getBytes(StandardCharsets.UTF_8), "application/json")
                    .multiPart("image", new File("src/test/resources/image/" + dish.image()))
                    .when()
                    .post(segmentApiCategory + "/category-id/1")
                    .then()
                    .statusCode(200);

        });
        fileLog.println("========== 📥 Ответ: блюдо успешно загружено ==========\n");
    }


    @Test
    void shouldGetDishById() {
        given()
                .baseUri(segmentApiCategory) // замени на свою базу
                .when()
                .get("/api/dish/1")
                .then()
                .statusCode(200)
                .log().body(); // выведет тело ответа
    }

    @Test
    void shouldGetAllDishesInCategory() {
        given()
                .baseUri(segmentApiCategory)
                .when()
                .get("/api/dish/category_id/1")
                .then()
                .statusCode(200)
                .log().body();
    }

    @Test
    void shouldDeleteMenuItemById() {
        given()
                .baseUri(segmentApiCategory)
                .when()
                .delete("/api/menu/menu_id/4")
                .then()
                .statusCode(200); // или 204 — зависит от того, как реализован контроллер
    }











}




