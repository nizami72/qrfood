package az.qrfood.backend.orderitem;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.user.dto.LoginRequest;
import az.qrfood.backend.user.dto.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.FileOutputStream;
import java.io.PrintStream;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderItemApiTest {

    private static PrintStream fileLog;

    // Hardcoded values to avoid property resolution issues
    String baseUrl = "http://localhost:8080";
    String segmentApiOrderItems = "/api/order-items";
    String segmentApiAuth = "/api/auth";

    // Authentication
    private String jwtToken;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("testLogs/orderitem.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeEach
    void authenticate() {
        fileLog.println("\n========== 📤 Request: Authenticate ==========");

        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nizami.budagov@gmail.com");
        loginRequest.setPassword("qqqq1111");
        loginRequest.setEateryId(1L);

        // Authenticate and get JWT token
        Response response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post(segmentApiAuth + "/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        LoginResponse loginResponse = response.as(LoginResponse.class);
        jwtToken = loginResponse.getJwt();

        fileLog.println("========== 📥 Response: Authentication successful ==========\n");
    }

    @Test
    void shouldGetOrderItemById() {
        fileLog.println("\n========== 📤 Request: Get order item by ID ==========");

        // First create an order item to test with
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(1L)
                .orderItemId(7L) // Assuming order with ID 1 exists
                .quantity(2)
                .note("Test note")
                .build();

        // Create the order item
        Response createResponse = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(orderItemDTO)
                .when()
                .post(segmentApiOrderItems)
                .then()
                .statusCode(200)
                .extract()
                .response();

        OrderItemDTO createdOrderItem = createResponse.as(OrderItemDTO.class);
        Long orderItemId = createdOrderItem.getId();

        // Get the order item by ID
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(segmentApiOrderItems + "/" + orderItemId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order item retrieved successfully ==========\n");
    }

    @Test
    void shouldGetOrderItemsByOrderId() {
        fileLog.println("\n========== 📤 Request: Get order items by order ID ==========");

        Long orderId = 7L; // Assuming order with ID 1 exists

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(segmentApiOrderItems + "/order/" + orderId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order items retrieved successfully ==========\n");
    }

    @Test
    void shouldCreateOrderItem() {
        fileLog.println("\n========== 📤 Request: Create order item ==========");

        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(1L)
                .quantity(2)
                .note("Test note")
                .orderItemId(1l)
                .build();

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(orderItemDTO)
                .when()
                .post(segmentApiOrderItems)
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order item created successfully ==========\n");
    }

    @Test
    void shouldUpdateOrderItem() {
        fileLog.println("\n========== 📤 Request: Update order item ==========");

        // First create an order item to update
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(1L)
                .quantity(2)
                .note("Test note")
                .orderItemId(1l)
                .build();

        // Create the order item
        Response createResponse = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(orderItemDTO)
                .when()
                .post(segmentApiOrderItems)
                .then()
                .statusCode(200)
                .extract()
                .response();

        OrderItemDTO createdOrderItem = createResponse.as(OrderItemDTO.class);
        Long orderItemId = createdOrderItem.getId();

        // Update the order item
        OrderItemDTO updateOrderItemDTO = OrderItemDTO.builder()
                .quantity(3)
                .note("Updated note")
                .build();

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(updateOrderItemDTO)
                .when()
                .put(segmentApiOrderItems + "/" + orderItemId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order item updated successfully ==========\n");
    }

    @Test
    void shouldDeleteOrderItem() {
        fileLog.println("\n========== 📤 Request: Delete order item ==========");

        // First create an order item to delete
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(5L)
                .orderItemId(1L) // Assuming order with ID 1 exists
                .quantity(2)
                .note("Test note")
                .build();

        // Create the order item
        Response createResponse = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(orderItemDTO)
                .when()
                .post(segmentApiOrderItems)
                .then()
                .statusCode(200)
                .extract()
                .response();

        OrderItemDTO createdOrderItem = createResponse.as(OrderItemDTO.class);
        Long orderItemId = createdOrderItem.getId();

        // Delete the order item
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(segmentApiOrderItems + "/" + orderItemId)
                .then()
                .statusCode(200);

        fileLog.println("========== 📥 Response: Order item deleted successfully ==========\n");
    }
}
