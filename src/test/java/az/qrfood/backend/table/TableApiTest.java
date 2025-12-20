package az.qrfood.backend.table;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest(properties = "spring.config.name=application-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TableApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;
    @Value("${table}")
    String tableEndpoint;
    @Value("${table.id}")
    String tableIdEndpoint;
    @Value("${auth.login}")
    String apiAuthLogin;

    String jwtToken;
    Long userId;
    Long eateryId = 1L; // Use an existing eatery ID
    Long tableId;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logs/test/table.log", false));
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
                .post(apiAuthLogin)
                .then()
                .statusCode(200)
                .extract()
                .response();

        jwtToken = authResponse.jsonPath().getString("jwt");
        userId = authResponse.jsonPath().getLong("userId");
    }

    /**
     * =============================== CREATE TABLE REQUEST =============================
     */
    @Test
    @Order(1)
    void createTable() {
        fileLog.println("\n==================== 📥 CREATE TABLE =====================");

        Map<String, Object> requestBody = Map.of(
                "number", "1",
                "eateryId", eateryId
        );

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(tableEndpoint.replace("{eateryId}", eateryId.toString()))
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response();

        tableId = response.jsonPath().getLong("id");
        fileLog.println("Created table with ID: " + tableId);
    }

    @Test
    @Order(2)
    void getAllTablesForEatery() {
        fileLog.println("\n===== 🟢 GET ALL TABLES FOR EATERY ID: " + eateryId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .get(tableEndpoint.replace("{eateryId}", eateryId.toString()))
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(3)
    void getTableById() {
        Long tableId = 1L; // Use an existing table ID
        fileLog.println("\n===== 🟢 GET TABLE BY ID: " + tableId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .get(tableIdEndpoint.replace("{eateryId}", eateryId.toString()).replace("{tableId}", tableId.toString()))
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(4)
    void updateTable() {
        Long tableId = 1L; // Use an existing table ID
        fileLog.println("\n===== 🔄 UPDATE TABLE ID: " + tableId + " =====");

        // First, get the current table data
        var currentTable = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .get(tableIdEndpoint.replace("{eateryId}", eateryId.toString()).replace("{tableId}", tableId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .as(Map.class);

        // Create updated data with modified fields
        Map<String, Object> requestBody = Map.of(
                "id", tableId,
                "number", "Updated-" + currentTable.get("number"),
                "eateryId", currentTable.get("eateryId"),
                "qrCodeDto", currentTable.get("qrCodeDto")
        );

        // Send update request
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(tableIdEndpoint.replace("{eateryId}", eateryId.toString()).replace("{tableId}", tableId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        // Verify the update was successful by getting the table again
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .get(tableIdEndpoint.replace("{eateryId}", eateryId.toString()).replace("{tableId}", tableId.toString()))
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(5)
    void deleteTable() {

        Long tableId = 5L; // Use an existing table ID
        fileLog.println("\n===== 🔴 DELETE TABLE ID: " + tableId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken) // ✅ Токен
                .when()
                .delete(tableIdEndpoint.replace("{eateryId}", eateryId.toString()).replace("{tableId}", tableId.toString()))
                .then()
                .log().all()
                .statusCode(204);
    }
}
