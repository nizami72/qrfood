package az.qrfood.backend.orderitem;

import static io.restassured.RestAssured.given;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.auth.dto.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
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

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderItemControllerTest {

    private static PrintStream fileLog;

    //<editor-fold desc="Fields">
    // Hardcoded values to avoid property resolution issues
    String baseUrl = "http://localhost:8081";
    String eateryId = "2";
    // the login end point
    String segmentApiAuth = "/api/auth/login";
    //? POST new and GET all order items into order /api/eatery/{eateryId}/order-item/order/{orderId}
    @Value("${order.item.order.id}")
    String orderItemOrderId;
    //? DELETE, PUT, GET by id /api/eatery/{eateryId}/order-item/{orderItemId}
    @Value("${order.item.id}")
    String orderItemId;
    //?  GET all order items
    @Value("${order.item}")
    String orderItem;
    Long orderId = 7L; // Assuming order with ID 1 exists
    // Authentication
    private String jwtToken;
    //</editor-fold>

    @Test
    @Order(2)
    void shouldGetOrderItemById() {
        fileLog.println("\n========== 📤 Request: Get order item by ID ==========");

        // First create an order item to test with
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(3L)
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
                .post(orderItemOrderId
                        .replace("{eateryId}", eateryId)
                        .replace("{orderId}", orderId.toString())
                )
                .then()
                .statusCode(200)
                .extract()
                .response();

        OrderItemDTO createdOrderItem = createResponse.as(OrderItemDTO.class);

        // Get the order item by ID
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderItemId
                        .replace("{orderItemId}", createdOrderItem.getOrderItemId().toString())
                        .replace("{eateryId}", eateryId))
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order item retrieved successfully ==========\n");
    }

    @Test
    @Order(3)
    void shouldGetOrderItemsByOrderId() {
        fileLog.println("\n========== 📤 Request: Get order items by order ID ==========");


        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderItemOrderId
                        .replace("{eateryId}", eateryId)
                        .replace("{orderId}", orderId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order items retrieved successfully ==========\n");
    }

    @Test
    @Order(1)
    void shouldCreateOrderItem() {
        fileLog.println("\n========== 📤 Request: Create order item ==========");

        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(2L)
                .quantity(2)
                .note("Test note")
                .orderItemId(orderId)
                .build();

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(orderItemDTO)
                .when()
                .post(orderItemOrderId
                        .replace("{eateryId}", eateryId)
                        .replace("{orderId}", orderId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order item created successfully ==========\n");
    }

    @Test
    @Order(4)
    void shouldUpdateOrderItem() {
        fileLog.println("\n========== 📤 Request: Update order item ==========");

        // First create an order item to update
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(2L)
                .quantity(2)
                .note("Test note")
                .orderItemId(2l)
                .build();

        // Create the order item
        Response createResponse = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(orderItemDTO)
                .when()
                .post(orderItemOrderId
                        .replace("{eateryId}", eateryId)
                        .replace("{orderId}", orderId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();

        OrderItemDTO createdOrderItem = createResponse.as(OrderItemDTO.class);
        Long orderItemId12 = createdOrderItem.getId();

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
                .put(orderItemId
                        .replace("{orderItemId}", orderItemId12.toString())
                        .replace("{eateryId}", eateryId))
                .then()
                .statusCode(200)
                .extract()
                .response();

        fileLog.println("========== 📥 Response: Order item updated successfully ==========\n");
    }

    @Test
    @Order(5)
    void shouldDeleteOrderItem() {
        fileLog.println("\n========== 📤 Request: Delete order item ==========");

        // First create an order item to delete
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(2L)
                .orderItemId(2L) // Assuming order with ID 1 exists
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
                 .post(orderItemOrderId
                        .replace("{eateryId}", eateryId)
                        .replace("{orderId}", orderId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();

        OrderItemDTO createdOrderItem = createResponse.as(OrderItemDTO.class);

        // Delete the order item
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(orderItemId
                        .replace("{orderItemId}", createdOrderItem.getOrderItemId().toString())
                        .replace("{eateryId}", eateryId))
                .then()
                .statusCode(200);

        fileLog.println("========== 📥 Response: Order item deleted successfully ==========\n");
    }

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/orderitem.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeAll
    void login() {
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
                .post(segmentApiAuth)
                .then()
                .statusCode(200)
                .extract()
                .response();

        LoginResponse loginResponse = response.as(LoginResponse.class);
        jwtToken = loginResponse.getJwt();

        fileLog.println("========== 📥 Response: Authentication successful ==========\n");
    }
}
