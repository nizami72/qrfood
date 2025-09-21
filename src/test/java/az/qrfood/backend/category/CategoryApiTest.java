package az.qrfood.backend.category;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.FakeData;
import az.qrfood.backend.util.TestUtil;
import io.restassured.builder.MultiPartSpecBuilder;
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
import org.springframework.boot.test.web.server.LocalServerPort; // <-- IMPORT THIS
import org.springframework.test.context.ActiveProfiles;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryApiTest {

    // STEP 2: Inject the random port that Spring chose for the server
    @LocalServerPort
    private int port;

    @Value("${test.data.json-source}")
    private String jsonSourceFile;

    // We will build this dynamically now, so the @Value is not needed
    private String baseUrl;

    @Value("${eatery.id.category}")
    String eateryIdCategory;
    @Value("${admin.api.eatery}")
    String adminApiEateryUrl;
    @Value("${auth.login}")
    String loginUrl;
    @Value("${eatery.id.category.id}")
    String uriEateryIdCategoryId;

    private Testov testov;
    StaffItem admin;
    CategoryDto ci;
    String jwtToken;
    Long eateryId;
    Long categoryId;
    Long userId;

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

    @Test
    @Order(1)
    void createCategory() {
        log.debug("\n==================== 📥 CREATE CATEGORY =====================");

        Map<String, String> categoryData = Map.of(
                "nameAz", ci.getNameAz(),
                "nameEn", ci.getNameEn(),
                "nameRu", ci.getNameRu()
        );

        MultiPartSpecBuilder dataPart = new MultiPartSpecBuilder(categoryData)
                .controlName("data")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8); // Explicitly set the charset!

        Response response =
                given()
                        .baseUri(baseUrl) // This will now have the correct port
                        .header("Authorization", "Bearer " + jwtToken)
                        .multiPart(dataPart.build())
                        .multiPart("image", new File("src/test/resources/image/salad.webp"), "image/webp")
                        .when()
                        .post(TestUtil.formatUrl(eateryIdCategory, eateryId.toString()))
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .response();

        categoryId = response.as(Long.class);
        log.info("Created category with ID [{}]", categoryId);
    }

    @Test
    @Order(2)
    void getCategoryById() {
        log.debug("\n==================== 🟢 GET CATEGORY BY ID =====================");
        String url = TestUtil.formatUrl(uriEateryIdCategoryId, eateryId.toString(), categoryId.toString());
        Response registerResponse = ApiUtils.sendGetRequest(baseUrl, jwtToken, url, 200);
        CategoryDto categoryDto = registerResponse.as(CategoryDto.class);
        assertEquals(categoryDto.getNameEn(), ci.getNameEn());
        assertEquals(categoryDto.getNameRu(), ci.getNameRu());
        assertEquals(categoryDto.getNameAz(), ci.getNameAz());
        log.debug("Created category [{}]", categoryDto);
    }

    @Test
    @Order(3)
    void getAllCategories() {
        log.debug("\n==================== 🟢 GET ALL CATEGORIES =====================");
        String url = TestUtil.formatUrl(eateryIdCategory, eateryId.toString());
        Response registerResponse = ApiUtils.sendGetRequest(baseUrl, jwtToken, url, 200);
        List<CategoryDto> categories = registerResponse.as(new io.restassured.common.mapper.TypeRef<List<CategoryDto>>() {});
        if (categories.isEmpty()) {
            throw new RuntimeException("No categories found");
        }
        CategoryDto categoryDto = categories.getFirst();
        assertEquals(categoryDto.getNameEn(), ci.getNameEn());
        assertEquals(categoryDto.getNameRu(), ci.getNameRu());
        assertEquals(categoryDto.getNameAz(), ci.getNameAz());
        log.debug("All categories: [{}]", categoryDto);
    }

    @Test
    @Order(4)
    void updateCategory() {
        log.debug("\n==================== 🔄 UPDATE CATEGORY =====================");

        String categoryName = FakeData.categoryName();

      String  updatedCategoryNameAz = categoryName + " Az Updated";
      String  updatedCategoryNameEn = categoryName + " En Updated";
      String  updatedCategoryNameRu = categoryName + " Ru Updated";

        Map<String, String> updatedCategoryData = Map.of(
                "nameAz", updatedCategoryNameAz,
                "nameEn", updatedCategoryNameEn,
                "nameRu", updatedCategoryNameRu
        );

        Response registerResponse = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .multiPart("data", updatedCategoryData, "application/json")
                .multiPart("image", new File("src/test/resources/image/soup.webp"), "image/webp")
                .when()
                .put(TestUtil.formatUrl(uriEateryIdCategoryId, eateryId.toString(), categoryId.toString()))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        Long d = registerResponse.as(Long.class);

        // Verify the update
        String url =  TestUtil.formatUrl(uriEateryIdCategoryId, eateryId.toString(), categoryId.toString());
        Response registerResponse1 = ApiUtils.sendGetRequest(baseUrl, jwtToken, url, 200);
        CategoryDto categoryDto = registerResponse1.as(CategoryDto.class);
        assertEquals(categoryDto.getNameEn(), updatedCategoryNameEn);
    }

    @Test
    @Order(5)
    void deleteCategory() {
        log.debug("\n==================== 🔴 DELETE CATEGORY =====================");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(uriEateryIdCategoryId, eateryId.toString(), categoryId.toString())
                .then()
                .log().all()
                .statusCode(200);

        // Verify the deletion
        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(uriEateryIdCategoryId, eateryId.toString(), categoryId.toString())
                .then()
                .log().all()
                .statusCode(404);
    }


    private void registerUserAndEatery() {
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
    }

    private void login() {
        log.debug("Login as [{}]", admin.getEmail());
        LoginRequest loginRequest = new LoginRequest(admin.getEmail(), admin.getPassword(), eateryId);
        Response authResponse = ApiUtils.sendPostRequest(baseUrl, loginUrl, loginRequest, 200);
        jwtToken = authResponse.jsonPath().getString("jwt");
    }

}
