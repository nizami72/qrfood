package az.qrfood.backend.useraccess;

import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.useraccess.dto.UserAccessRequest;
import az.qrfood.backend.useraccess.dto.UserAccessResponse;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the UserAccess API.
 * This test follows a CRUD scenario for the UserAccess entity:
 * 1. Create a user access
 * 2. Get the user access by ID
 * 3. Get all user accesses
 * 4. Update the user access
 * 5. Verify the update
 * 6. Delete the user access
 * 7. Verify the deletion
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAccessApiTest {

    private static PrintStream fileLog;
    private Long createdUserAccessId;

    @Value("${base.url}")
    String baseUrl;

    String jwtToken;
    Long userId;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/user-access.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeEach
    void login() {
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
     * Test creating a user access.
     * Expected: 201 Created response with the new user access ID.
     */
    @Test
    @Order(1)
    void testCreateUserAccess() {
        UserAccessRequest request = new UserAccessRequest();
        request.setUserId(userId); // Using the logged-in user
        request.setEateryId(1L);   // Assuming eatery ID 1 exists
        request.setRole(Role.WAITER);

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/user-access")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId.intValue()))
                .body("eateryId", equalTo(1))
                .body("role", equalTo(Role.WAITER.toString()))
                .extract()
                .response();

        // Store the created ID for subsequent tests
        createdUserAccessId = response.jsonPath().getLong("id");
        System.out.println("[DEBUG_LOG] Created UserAccess ID: " + createdUserAccessId);
    }

    /**
     * Test getting a user access by ID.
     * Expected: 200 OK response with the user access data.
     */
    @Test
    @Order(2)
    void testGetUserAccessById() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/user-access/" + createdUserAccessId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserAccessId.intValue()))
                .body("userId", equalTo(userId.intValue()))
                .body("eateryId", equalTo(1))
                .body("role", equalTo(Role.WAITER.toString()));
    }

    /**
     * Test getting all user accesses.
     * Expected: 200 OK response with a list containing the created user access.
     */
    @Test
    @Order(3)
    void testGetAllUserAccesses() {
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/user-access")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<UserAccessResponse> userAccesses = response.jsonPath().getList("", UserAccessResponse.class);

        // Verify the list contains the created user access
        boolean found = userAccesses.stream()
                .anyMatch(ua -> ua.getId().equals(createdUserAccessId));

        assertTrue(found, "Created user access not found in the list");
        System.out.println("[DEBUG_LOG] Found UserAccess in list: " + found);
    }

    /**
     * Test updating a user access.
     * Expected: 200 OK response with the updated data.
     */
    @Test
    @Order(4)
    void testUpdateUserAccess() {
        UserAccessRequest request = new UserAccessRequest();
        request.setUserId(userId);
        request.setEateryId(1L);
        request.setRole(Role.RESTAURANT_ADMIN); // Change role from WAITER to RESTAURANT_ADMIN

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/user-access/" + createdUserAccessId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserAccessId.intValue()))
                .body("userId", equalTo(userId.intValue()))
                .body("eateryId", equalTo(1))
                .body("role", equalTo(Role.RESTAURANT_ADMIN.toString()));
    }

    /**
     * Test verifying the update.
     * Expected: 200 OK response with the updated fields.
     */
    @Test
    @Order(5)
    void testVerifyUpdate() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/user-access/" + createdUserAccessId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserAccessId.intValue()))
                .body("userId", equalTo(userId.intValue()))
                .body("eateryId", equalTo(1))
                .body("role", equalTo(Role.RESTAURANT_ADMIN.toString())); // Verify role is now RESTAURANT_ADMIN
    }

    /**
     * Test deleting a user access.
     * Expected: 204 No Content response.
     */
    @Test
    @Order(6)
    void testDeleteUserAccess() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/api/user-access/" + createdUserAccessId)
                .then()
                .statusCode(204);
    }

    /**
     * Test verifying the deletion.
     * Expected: 404 Not Found response.
     */
    @Test
    @Order(7)
    void testVerifyDeletion() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/user-access/" + createdUserAccessId)
                .then()
                .statusCode(404);
    }
}
