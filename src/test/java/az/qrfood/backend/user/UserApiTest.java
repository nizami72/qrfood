package az.qrfood.backend.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.util.AbstractTest;
import az.qrfood.backend.util.TestUtil;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Integration test for the User API.
 * This test covers the following operations for the User entity:
 * - Create users with different roles (WAITER, CASHIER, KITCHEN_ADMIN)
 * - Get all users for an eatery
 * - Get a user by ID
 * - Get a user by username
 * - Update a user
 * - Delete a user
 * - Test forbidden access for SUPER_ADMIN endpoints with EATERY_ADMIN token
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiTest extends AbstractTest {

    private static PrintStream fileLog;

    @Value("${user.and.eatery}")
    String uriAdminApiEatery;
    @Value("${admin.eatery}")
    String uriAdminEateryRegister;
    @Value("${usr}")
    String uriEateryUserBase;
    @Value("${user.general}")
    String uriUserGeneral;
    @Value("${user.id}")
    String uriUserId;
    @Value("${user.n}")
    String uriUserN;
    @Value("${api.user}")
    String uriUserAll;
    @Value("${usr.delete}")
    String uriUserDeleteById;

    static class UserTestData {
        RegisterRequest request;
        RegisterResponse response;

        public UserTestData(RegisterRequest request, RegisterResponse response) {
            this.request = request;
            this.response = response;
        }

        public RegisterRequest getRequest() {
            return request;
        }

        public RegisterResponse getResponse() {
            return response;
        }
    }

    List<UserTestData> userTestDataSet;

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logs/test/test-user-controller.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    /**
     * Test creating users with WAITER, CASHIER, KITCHEN_ADMIN roles.
     * These users are associated with the eatery created in AbstractTest.
     */
    @Test
    @Order(1)
    void createEateryOtherUsers() {
        fileLog.println("\n !!Test1 ============== POST create a couple of users with different roles ==========");
        String adminJwt = super.jwtToken;
        Long currentEateryId = super.eateryId;

        List<RegisterRequest> requests = List.of(
                TestUtil.createRegisterRequest(Set.of(Role.WAITER), false),
                TestUtil.createRegisterRequest(Set.of(Role.CASHIER), false),
                TestUtil.createRegisterRequest(Set.of(Role.KITCHEN_ADMIN), false)
        );

        userTestDataSet = requests.stream()
                .map(r -> {
                    Response response = postGeneralUser(adminJwt, r, currentEateryId);
                    RegisterResponse rr = response.as(RegisterResponse.class);
                    log.debug("Created user: {}", rr);
                    return new UserTestData(r, rr);
                })
                .collect(Collectors.toCollection(ArrayList::new));
        assertEquals(3, userTestDataSet.size(), "Should be 3 members");
    }

    /**
     * Helper method to register a general user (WAITER, CASHIER, KITCHEN_ADMIN)
     * with an existing eatery.
     */
    private Response postGeneralUser(String jwt, RegisterRequest registerRequest, Long eateryId) {
        fileLog.println("\n==================== POST User with Eatery Order =====================\n" +
                "Registering user: " + registerRequest.getUser().getEmail() + " with roles: " + registerRequest.getUser().getRoles());
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post(uriUserGeneral.replace("{eateryId}", eateryId.toString()))
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", equalTo("User registered successfully"))
                .extract()
                .response();
    }

    /**
     * Test retrieving all users for the eatery.
     * Verifies that the admin user and the newly created general users are present.
     */
    @Test
    @Order(2)
    void testGetAllEateryUsers() {
        fileLog.println("\n !!Test2 ==================== Get all users of the eatery =====================\n");
        String adminJwt = super.jwtToken;
        Long currentEateryId = super.eateryId;

        List<UserResponse> users = findAllUsersForEatery(adminJwt, currentEateryId);

        assertEquals(1 + userTestDataSet.size(), users.size()); // 1 admin + 3 general users

        assertTrue(users.stream().anyMatch(user -> user.getId().equals(super.userId)), "Admin user not found in the list");

        for (UserTestData testData : userTestDataSet) {
            assertTrue(users.stream().anyMatch(user -> user.getId().equals(testData.getResponse().userId())), "General user " + testData.getResponse().userId() + " not found in the list");
        }
    }

    /**
     * Test retrieving a specific user by their ID.
     */
    @Test
    @Order(3)
    void testGetUserById() {
        fileLog.println("\n !!Test3 ==================== Get User by ID =====================\n");
        String adminJwt = super.jwtToken;
        Long currentEateryId = super.eateryId;
        UserTestData targetUserTestData = userTestDataSet.get(0); // Get the first general user
        RegisterResponse targetUserResponse = targetUserTestData.getResponse();
        RegisterRequest targetUserRequest = targetUserTestData.getRequest();

        UserResponse userResponse = getUserById(adminJwt, currentEateryId, targetUserResponse.userId());

        assertEquals(targetUserResponse.userId(), userResponse.getId());
        assertEquals(targetUserResponse.name(), userResponse.getName());
        assertTrue(userResponse.getRoles().containsAll(targetUserRequest.getUser().getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet()))
        );
    }

    /**
     * Helper method to get a user by ID.
     */
    private UserResponse getUserById(String jwt, Long eateryId, Long userId) {
        fileLog.println("Getting user by ID: " + userId);
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(uriUserId.replace("{eateryId}", eateryId.toString()).replace("{userId}", userId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.as(UserResponse.class);
    }

    /**
     * Test retrieving a specific user by their username.
     */
    @Test
    @Order(4)
    void testGetUserByUsername() {
        fileLog.println("\n !!Test4 ==================== Get User by Username =====================\n");
        String adminJwt = super.jwtToken;
        Long currentEateryId = super.eateryId;
        UserTestData targetUserTestData = userTestDataSet.get(1); // Get the second general user
        RegisterResponse targetUserResponse = targetUserTestData.getResponse();
        RegisterRequest targetUserRequest = targetUserTestData.getRequest();
        UserResponse userResponse = getUserByUsername(adminJwt, currentEateryId, targetUserRequest.getUser().getEmail());
        assertEquals(targetUserResponse.userId(), userResponse.getId());
        assertEquals(targetUserResponse.name(), userResponse.getName());
        assertTrue(userResponse.getRoles().containsAll(targetUserRequest.getUser().getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet())));
    }

    /**
     * Helper method to get a user by username.
     */
    private UserResponse getUserByUsername(String jwt, Long eateryId, String username) {
        fileLog.println("Getting user by username: " + username);
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(uriUserN.replace("{eateryId}", eateryId.toString()).replace("{userName}", username))
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.as(UserResponse.class);
    }

    /**
     * Test updating an existing user.
     */
    @Test
    @Order(5)
    void testUpdateUser() {
        fileLog.println("\n !!Test5 ==================== Updating User =====================\n");
        String adminJwt = super.jwtToken;
        Long currentEateryId = super.eateryId;
        UserTestData userToUpdateTestData = userTestDataSet.get(0); // Update the first general user
        RegisterResponse userToUpdateResponse = userToUpdateTestData.getResponse();

        String updatedUsername = userToUpdateResponse.name() + "_updated";
        Set<Role> updatedRoles = new HashSet<>();
        updatedRoles.add(Role.WAITER);
        updatedRoles.add(Role.CASHIER); // Add another role

        UserRequest request = new UserRequest();
        request.setUsername(updatedUsername);
        request.setPassword("newpassword123"); // Password update might not be reflected in UserResponse
        request.setRoles(updatedRoles);

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + adminJwt)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(uriUserId.replace("{eateryId}", currentEateryId.toString()).replace("{userId}", userToUpdateResponse.userId().toString()))
                .then()
                .statusCode(200)
                .body("id", equalTo(userToUpdateResponse.userId().intValue()))
                .body("username", equalTo(updatedUsername))
                .body("roles", hasItems(Role.WAITER.name(), Role.CASHIER.name()));

        // Verify the update by getting the user again
        UserResponse updatedUser = getUserById(adminJwt, currentEateryId, userToUpdateResponse.userId());
        assertEquals(updatedUsername, updatedUser.getUsername());
        assertTrue(updatedUser.getRoles().containsAll(updatedRoles.stream().map(Enum::name).collect(Collectors.toSet())));
    }

    /**
     * Test deleting a user.
     */
    @Test
    @Order(6)
    void testDeleteUser() {
        fileLog.println("\n !!Test6 ==================== Deleting User =====================\n");
        String adminJwt = super.jwtToken;
        Long currentEateryId = super.eateryId;
        UserTestData userToDeleteTestData = userTestDataSet.get(2); // Delete the third general user
        RegisterResponse userToDeleteResponse = userToDeleteTestData.getResponse();

        int initialUserCount = findAllUsersForEatery(adminJwt, currentEateryId).size();

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + adminJwt)
                .when()
                .delete(uriUserId.replace("{eateryId}", currentEateryId.toString()).replace("{userId}", userToDeleteResponse.userId().toString()))
                .then()
                .statusCode(204);

        // Verify deletion
        List<UserResponse> remainingUsers = findAllUsersForEatery(adminJwt, currentEateryId);
        assertEquals(initialUserCount - 1, remainingUsers.size());
        assertFalse(remainingUsers.stream().anyMatch(user -> user.getId().equals(userToDeleteResponse.userId())), "Deleted user still found in the list");
    }

    /**
     * Test attempting to get all users from all eateries with an EATERY_ADMIN token.
     * Expects a 403 Forbidden status code.
     */
    @Test
    @Order(7)
    void testGetAllUsersFromAllEateriesForbidden() {
        fileLog.println("\n !!Test7 ==================== Get all users from all eateries (Forbidden) =====================\n");
        String adminJwt = super.jwtToken;

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + adminJwt)
                .when()
                .get(uriUserAll)
                .then()
                .statusCode(403); // Expect Forbidden
    }

    /**
     * Test attempting to delete a user by name (SUPER_ADMIN endpoint) with an EATERY_ADMIN token.
     * Expects a 403 Forbidden status code.
     */
    @Test
    @Order(8)
    void testDeleteEateryAdminWithResourcesForbidden() {
        fileLog.println("\n !!Test8 ==================== Delete user by name (Forbidden) =====================\n");
        String adminJwt = super.jwtToken;
        // Try to delete the admin user created by AbstractTest setup
        String adminUserIdString = super.userId.toString();

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + adminJwt)
                .when()
                .post(uriUserDeleteById.replace("{id}", adminUserIdString))
                .then()
                .statusCode(403); // Expect Forbidden
    }

    /**
     * Helper method to find all users for a specific eatery.
     */
    private List<UserResponse> findAllUsersForEatery(String jwt, Long eateryId) {
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(uriEateryUserBase.replace("{eateryId}", eateryId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.jsonPath().getList("", UserResponse.class);
    }
}
