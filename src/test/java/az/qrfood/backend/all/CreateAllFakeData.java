package az.qrfood.backend.all;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.dto.Category;
import az.qrfood.backend.dto.CategoryDto;
import az.qrfood.backend.dto.Dish;
import az.qrfood.backend.dto.Eatery;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.util.TestDataLoader;
import az.qrfood.backend.util.TestUtil;
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
import java.util.Set;

@Log4j2
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateAllFakeData {

    private static PrintStream fileLog;
    @Value("${base.url}")
    String baseUrl;
    @Value("${full.admin.eatery}")
    String fullAdminEatery;
    @Value("${eatery.id.category.id.dish}")
    String urlPostDish;
    @Value("${eatery}")
    String segmentEateries;
    @Value("${category}")
    String componentCategories;
    List<Eatery> eateries;
    String jwtToken;
    Long userId;
    String userName;
    String password;

    @BeforeAll
    void setupLoggingAndCreateFakes() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/all.log", false));
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

        RegisterRequest authPayload = TestUtil.createRegisterRequest(Set.of(Role.EATERY_ADMIN));
        userName = authPayload.getUser().getEmail();
        password = authPayload.getUser().getPassword();

        Response authResponse = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(authPayload)
                .when()
                .post(fullAdminEatery)
                .then()
                .statusCode(201)
                .extract()
                .response();
        log.debug("Registered user [{}]", userName);

        // after registration and getting new eateryId, we need to log in and get a token
        login(
                userName,
                password,
                ((Integer) authResponse.as(Map.class).get("eateryId")).longValue());
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
                    "ownerProfileId", 2,
                    "numberOfTables", eatery.numberOfTables()
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

            Long createdEateryId = Long.valueOf(response1.getBody().asString());
            log.debug("Eatery created [{}]", createdEateryId);

            // refresh token to change the category of eatery
            login(
                    userName,
                    password,
                    createdEateryId);

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
                log.debug("Category created [{}]", createdCategoryId);

                List<Dish> d = category.dishes();
                // dishes creation
                d.forEach(dish -> {
                    String json2 = TestDataLoader.serializeToJsonString(dish);
                    String url = urlPostDish
                            .replace("{eateryId}", createdEateryId.toString())
                            .replace("{categoryId}", createdCategoryId);
                    Response response2 = given()
                            .baseUri(baseUrl)
                            .header("Authorization", "Bearer " + jwtToken)
                            .multiPart("data", "data.json", json2.getBytes(StandardCharsets.UTF_8), "application/json")
                            .multiPart("image", new File("src/test/resources/image/" + dish.image()))
                            .when()
                            .post(url)
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();
                    String createdDishId = response2.getBody().asString();
                    log.debug("Dish Created [{}]", createdDishId);
                });
            });
        });
        fileLog.println("========== 📥 The dish has been successfully loaded.==========\n");
    }

    private <T, R> T request(String url, R body, String jwt, Class<T> clazz) {
        Response response2 = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
                .body(body)
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response2.as(clazz);

    }

    //    @BeforeAll
    void login(String login, String pass, Long eateryId) {
        LoginRequest loginRequest = new LoginRequest(login, pass, eateryId);
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




