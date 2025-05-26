package az.qrfood.backend.eatery;

import az.qrfood.backend.dto.Eatery;
import az.qrfood.backend.util.TestDataLoader;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
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
    @Value("${segment.api.eatery}")
    String segmentApiEatery;
    List<Eatery> eateryList;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("eatery.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
//        eateryList = TestDataLoader.getTestEateriesFromFile();

    }

    /**
     * =============================== CREATE EATERY REQUEST =============================
     */
    @Test
    void createEatery() {

        fileLog.println("\n==================== 📥 CREATE EATERIES =====================");

        eateryList.forEach(eatery -> {

            Map<String, Object> requestBody = Map.of(
                    "name", eatery.name(),
                    "address", eatery.address(),
                    "phones", eatery.phones(),
                    "geoLat", eatery.geoLat(),
                    "geoLng", eatery.geoLng()
            );


            given()
//                .log().all() // лог всего ответа
                    .baseUri(baseUrl)
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post(segmentApiEatery)
                    .then()
                    .log().all() // лог всего ответа
                    .statusCode(200); // или другой ожидаемый статус
        });

    }

    @Test
    void getAllEateries() {
        fileLog.println("\n===== 🟢 GET ALL EATERIES =====");

        given()
                .log().all() // лог всего ответа
                .baseUri(baseUrl)
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
                .when()
                .delete(segmentApiEatery + "/{id}", eateryId)
                .then()
//                .body("success", "false")
                .log().all() // лог всего ответа
                .statusCode(404); // Или 204, если возвращается No Content
    }

}
