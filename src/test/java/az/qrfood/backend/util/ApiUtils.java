package az.qrfood.backend.util;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class ApiUtils {

    /**
     * Sends a POST request to a specified endpoint.
     *
     * @param baseUri            The base URI of the API.
     * @param endpoint           The endpoint path for the request.
     * @param requestBody        The request payload (can be a POJO, Map, or String).
     * @param expectedStatusCode The expected HTTP status code for validation.
     * @return The full Response object from the API call.
     */
    public static Response sendPostRequest(String baseUri, String endpoint, Object requestBody, int expectedStatusCode) {
        return given()
                .baseUri(baseUri)
                .contentType("application/json") // Assuming JSON content type
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response sendGetRequest(String baseUri, String jwtToken, String endpoint, int expectedStatusCode) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response sendGetRequest(String baseUri, String jwtToken, String endpoint) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response sendDeleteRequest(String baseUri, String jwtToken, String endpoint, int expectedStatusCode) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(endpoint)
                .then()
                .log().all()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Pair<Long, Long> registerUserAndEatery(String email, String password, String eateryName, String baseUrl, String endpoint) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .user(RegisterRequest.UserDto.builder()
                        .email(email)
                        .password(password)
                        .build())
                .restaurant(RegisterRequest.RestaurantDto.builder()
                        .name(eateryName)
                        .build())
                .build();
        // POST to create admin and eatery
        Response registerResponse = sendPostRequest(baseUrl, endpoint, registerRequest, 201);
        RegisterResponse rr = registerResponse.as(RegisterResponse.class);
        log.debug("Created user and eatery [{}]", rr);
        assertNotNull(rr);
        assertNotNull(rr.userId());
        assertNotNull(rr.eateryId());
        assertTrue(rr.success());
        return Pair.of(rr.userId(), rr.eateryId());
    }

    public static String login(String email, String password, Long eateryId, String baseUrl, String loginUrl) {
        log.debug("Login as [{}]", email);
        LoginRequest loginRequest = new LoginRequest(email, password, eateryId);
        Response authResponse = ApiUtils.sendPostRequest(baseUrl, loginUrl, loginRequest, 200);
        return authResponse.jsonPath().getString("jwt");
    }

}