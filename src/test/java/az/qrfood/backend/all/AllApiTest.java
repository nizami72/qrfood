package az.qrfood.backend.all;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.dto.Category;
import az.qrfood.backend.dto.CategoryDto;
import az.qrfood.backend.dto.Dish;
import az.qrfood.backend.dto.Eatery;
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
import java.util.Map;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AllApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.api.eatery}")
    String segmentApiEatery;
    @Value("${segment.api.category}")
    String segmentApiCategory;

    @Value("${segment.api.dish}")
    String segmentApiDish;
    List<Eatery> eateries;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logs/all.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
        eateries = TestDataLoader.loadJsonListFromResource(
                "all.json",
                new TypeReference<>() {
                });
    }


    @Test
    void shouldCreateCategoryWithImageAndDishWithImage() {
        fileLog.println("\n========== 📤 Запрос: загрузка блюда с изображением ==========");

        eateries.forEach(eatery -> {

            Map<String, Object> requestBody = Map.of(
                    "name", eatery.name(),
                    "address", eatery.address(),
                    "phones", eatery.phones(),
                    "geoLat", eatery.geoLat(),
                    "geoLng", eatery.geoLng(),
                    "categories", eatery.categories()
            );

            Response response1 = given()
//                .log().all() // лог всего ответа
                    .baseUri(baseUrl)
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post(segmentApiEatery)
                    .then()
                    .log().all() // лог всего ответа
                    .statusCode(200)
                    .extract()
                    .response();

            String createdEateryId = response1.getBody().asString();

            List<Category> categoryList = eatery.categories();

            categoryList.forEach(category -> {

                String json1 = TestDataLoader.serializeToJsonString(
                        new CategoryDto(category.nameAz(), category.nameEn(), category.nameRu(), category.image()));

                Response response = given()
                        .baseUri(baseUrl)
                        .multiPart("data", "data.json", json1.getBytes(StandardCharsets.UTF_8), "application/json")
                        .multiPart("image", new File("src/test/resources/image/" + category.image()))
                        .when()
                        .post(segmentApiCategory + "/create/eatery/" + createdEateryId)
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
        });


        fileLog.println("========== 📥 Ответ: блюдо успешно загружено ==========\n");
    }


}




