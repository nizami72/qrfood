package az.qrfood.backend.user;

import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.Role;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the User API.
 * This test covers the following operations for the User entity:
 * - Create a user
 * - Get a user by ID
 * - Get all users
 * - Update a user
 * - Delete a user
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiTest {

    private static PrintStream fileLog;

    @Value("${base.url}")
    String baseUrl;

    String jwtToken;
    String uniqueUsername;
    Long createdUserId;
    String role = Role.WAITER.name();

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/user-api.log", false));
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
    }

    /**
     * Test creating a user.
     * Expected: 201 Created response with the new user's ID.
     */
    @Test
    @Order(1)
    void testCreateUser() {
        // Generate a unique username to avoid conflicts
        uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.valueOf(role));

        UserRequest request = new UserRequest();
        request.setUsername(uniqueUsername);
        request.setPassword("password123");
        request.setRoles(roles);

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("username", equalTo(uniqueUsername))
                .body("roles", hasItem(role))
                .extract()
                .response();

        // Store the created ID for subsequent tests
        createdUserId = response.jsonPath().getLong("id");
        System.out.println("[DEBUG_LOG] Created User ID: " + createdUserId);
    }

    /**
     * Test getting a user by ID.
     * Expected: 200 OK response with the user data corresponding to what was sent in step 1.
     */
    @Test
    @Order(2)
    void testGetUserById() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/users/" + createdUserId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserId.intValue()))
                .body("username", equalTo(uniqueUsername))
                .body("roles", hasItem(role));
    }

    /**
     * Test getting all users.
     * Expected: 200 OK response with a list containing the created user.
     */
    @Test
    @Order(3)
    void testGetAllUsers() {
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<UserResponse> users = response.jsonPath().getList("", UserResponse.class);

        // Verify the list contains the created user
        boolean found = users.stream()
                .anyMatch(user -> user.getId().equals(createdUserId));

        assertTrue(found, "Created user not found in the list");
        System.out.println("[DEBUG_LOG] Found User in list: " + found);
    }

    /**
     * Test updating a user.
     * Expected: 200 OK response with the updated data.
     */
    @Test
    @Order(4)
    void testUpdateUser() {
        String updatedUsername = uniqueUsername + "_updated";
        Set<Role> updatedRoles = new HashSet<>();
        updatedRoles.add(Role.fromString(role));
        updatedRoles.add(Role.fromString("ROLE_ADMIN"));

        UserRequest request = new UserRequest();
        request.setUsername(updatedUsername);
        request.setPassword("newpassword123");
        request.setRoles(updatedRoles);

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/users/" + createdUserId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserId.intValue()))
                .body("username", equalTo(updatedUsername))
                .body("roles", hasItems(role, "ROLE_ADMIN"));

        // Update the username for subsequent tests
        uniqueUsername = updatedUsername;
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
                .get("/api/users/" + createdUserId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserId.intValue()))
                .body("username", equalTo(uniqueUsername))
                .body("roles", hasItems(role, "ROLE_ADMIN"));
    }

    /**
     * Test deleting a user.
     * Expected: 204 No Content response.
     */
    @Test
    @Order(6)
    void testDeleteUser() {
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/api/users/" + createdUserId)
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
                .get("/api/users/" + createdUserId)
                .then()
                .statusCode(404);
    }
}
