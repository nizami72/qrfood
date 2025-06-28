package az.qrfood.backend.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.Role;
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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Integration test for the User API.
 * This test covers the following operations for the User entity:
 * - Create a user
 * - Get a user by ID
 * - Get all users
 * - Update a user
 * - Delete a user
 */
@SpringBootTest(properties = "spring.config.name=application")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class UserApiTest {

    //<editor-fold desc="Field">
    private static PrintStream fileLog;
    @Value("${admin}")
    String uriSuperAdminRegister;
    @Value("${admin.eatery}")
    String uriAdminEateryRegister;


    @Value("${base.url}")
    String baseUrl;
    @Value("${usr}")
    String controllerPath1;
    @Value("${user.general}")
    String userRegisterGeneral;
    @Value("${user.n}")
    String userN;
    @Value("${users}")
    String users;
    @Value("${user.id}")
    String userId;

    String jwtToken;
    //    String uniqueUsername;
    String role = Role.WAITER.name();
    String eateryId = "2";
    String controllerPath;
    RegisterRequest adminRegisterRequest;
    RegisterResponse registerResponse;
    RegisterResponse adminRegisterResponse;
    List<RegisterResponse> generalUserRegisterResponses;

    //</editor-fold>

    //<editor-fold desc="Before All Setup">
    @BeforeAll
    void setupPaths() {
        controllerPath = controllerPath1.replace("{eateryId}", eateryId);
    }

    @BeforeAll
    void setupLogging() throws Exception {
        fileLog = new PrintStream(new FileOutputStream("logsTest/test-user-controller.log", false));
        RestAssured.filters(
                new RequestLoggingFilter(fileLog),
                new ResponseLoggingFilter(fileLog)
        );
    }

    @BeforeAll
    void login() {
//       jwtToken = login("nizami.budagov@gmail.com", "qqqq1111");
    }
    //</editor-fold>

    /**
     * Test creating a user.
     * Expected: 201 Created response with the new user's ID.
     */
    @Test
    @Order(1)
    void postJustUser() {
        fileLog.println("\n !!Test1 ==================== 📥 POST User Order =====================");
        // Generate a unique username to avoid conflicts
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.valueOf(role));

        UserRequest request = new UserRequest();
        request.setUsername(uniqueUsername);
        request.setPassword("password123");
        request.setRoles(roles);

        Response response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(uriSuperAdminRegister)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("username", equalTo(uniqueUsername))
                .body("roles", hasItem(role))
                .extract()
                .response();

        Long createdUserId = response.jsonPath().getLong("id");
        log.debug(" Created User ID [{}]", createdUserId);

        String pass = "qqqq1111";
        String mail = "nizami.budagov@gmail.com";
        String jwt = login(mail, pass);

        fileLog.println("\n==================== 📥 Get User by ID =====================");
        UserResponse userResponse = getUserById(createdUserId);
        assertEquals(uniqueUsername, userResponse.getUsername());

        fileLog.println("\n==================== 📥 Delete user =====================");
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .when()
                .delete(uriSuperAdminRegister + "/" + userResponse.getId().toString())
                .then()
                .statusCode(204);
    }

    /**
     * Registers a new user with the restaurant.
     */
    @Test
    @Order(2)
    void postEateryAdmin() {
        fileLog.println("\n !!Test2 ==================== 📥 POST Admin User with Eatery =====================");
        // login as super admin
        String superAdminPassword = "qqqq1111";
        String superAdminMail = "nizami.budagov@gmail.com";
        String superAdminJwt = login(superAdminMail, superAdminPassword);

        // create admin and eatery
        adminRegisterRequest = TestUtil.createRegisterRequest(true);
        adminRegisterResponse = postUserAndEateryAsAdmin(superAdminJwt, adminRegisterRequest,
                uriSuperAdminRegister + uriAdminEateryRegister)
                .as(RegisterResponse.class);
        assertTrue(adminRegisterResponse.success());
        fileLog.println("Created eatery and its admin: " + adminRegisterResponse);
    }

    @Test
    @Order(3)
    void createEateryOtherUsers() {
        fileLog.println("\n !!Test3 ============== 📥 POST create a couple of users with different roles -3- ==========");
        Long eatery = adminRegisterResponse.eateryId();
        String adminJwt = login(adminRegisterRequest.getUser().getEmail(), adminRegisterRequest.getUser().getPassword());
        List<RegisterRequest> requests = List.of(
                TestUtil.createRegisterRequest(Set.of(Role.WAITER), false),
                TestUtil.createRegisterRequest(Set.of(Role.CASHIER), false),
                TestUtil.createRegisterRequest(Set.of(Role.KITCHEN_ADMIN), false)
        );
        generalUserRegisterResponses = requests.stream()
                .map(r -> {
                    RegisterResponse response = postGeneralUser(adminJwt, r, eatery).as(RegisterResponse.class);
                    log.debug("Created user: {}", response);
                    return response;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        assertEquals(3, generalUserRegisterResponses.size(), "Should be 4 members");
    }

    // check created delete users
    // delete admin and eatery

    @Test
    @Order(4)
    void getAllUsers() {
        fileLog.println("\n !! Test4 ==================== 📥 Get all users of the eatery =====================");

        String jwt = login(adminRegisterRequest.getUser().getEmail(), adminRegisterRequest.getUser().getPassword());
        List<UserResponse> users = findAllUsersForEatery(jwt, adminRegisterResponse.eateryId());
        // Verify the list contains the created user
        boolean found = users.stream()
                .anyMatch(user -> user.getId().equals(adminRegisterResponse.userId()));
        log.debug("Found User in list [{}]", found);
        assertEquals(4, users.size());
        assertTrue(found, "Created user not found in the list");
    }

    /**
     * Test updating a user.
     * Expected: 200 OK response with the updated data.
     */
    @Test
    @Order(5)
    void putUser() {
        fileLog.println("\n !!Test5 ==================== 📥 Updating User =====================");
        RegisterResponse u = generalUserRegisterResponses.get(0);
        Long eatery = adminRegisterResponse.eateryId();

        int initV = u.userId().intValue();
        String updatedUsername = u.name() + "_updated";
        Set<Role> updatedRoles = new HashSet<>();
        updatedRoles.add(Role.WAITER);

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
                .put(controllerPath1.replace("{eateryId}", eatery.toString())
                        + userId.replace("{userId}", u.userId().toString()))
                .then()
                .statusCode(200)
                .body("id", equalTo(initV))
                .body("username", equalTo(updatedUsername))
                .body("roles", hasItems(role, "WAITER"));
    }

    String createdUserId = "todo";


    /**
     * Test deleting a user.
     * Expected: 204 No Content response.
     */
    @Test
    @Order(6)
    void deleteUser() {
        fileLog.println("\n !!Test6 ==================== 📥 =====================");
        Long eateryId = adminRegisterResponse.eateryId();
        RegisterResponse u = generalUserRegisterResponses.get(2);
        assertEquals(4, findAllUsersForEatery(jwtToken, eateryId).size());
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(controllerPath1.replace("{eateryId", eateryId.toString())
                        + userId.replace("{userId}", u.userId().toString()))
                .then()
                .statusCode(204);
        assertEquals(3, findAllUsersForEatery(jwtToken, eateryId).size());
    }


    /**
     * Get a user by ID.
     * Expected: 200 OK responses with the user data corresponding to what was sent in step 1.
     */
    private UserResponse getUserById(Long userId1) {
        String s = userId1.toString();
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(controllerPath + userId.replace("{userId}", s))
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.as(UserResponse.class);
    }

    private String login(String mail, String password) {

        fileLog.println("\n==================== 📥 LOGIN =====================");

        String arg = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """;
        String authPayload = String.format(arg, mail, password);

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

        fileLog.println("\n==================== 📥 Login Success =====================");
        return jwtToken;
    }

    private Response postUserAndEateryAsAdmin(String jwt, RegisterRequest registerRequest, String uri) {
        fileLog.println("\n==================== 📥 POST User with Eatery Order =====================");
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post(uri)
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    private Response postGeneralUser(String jwt, RegisterRequest registerRequest, Long eateryId) {
        fileLog.println("\n==================== 📥 POST User with Eatery Order =====================");
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post(controllerPath1.replace("{eateryId}", eateryId.toString()) + userRegisterGeneral)
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", equalTo("User and a eatery successfully created!"))
                .extract()
                .response();
    }

    private List<UserResponse> findAllUsersForEatery(String jwt, Long eateryId) {
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(controllerPath1.replace("{eateryId}", eateryId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.jsonPath().getList("", UserResponse.class);
    }

}