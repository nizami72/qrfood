package az.qrfood.backend.util;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ApiUtils {

    /**
     * Sends a POST request to a specified endpoint.
     *
     * @param baseUri The base URI of the API.
     * @param endpoint The endpoint path for the request.
     * @param requestBody The request payload (can be a POJO, Map, or String).
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
}