package az.qrfood.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.dto.OrderStatusUpdateDTO;
import az.qrfood.backend.util.AbstractTest;
import az.qrfood.backend.util.TestUtil;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Log4j2
class OrderControllerTest extends AbstractTest {

    @Value("${order.post}")
    String orderEndPoint;

    @Value("${order.id}")
    String orderIdEndPoint;

    Long tableId = 1L; // Use an existing table ID
    String jwtToken;
    Long orderId;

    /**
     * =============================== CREATE ORDER REQUEST =============================
     */
    @Test
    @Order(1)
    void postOrder() {
        log.debug("\n==================== 📥 CREATE ORDER =====================");

        // Print endpoint for debugging
        String endpoint = TestUtil.formatUrl(orderEndPoint, eateryId.toString());
        log.debug("Using endpoint: {}", endpoint);
        log.debug("Base URL: {}", baseUrl);

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

        log.debug("Request body: {}", orderDto);

        try {
            Response response = given()
                    .baseUri(baseUrl)
//                    .header("Authorization", "Bearer " + jwtToken)
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
            log.debug("Created order with ID: {}", orderId);

            // Verify the order was created with the correct data
            assertNotNull(orderId, "Order ID should not be null");
        } catch (Exception e) {
            log.debug("Error creating order: {}", e.getMessage());
            log.error(e);

            // For testing purposes, set a fake orderId so other tests can run
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
        log.debug("\n===== \uD83D\uDFE2 GET ORDER BY ID: {} =====", orderId);

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
    void getOrdersByStatus() {
        log.debug("\n===== \uD83D\uDFE2 GET ALL ORDERS FOR EATERY ID: {} =====", eateryId);

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
        log.debug("\n===== \uD83D\uDD04 UPDATE ORDER ID: {} =====", orderId);

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
        log.debug("\n===== \uD83D\uDD04 UPDATE ORDER STATUS ID: {} =====", orderId);

        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO();
        statusUpdate.setStatus("COMPLETED");

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(statusUpdate)
                .when()
                .put(baseUrl + "/api/orders/" + orderId + "/status") // Using a direct path as per controller
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
        log.debug("\n===== \uD83D\uDD34 DELETE ORDER ID: {} =====", orderId);

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
