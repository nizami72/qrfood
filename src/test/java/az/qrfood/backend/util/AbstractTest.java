package az.qrfood.backend.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public abstract class AbstractTest {

    @LocalServerPort
    protected int port;
    @Value("${test.data.json-source}")
    protected String jsonSourceFile;
    @Value("${admin.api.eatery}")
    String adminApiEateryUrl;
    @Value("${auth.login}")
    String loginUrl;

    protected String baseUrl;
    protected CategoryDto ci;
    protected String jwtToken;
    protected Long eateryId;
    protected Testov testov;
    protected StaffItem admin;
    protected Long userId;

    @BeforeAll
    void setup() throws Exception {
        // STEP 3: Construct the baseUrl using the injected random port
        baseUrl = "http://localhost:" + port;
        System.out.println("Test server running on: " + baseUrl); // For debugging

        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(jsonSourceFile), Testov.class);
        admin = testov.getStaff().stream().filter(s -> s.getRoles().contains("EATERY_ADMIN")).findFirst()
                .orElseThrow();
        ci = testov.getCategories().get(0);
        registerUserAndEatery();
        login();
    }

    public void registerUserAndEatery() {
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
        Response registerResponse = ApiUtils.sendPostRequest(baseUrl, adminApiEateryUrl, registerRequest, 201);
        RegisterResponse rr = registerResponse.as(RegisterResponse.class);
        assertNotNull(rr);
        assertNotNull(rr.userId());
        assertNotNull(rr.eateryId());
        assertTrue(rr.success());
        this.userId = rr.userId();
        this.eateryId = rr.eateryId();
        log.debug("Registered eatery and eatery admin [{}]", rr);
    }

    public void login() {
        log.debug("Login as [{}]", admin.getEmail());
        LoginRequest loginRequest = new LoginRequest(admin.getEmail(), admin.getPassword(), eateryId);
        Response authResponse = ApiUtils.sendPostRequest(baseUrl, loginUrl, loginRequest, 200);
        jwtToken = authResponse.jsonPath().getString("jwt");
        log.debug("Loged in as [{}], JWT token: [{}]",admin.getEmail(), jwtToken);
    }
}