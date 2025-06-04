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
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateAllFakeData {

    private static PrintStream fileLog;
    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.eateries}")
    String segmentEateries;
    @Value("${segment.categories}")
    String segmentCategories;
    @Value("${segment.dishes}")
    String segmentDishes;
    @Value("${component.categories}")
    String componentCategories;
    List<Eatery> eateries;

    String jwtToken;
    Long userId;

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

    @BeforeAll
    void registerUserAndEatery() {
        // Fetch token
        String authPayload = """
                            {
                    "user": {
                    "name": "Nizami Budagov",
                            "email": "nizami.budagov@gmail.com",
                            "password": "qqqq1111"
                },
                    "restaurant": {
                    "name": "My First Restaurant"
                }
                }
                """;

        Response authResponse = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(authPayload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .extract()
                .response();
        log.debug("Registered user");
        login();
    }

    //    @BeforeAll
    void login() {
        // Fetch token
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


    @Test
    void shouldCreateCategoryWithImageAndDishWithImage() {
        fileLog.println("\n========== 📤 All data going to be loaded ==========");

        eateries.forEach(eatery -> {

            Map<String, Object> requestBody = Map.of(
                    "name", eatery.name(),
                    "address", eatery.address(),
                    "phones", eatery.phones(),
                    "geoLat", eatery.geoLat(),
                    "geoLng", eatery.geoLng(),
                    "categories", eatery.categories(),
                    "ownerProfileId", 1
            );
            Response response1 = given()
//                .log().all()
                    .baseUri(baseUrl)
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post(segmentEateries)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract()
                    .response();


            String createdEateryId = response1.getBody().asString();
            log.debug("Eatery created [{}]", createdEateryId);

            List<Category> categoryList = eatery.categories();

            categoryList.forEach(category -> {

                String json1 = TestDataLoader.serializeToJsonString(
                        new CategoryDto(category.nameAz(), category.nameEn(), category.nameRu(), category.image()));

                Response response = given()
                        .baseUri(baseUrl)
                        .header("Authorization", "Bearer " + jwtToken)
                        .multiPart("data", "data.json", json1.getBytes(StandardCharsets.UTF_8), "application/json")
                        .multiPart("image", new File("src/test/resources/image/" + category.image()))
                        .when()
                        .post(segmentEateries + "/" + createdEateryId + componentCategories)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
                String createdCategoryId = response.getBody().asString();
                log.debug("Created category [{}]", createdCategoryId);

                List<Dish> d = category.dishes();
                d.forEach(dish -> {
                    String json2 = TestDataLoader.serializeToJsonString(dish);

                    Response r = given()
                            .baseUri(baseUrl)
                            .header("Authorization", "Bearer " + jwtToken)
                            .multiPart("data", "data.json", json2.getBytes(StandardCharsets.UTF_8), "application/json")
                            .multiPart("image", new File("src/test/resources/image/" + dish.image()))
                            .when()
                            .post("/api/categories" + "/" + createdCategoryId + "/dishes")
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();
                    String createdDishId = r.getBody().asString();
                    log.debug("Created dish [{}]", createdDishId);
                });
            });
        });


        fileLog.println("========== 📥 The dish has been successfully loaded.==========\n");
    }


}




