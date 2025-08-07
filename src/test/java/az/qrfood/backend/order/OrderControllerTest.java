package az.qrfood.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.dto.OrderStatusUpdateDTO;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest(properties = "spring.config.name=application-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {

    private static PrintStream fileLog;
    @Value("${base.url}")
    String baseUrl;
    String jwtToken;
    Long userId;

    @Value("${order}")
    String orderEndPoint;
    @Value("${order.id}")
    String orderIdEndPoint;
    @Value("${order.status}")
    String orderStatusEndPoint;

    Long eateryId = 1L; // Use an existing eatery ID
    Long tableId = 1L; // Use an existing table ID
    Long orderId;


    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logs/test/order.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );

        // Fetch token
        String authPayload = """
                {
                  "email": "nizami.budagov1@gmail.com",
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
     */
    @Test
    @Order(1)
    void postOrder() {
        fileLog.println("\n==================== 📥 CREATE ORDER =====================");

        // Print endpoint for debugging
        String endpoint = orderEndPoint.replace("{eateryId}", eateryId.toString());
        fileLog.println("Using endpoint: " + endpoint);
        fileLog.println("Base URL: " + baseUrl);

        // Simplified order with minimal required fields
        OrderDto orderDto = OrderDto.builder()
                .tableId(tableId)
                .status(OrderStatus.CREATED)
                .tableNumber("Table - 1")
                .note("Prepare by 25 minutes please!")
                .items(List.of(
                        OrderItemDTO.builder().dishId(2L).quantity(2).note("Some note 1").build()
                ))
                .build();

        fileLog.println("Request body: " + orderDto);

        try {
            Response response = given()
                    .baseUri(baseUrl)
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType("application/json")
                    .body(orderDto)
                    .when()
                    .post(endpoint)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract()
                    .response();

            orderId = response.jsonPath().getLong("id");
            fileLog.println("Created order with ID: " + orderId);

            // Verify the order was created with the correct data
            assertNotNull(orderId, "Order ID should not be null");
        } catch (Exception e) {
            fileLog.println("Error creating order: " + e.getMessage());
            e.printStackTrace(fileLog);

            // For testing purposes, set a dummy orderId so other tests can run
            orderId = 1L;
            throw e;
        }
    }

    /**
     * =============================== GET ORDER BY ID =============================
     */
    @Test
    @Order(2)
    void getOrderById() {
        fileLog.println("\n===== 🟢 GET ORDER BY ID: " + orderId + " =====");

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderIdEndPoint.replace("{eateryId}", eateryId.toString()).replace("{orderId}", orderId.toString()))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        // Verify the retrieved order has the correct ID and data
        assertEquals(orderId, response.jsonPath().getLong("id"), "Retrieved order ID should match the created order ID");
        assertEquals("CREATED", response.jsonPath().getString("status"), "Order status should be CREATED");
        assertEquals(tableId, response.jsonPath().getLong("tableId"), "Table ID should match");
    }

    /**
     * =============================== GET ALL ORDERS =============================
     */
    @Test
    @Order(3)
    void getAllEateryOrdersByStatus() {
        fileLog.println("\n===== 🟢 GET ALL ORDERS FOR EATERY ID: " + eateryId + " =====");

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderEndPoint.replace("{eateryId}", eateryId.toString()))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        // Verify the list contains our created order
        List<Map<String, Object>> orders = response.jsonPath().getList("$");
        boolean orderFound = orders.stream()
                .anyMatch(order -> order.get("id").toString().equals(orderId.toString()));

        assertTrue(orderFound, "The created order should be in the list of all orders");
    }

    /**
     * =============================== UPDATE ORDER =============================
     */
    @Test
    @Order(4)
    void updateOrder() {
        fileLog.println("\n===== 🔄 UPDATE ORDER ID: " + orderId + " =====");

        // First, get the current order data
        OrderDto currentOrder = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderIdEndPoint.replace("{eateryId}", eateryId.toString()).replace("{orderId}", orderId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .as(OrderDto.class);

        // Update the order note
        currentOrder.setNote("Updated note - please prepare quickly!");

        // Send update request
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(currentOrder)
                .when()
                .put(orderIdEndPoint
                        .replace("{eateryId}", eateryId.toString())
                        .replace("{orderId}", orderId.toString()))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        // Verify the update was successful
        assertEquals("Updated note - please prepare quickly!", response.jsonPath().getString("note"), 
                "Order note should be updated");
    }

    /**
     * =============================== UPDATE ORDER STATUS =============================
     */
    @Test
    @Disabled // note disabled as the corresponding method is not fully implemented
    @Order(5)
    void updateOrderStatus() {
        fileLog.println("\n===== 🔄 UPDATE ORDER STATUS ID: " + orderId + " =====");

        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO();
        statusUpdate.setStatus("COMPLETED");

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(statusUpdate)
                .when()
                .put(baseUrl + "/api/orders/" + orderId + "/status") // Using direct path as per controller
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        // Verify the status was updated
        assertEquals("COMPLETED", response.jsonPath().getString("status"), 
                "Order status should be updated to COMPLETED");
    }

    /**
     * =============================== DELETE ORDER =============================
     */
    @Test
    @Order(6)
    void deleteOrder() {
        fileLog.println("\n===== 🔴 DELETE ORDER ID: " + orderId + " =====");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(orderIdEndPoint.replace("{eateryId}", eateryId.toString()).replace("{orderId}", orderId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        // Verify the order was deleted by trying to get it (should return 404)
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderIdEndPoint.replace("{eateryId}", eateryId.toString()).replace("{orderId}", orderId.toString()))
                .then()
                .log().all()
                .statusCode(404);
    }
}
