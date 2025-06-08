package az.qrfood.backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
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

import static io.restassured.RestAssured.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerTest {

    private static PrintStream fileLog;
    @Value("${base.url}")
    String baseUrl;
    @Value("${segment.menu.order}")
    String segmentOrderMenu;
    String jwtToken;
    Long userId;


    @BeforeEach
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/order.log", false));
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

    @Test
    void getAllOrders() {
    }

    @Test
    void getOrdersByEateryId() {
    }

    @Test
    void getOrderById() {
    }

    @Test
    void createOrder() {
        Long tableId = 1L;

        OrderDto o = OrderDto.builder()
                .tableId(1L)
                .status("ACTIVE")
                .tableNumber("Table - 1")
                .note("Prepare by 25 minutes please!")
                .items(List.of(
                        OrderItemDTO.builder().dishId(1L).quantity(2).note("Some note 1").build(),
                        OrderItemDTO.builder().dishId(2L).quantity(2).note("Some note 2").build(),
                        OrderItemDTO.builder().dishId(3L).quantity(2).note("Some note 3").build(),
                        OrderItemDTO.builder().dishId(4L).quantity(2).note("Some note 4").build()
                ))
                .build();
        given()
                .log().all() // лог всего ответа
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(o)
                .when()
                .post(String.format(segmentOrderMenu, tableId))
                .then()
                .log().all() // лог всего ответа
                .statusCode(200); // или другой ожидаемый статус

    }

    @Test
    void updateOrder() {
    }

    @Test
    void updateOrderStatus() {
    }

    @Test
    void deleteOrder() {
    }
}
