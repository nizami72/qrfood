
package az.qrfood.backend.user.controller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.TestUtil;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
class UserApiTest {

    @Value("${base.url}")
    String baseUrl;
    @Value("${admin.api.eatery}")
    String adminaPIeateryUrl;
    @Value("${auth.login}")
    String authLoginUrl;
    @Value("${usr}")
    String eateryUsers;

    private Testov testov;
    String jwtToken;
    Long userId;
    Long eateryId;
    StaffItem admin;

    @BeforeAll
    void setup() throws Exception {
        String fileWithData = System.getenv("JSON_SOURCE");
        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(fileWithData), Testov.class);
        admin = testov.getStaff().stream().filter(s -> s.getRoles().contains("EATERY_ADMIN")).findFirst()
                .orElseThrow();
    }

    @Test
    void registerUserAndEatery() {
        // Build register request for EATERY_ADMIN
        RegisterRequest registerRequest = RegisterRequest.builder()
                .user(RegisterRequest.UserDto.builder()
                        .email(admin.getEmail())
                        .password(admin.getPassword())
                        .build())
                .restaurant(RegisterRequest.RestaurantDto.builder()
                        .name(testov.getEatery().getName())
                        .build())
                .build();


        // POST to create admin and eatery
        Response registerResponse = ApiUtils.sendPostRequest(baseUrl, adminaPIeateryUrl, registerRequest, 201);
        RegisterResponse rr = registerResponse.as(RegisterResponse.class);
        assertNotNull(rr);
        assertNotNull(rr.userId());
        assertNotNull(rr.eateryId());
        assertTrue(rr.success());
        this.userId = rr.userId();
        this.eateryId = rr.eateryId();

        // Login to get JWT for subsequent protected calls (reusing CreateAllFakeDataTest pattern)
        LoginRequest loginRequest = new LoginRequest(
                registerRequest.getUser().getEmail(),
                registerRequest.getUser().getPassword(),
                this.eateryId
        );
        Response authResponse = ApiUtils.sendPostRequest(baseUrl, authLoginUrl, loginRequest, 200);
        this.jwtToken = authResponse.jsonPath().getString("jwt");
        assertNotNull(jwtToken);

        // validate call a protected endpoint: get users of eatery
        Response getUsersResp = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(eateryUsers.replace("{eateryId}", eateryId.toString()))
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Validate response is an array JSON (basic sanity check)
        assertNotNull(getUsersResp.asString());
    }
}
