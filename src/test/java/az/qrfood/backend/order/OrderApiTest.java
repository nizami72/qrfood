package az.qrfood.backend.order;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
    "PROTOCOL=http://",
    "DOMAIN_NAME=localhost",
    "DOMAIN_PORT=:8080"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;

    @Value("${segment.api.orders}")
    String apiOrders;

    String jwtToken;
    Long userId;
    Long eateryId = 1L; // Use an existing eatery ID
    Long tableId = 2L; // Use an existing table ID
    Long orderId; // Will be set after creating an order

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("order.log", false));
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
     * =============================== CREATE ORDER REQUEST =============================
     * POST new order
     */
    @Test
    @Order(1)
    void createOrder() {
        fileLog.println("\n==================== 📥 CREATE ORDER =====================");

        // Create order items
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("dishItemId", 1L);
        item1.put("name", "Dish 1");
        item1.put("quantity", 2);
        item1.put("note", "No onions please");
        items.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("dishItemId", 3L);
        item2.put("name", "Dish 2");
        item2.put("quantity", 1);
        item2.put("note", "Extra spicy");
        items.add(item2);

        // Create order request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tableNumber", "1");
        requestBody.put("note", "Please, start in 10 minute");
        requestBody.put("items", items);

        // Send create order request
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(apiOrders + "/{tableId}", tableId)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("tableId", equalTo(tableId.intValue()))
                .body("note", equalTo("Please, start in 10 minute"))
                .extract()
                .response();

        // Store the order ID for later tests
        orderId = response.jsonPath().getLong("id");
        fileLog.println("Created order with ID: " + orderId);

        assertNotNull(orderId, "Order ID should not be null");
    }

    /**
     * =============================== GET ALL ORDERS =============================
     * GET all ensure the one created there
     */
    @Test
    @Order(2)
    void getAllOrders() {
        fileLog.println("\n===== 🟢 GET ALL ORDERS =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(apiOrders)
                .then()
                .log().all()
                .statusCode(200)
                .body("$", hasSize(org.hamcrest.Matchers.greaterThan(0)))
                .body("id", hasItem(orderId.intValue()));
    }

    /**
     * =============================== GET ORDER BY ID =============================
     * Get new order by id and check
     */
    @Test
    @Order(3)
    void getOrderById() {
        fileLog.println("\n===== 🟢 GET ORDER BY ID: " + orderId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(apiOrders + "/{id}", orderId)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(orderId.intValue()))
                .body("tableId", equalTo(tableId.intValue()))
                .body("note", equalTo("Please, start in 10 minute"))
                .body("items", hasSize(2));
    }

    /**
     * =============================== GET ORDERS BY EATERY =============================
     * GET new order by eatery, check
     */
    @Test
    @Order(4)
    void getOrdersByEatery() {
        fileLog.println("\n===== 🟢 GET ORDERS BY EATERY ID: " + eateryId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(apiOrders + "/eatery/{eateryId}", eateryId)
                .then()
                .log().all()
                .statusCode(200)
                .body("$", hasSize(org.hamcrest.Matchers.greaterThan(0)))
                .body("id", hasItem(orderId.intValue()));
    }

    /**
     * =============================== UPDATE ORDER =============================
     * PUT a new order then get and ensure updated
     */
    @Test
    @Order(5)
    void updateOrder() {
        fileLog.println("\n===== 🔄 UPDATE ORDER ID: " + orderId + " =====");

        // Create updated order items
        List<Map<String, Object>> updatedItems = new ArrayList<>();
        updatedItems.add(Map.of(
                "dishItemId", 1L,
                "quantity", 3,
                "note", "No onions and extra sauce please"
        ));
        updatedItems.add(Map.of(
                "dishItemId", 3L,
                "quantity", 2,
                "note", "Extra spicy and hot"
        ));

        // Create update request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", orderId);
        requestBody.put("tableId", tableId);
        requestBody.put("note", "Updated order note");
        requestBody.put("items", updatedItems);

        // Send update request
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(apiOrders + "/{id}", orderId)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(orderId.intValue()))
                .body("note", equalTo("Updated order note"));

        // Verify the update was successful by getting the order again
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(apiOrders + "/{id}", orderId)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(orderId.intValue()))
                .body("note", equalTo("Updated order note"));
    }

    /**
     * =============================== DELETE ORDER =============================
     * DELETE new order and GET deleted by id to ensure the one is deleted
     */
    @Test
    @Order(6)
    void deleteOrder() {
        fileLog.println("\n===== 🔴 DELETE ORDER ID: " + orderId + " =====");

        // Delete the order
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(apiOrders + "/{id}", orderId)
                .then()
                .log().all()
                .statusCode(200);

        // Try to get the deleted order - should return an error
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(apiOrders + "/{id}", orderId)
                .then()
                .log().all()
                .statusCode(500); // Server returns 500 for non-existent orders
    }
}
