package az.qrfood.backend.eatery;

import az.qrfood.backend.dto.Eatery;
import az.qrfood.backend.util.FakeData;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EateryApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.api.eateries}")
    String segmentApiEatery;
    List<Eatery> eateryList;
    String jwtToken;
    Long userId;

    @BeforeEach
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/eatery.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );

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

    /**
     * =============================== CREATE EATERY REQUEST =============================
     */
    @Test
    void createEatery() {

        fileLog.println("\n==================== 📥 CREATE EATERIES =====================");


            Map<String, Object> requestBody = Map.of(
                    "name", FakeData.eateryName(),
                    "address", FakeData.generateFakeAddress(),
                    "phones", FakeData.phones(),
                    "geoLat", FakeData.geo1(),
                    "geoLng", FakeData.geo2(),
                    "numberOfTables", FakeData.numberOfTables(),

                    "ownerProfileId", 2
            );


            given()
//                .log().all() // лог всего ответа
                    .baseUri(baseUrl)
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post(segmentApiEatery)
                    .then()
                    .log().all() // лог всего ответа
                    .statusCode(200); // или другой ожидаемый статус

    }

    @Test
    void getAllEateries() {
        fileLog.println("\n===== 🟢 GET ALL EATERIES =====");

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
    void getEateryById() {
        long eateryId = 5;
        fileLog.println("\n===== 🟢 GET EATERY BY ID: " + eateryId + " =====");

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
    void deleteEateryById() {
        long eateryId = 5;
        fileLog.println("\n===== 🔴 DELETE EATERY ID: " + eateryId + " =====");

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
    void deleteEateryByIdError404() {
        long eateryId = 50000;
        fileLog.println("\n===== 🔴 DELETE EATERY ID: " + eateryId + " =====");

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
    void updateEatery() {
        long eateryId = 1; // Use an existing eatery ID
        fileLog.println("\n===== 🔄 UPDATE EATERY ID: " + eateryId + " =====");

        // First, get the current eatery data
        var currentEatery = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(segmentApiEatery + "/{id}", eateryId)
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
    void getEateriesByOwnerId() {
        // Use the userId extracted from the login response
        fileLog.println("\n===== 🟢 GET EATERIES BY OWNER ID: " + userId + " =====");

        given()
                .log().all() // лог всего ответа
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .get(segmentApiEatery + "/owner/{ownerId}", userId)
                .then()
                .log().all() // Log the entire response
                .statusCode(200); // Expect 200 OK status
    }

}
