package az.qrfood.backend.eatery;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dto.Eatery;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.FakeData;
import az.qrfood.backend.util.TestDataLoader;
import az.qrfood.backend.util.TestUtil;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EateryApiTest {

    private static PrintStream fileLog;

    @LocalServerPort
    private int port;

    @Value("${test.data.json-source}")
    private String jsonSourceFile;

    @Value("${auth.login}")
    String loginUrl;

    @Value("${admin.api.eatery}")
    String adminApiEateryUrl;

    String baseUrl;
    @Value("${eatery}")
    String segmentApiEatery;
    @Value("${eatery.owner}")
    String uriEateryOwner;
    @Value("${auth.refresh}")
    String uriRecreateTokenOnEateryChange;

    List<Eatery> eateryList;
    String jwtToken;
    Long userId;
    long eateryId; // Use an existing eatery ID


    private Testov testov;
    StaffItem admin;

    String name = "nizami.budagov@gmail.com";
    String pass = "qqqq1111";

    @BeforeAll
    void setupLogging() throws Exception {

        baseUrl = "http://localhost:" + port;
        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(jsonSourceFile), Testov.class);
        admin = testov.getStaff().stream().filter(s -> s.getRoles().contains("EATERY_ADMIN")).findFirst()
                .orElseThrow();
        registerUserAndEatery();
        login();

    }

    /**
     * =============================== CREATE EATERY REQUEST =============================
     */
    @Test
    @Order(1)
    void createEatery() {
        log.debug("\n==================== 📥 CREATE EATERIES =====================");

            Map<String, Object> requestBody = Map.of(
                    "name", FakeData.eateryName(),
                    "address", FakeData.generateFakeAddress(),
                    "phones", FakeData.phones(),
                    "geoLat", FakeData.geo1(),
                    "geoLng", FakeData.geo2(),
                    "numberOfTables", FakeData.numberOfTables(),

                    "ownerProfileId", 2
            );

            Response response = given()
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

        eateryId = Long.parseLong(response.getBody().asString());
        log.debug("Created eatery [{}]", eateryId);

        Response response1 = given()
//                .log().all() // лог всего ответа
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(new LoginRequest(name, pass, eateryId))
                .when()
                .post(uriRecreateTokenOnEateryChange)
                .then()
                .log().all() // лог всего ответа
                .statusCode(200)
                .extract()
                .response();

        jwtToken = "13";


    }

    @Test
    @Order(2)
    void getAllEateries() {
        log.debug("\n===== 🟢 GET ALL EATERIES =====");

        given()
                .log().all() // лог всего ответа
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(segmentApiEatery)
                .then()
                .log().all() // лог всего ответа
                .statusCode(200);
    }

    @Test
    @Order(3)
    void getEateryById() {
        log.debug("\n===== 🟢 GET EATERY BY ID: " + eateryId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(segmentApiEatery + "/{id}", eateryId)
                .then()
                .log().all() // лог всего ответа
                .statusCode(200);
    }

    @Test
    @Order(4)
    void updateEatery() {
        log.debug("\n===== 🔄 UPDATE EATERY ID: " + eateryId + " =====");

        // First, get the current eatery data
        var currentEatery = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(segmentApiEatery + "/" + eateryId)
                .then()
                .statusCode(200)
                .extract()
                .as(Map.class);

        // Create updated data with modified fields
        Map<String, Object> requestBody = Map.of(
                "name", "Updated " + currentEatery.get("name"),
                "address", "Updated " + currentEatery.get("address"),
                "phones", currentEatery.get("phones"),
                "numberOfTables", currentEatery.get("numberOfTables"),
                "geoLat", currentEatery.get("geoLat"),
                "geoLng", currentEatery.get("geoLng")
        );

        // Send update request
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(segmentApiEatery + "/{id}", eateryId)
                .then()
                .log().all() // Log the entire response
                .statusCode(200); // Expect 200 OK status

        // Verify the update was successful by getting the eatery again
        given()
                .baseUri(baseUrl)
                .when()
                .get(segmentApiEatery + "/{id}", eateryId)
                .then()
                .log().all() // Log the entire response
                .statusCode(200)
                .extract()
                .as(Map.class);
    }


    @Test
    @Order(5)
    void deleteEateryById() {
        long eateryId = 5;
        log.debug("\n===== 🔴 DELETE EATERY ID: " + eateryId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/api/eatery/{id}", eateryId)
                .then()
                .log().all() // лог всего ответа
                .statusCode(200); // Или 204, если возвращается No Content
    }

    @Test
    @Order(6)
    void deleteEateryByIdError404() {
        long eateryId = 50000;
        log.debug("\n===== 🔴 DELETE EATERY ID: " + eateryId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(segmentApiEatery + "/{id}", eateryId)
                .then()
//                .body("success", "false")
                .log().all() // лог всего ответа
                .statusCode(404); // Или 204, если возвращается No Content
    }

    @Test
    @Order(7)
    void getEateriesByOwnerId() {
        // Use the userId extracted from the login response
        log.debug("\n===== 🟢 GET EATERIES BY OWNER ID: " + userId + " =====");

        String json1 = TestDataLoader.serializeToJsonString(
                DishDto.builder()
                        .nameAz("Test Dish")
                        .nameEn("Updated Dish Name En")
                        .nameRu("Updated Dish Name Ru")
                        .price(BigDecimal.valueOf(15.0))
                        .descriptionAz("Updated Dish Description Az")
                        .descriptionEn("Updated Dish Description En")
                        .descriptionRu("Updated Dish Description Ru")
                        .isAvailable(true)
                        .build());

        given()
                .log().all() // лог всего ответа
                .baseUri(baseUrl)
                .multiPart("data", "data.json", json1.getBytes(StandardCharsets.UTF_8), "application/json")
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .get(uriEateryOwner, userId)
                .then()
                .log().all() // Log the entire response
                .statusCode(200); // Expect 200 OK status
    }

    private void registerUserAndEatery() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .user(RegisterRequest.UserDto.builder()
                        .email(admin.getEmail())
                        .password(admin.getPassword())
                        .build())
                .restaurant(RegisterRequest.RestaurantDto.builder()
                        .name(testov.getEatery().getName())
                        .build())
                .build();
        // POST to create admin and eatery
        Response registerResponse = ApiUtils.sendPostRequest(baseUrl, adminApiEateryUrl, registerRequest, 201);
        RegisterResponse rr = registerResponse.as(RegisterResponse.class);
        assertNotNull(rr);
        assertNotNull(rr.userId());
        assertNotNull(rr.eateryId());
        assertTrue(rr.success());
        this.userId = rr.userId();
        this.eateryId = rr.eateryId();
        log.debug("Registered eatery [{}]", eateryId);
    }

    private void login() {
        log.debug("Login as [{}]", admin.getEmail());
        LoginRequest loginRequest = new LoginRequest(admin.getEmail(), admin.getPassword(), eateryId);
        Response authResponse = ApiUtils.sendPostRequest(baseUrl, loginUrl, loginRequest, 200);
        jwtToken = authResponse.jsonPath().getString("jwt");
    }

}
