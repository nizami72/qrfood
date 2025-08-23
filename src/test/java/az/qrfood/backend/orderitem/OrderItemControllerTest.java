package az.qrfood.backend.orderitem;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.order.OrderItemStatus;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.auth.dto.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
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
import java.math.BigDecimal;

@SpringBootTest
@TestPropertySource(locations = "classpath:create-eatery-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class OrderItemControllerTest {

    //<editor-fold desc="Fields">
    // Hardcoded values to avoid property resolution issues
    String baseUrl = "http://localhost:8081";
    String eateryId = "1";
    Long dishId = 13L;
    Long dishIdUpdate = 14L;
    Long orderId = 20L; // Assuming order with ID 1 exists

    // the login end point
    String segmentApiAuth = "/api/auth/login";
    //? POST new and GET all order items into order /api/eatery/{eateryId}/order-item/order/{orderId}
    @Value("${order.item.order.id}")
    String orderItemOrderId;
    //? DELETE, PUT, GET by id /api/eatery/{eateryId}/order-item/{orderItemId}
    @Value("${order.item.id}")
    String orderItemId;
    // Authentication
    private String jwtToken;
    Long createdOrderItemId;
    //</editor-fold>

    @Test
    @Order(1)
    void shouldCreateOrderItem() {
        log.info("========== 📤 Request: Create order item ==========");

        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .dishId(dishId)
                .quantity(2)
                .note("Test note")
                .price(BigDecimal.valueOf(1.99))
                .orderId(orderId)
                .build();

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
        createdOrderItemId = createdOrderItem.getId();

        log.info("========== 📥 Response: Order item " + createdOrderItem + " created successfully ==========");
    }

    @Test
    @Order(2)
    void shouldGetOrderItemById() {
        log.info("========== 📤 Request: Get order item by ID ==========");
        // Get the order item by ID
        OrderItemDTO item = getOrderItem(200);
        assertEquals(createdOrderItemId, item.getId(), "Order item ID should match");
        log.info("========== 📥 Response: Order item [{}] retrieved successfully ==========", item);
    }

    @Test
    @Order(3)
    void shouldGetOrderItemsByOrderId() {
        log.info("========== 📤 Request: Get order items by order ID ==========");
        Response createResponse = given()
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

        assertTrue(
                createResponse.asString().contains(createdOrderItemId.toString()),
                "One of returned order items should contain created order item ID"
        );
        log.info("========== 📥 Response: Order items retrieved successfully [{}] ==========", createResponse.asString());
    }

    @Test
    @Order(4)
    void shouldUpdateOrderItem() {
        log.info("========== 📤 Request: Update order item ==========");

        // Update the order item
        OrderItemDTO updateOrderItemDTO = OrderItemDTO.builder()
                .dishId(dishIdUpdate)
                .quantity(3)
                .note("Updated")
                .build();
        putOrderItemId(updateOrderItemDTO);
        assertEquals(3, getOrderItem(200).getQuantity(), "Order item quantity should be updated to 3");
        log.info("========== 📥 Response: Order item updated successfully ==========");
    }

    @Test
    @Order(5)
    void shouldUpdateOrderItemStatus() {
        log.info("========== 📤 Request: Update order item status ==========");

        OrderItemDTO item = getOrderItem(200);
        assertEquals(OrderItemStatus.CREATED, getOrderItem(200).getStatus(), "Order item status should be updated to CREATED");

        // Update the order item status to PREPARING
        item.setStatus(OrderItemStatus.PREPARING);
        putOrderItemId(item);
        assertEquals(OrderItemStatus.PREPARING, getOrderItem(200).getStatus(), "Order item status should be updated to PREPARING");

        log.info("========== 📥 Response: Order item status updated to PREPARING successfully ==========");

        // Update the order item status to READY
        item.setStatus(OrderItemStatus.READY);
        putOrderItemId(item);
        assertEquals(OrderItemStatus.READY, getOrderItem(200).getStatus(), "Order item status should be updated to READY");
        log.info("========== 📥 Response: Order item status updated to READY successfully ==========");

        // Update the order item status to SERVED
        item.setStatus(OrderItemStatus.SERVED);
        putOrderItemId(item);
        assertEquals(OrderItemStatus.SERVED, getOrderItem(200).getStatus(), "Order item status should be updated to SERVED");
        log.info("========== 📥 Response: Order item status updated to SERVED successfully ==========");
    }

    @Test
    @Order(6)
    void shouldDeleteOrderItem() {
        log.info("========== 📤 Request: Delete order item ==========");

        OrderItemDTO item = getOrderItem(200);
        // Delete the order item
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(orderItemId
                        .replace("{orderItemId}", item.getId().toString())
                        .replace("{eateryId}", eateryId))
                .then()
                .statusCode(200);

        assertThrows(
                EntityNotFoundException.class, () -> this.getOrderItem(404),
                "Order item should not be found after deletion"
        );

        log.info("========== 📥 Response: Order item deleted successfully ==========");
    }


    @BeforeAll
    void setupLogging() throws Exception {
        PrintStream fileLog = new PrintStream(new FileOutputStream("logs/test/orderitem.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeAll
    void login() {
        log.info("========== 📤 Request: Authenticate ==========");

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

        log.info("========== 📥 Response: Authentication successful ==========");
    }

    private Response putOrderItemId(OrderItemDTO updateOrderItemDTO) {
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(updateOrderItemDTO)
                .when()
                .put(orderItemId
                        .replace("{orderItemId}", createdOrderItemId.toString())
                        .replace("{eateryId}", eateryId))
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    private OrderItemDTO getOrderItem(int expectedStatus) {
        if (createdOrderItemId == 0) throw new RuntimeException("Order item not created at test 1");
        Response createResponse = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(orderItemId
                        .replace("{orderItemId}", createdOrderItemId.toString())
                        .replace("{eateryId}", eateryId))
                .then()
                .statusCode(expectedStatus)
                .extract()
                .response();
        try {
            return createResponse.as(OrderItemDTO.class);
        } catch (Exception e) {
            throw new EntityNotFoundException("OrderItem not found");
        }
    }
}
