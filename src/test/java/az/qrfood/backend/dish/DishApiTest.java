package az.qrfood.backend.dish;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import az.qrfood.backend.dish.dto.CommonDishDto;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.util.AbstractTest;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.TestDataLoader;
import az.qrfood.backend.util.TestUtil;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishApiTest extends AbstractTest {

    //<editor-fold desc="Fields">
    @Value("${eatery.id.category.id.dish}")
    private String eateryIdCategoryIdDish;
    @Value("${eatery.id.category.id.dish.id}")
    private String eateryIdCategoryIdDishId;
    @Value("${eatery.id.category}")
    String eateryIdCategory;
    private Long categoryId; // Default category ID for tests
    private Long dishId; // Will be set during test execution
    //</editor-fold>

    @Test
    @Order(1)
    void shouldCreateCategory() {
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

        Response response = given()
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
    void shouldCreateDish() {
        log.debug("\n========== 📤 Creating a dish ==========");

        DishDto dish = testov.getCategories().getFirst().getDishes().getFirst();
        String json = TestDataLoader.serializeToJsonString(dish);

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .multiPart("data", "data.json", json.getBytes(StandardCharsets.UTF_8), "application/json")
                .multiPart("image", new File("src/test/resources/image/" + dish.getImage()))
                .when()
                .post(eateryIdCategoryIdDish.replace("{eateryId}", eateryId.toString())
                        .replace("{categoryId}", categoryId.toString()))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        dishId = Long.parseLong(response.getBody().asString());
        log.debug("Created dish ID: {}", dishId);

        log.debug("========== 📥 Dish created successfully ==========\n");
    }

    @Test
    @Order(3)
    void shouldGetDishById() {
        log.debug("\n========== 📤 Getting dish by ID ==========");
        DishDto dish = testov.getCategories().getFirst().getDishes().getFirst();
        DishDto actual = getDish(DishDto.class);
        validateDishDto(dish, actual);
        log.debug("========== 📥 Dish retrieved successfully ==========\n");
    }

    @Test
    @Order(4)
    void shouldGetAllDishesInCategory() {
        log.debug("\n========== 📤 Getting all dishes in category ==========");
        List<DishDto> dishDtos = getDishes(DishDto.class);
        assertEquals(1, dishDtos.size(), "List of dishes in category should not be empty");
        log.debug("========== 📥 Dishes retrieved successfully ==========\n");
    }

    @Test
    @Order(6)
    void shouldDeleteDishById() {
        log.debug("\n========== 📤 Deleting dish by ID ==========");

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(eateryIdCategoryIdDishId.replace("{eateryId}", eateryId.toString())
                        .replace("{categoryId}", categoryId.toString())
                        .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        log.debug("========== 📥 Dish deleted successfully ==========\n");
    }

    @Test
    @Order(5)
    void shouldUpdateDish() {
        log.debug("\n========== 📤 Updating dish ==========");
        DishDto expected = DishDto.builder()
                .dishId(dishId)
                .categoryId(categoryId)
                .nameAz("Updated Test Dish Az")
                .nameEn("Updated Test Dish En")
                .nameRu("Updated Test Dish Ru")
                .price(BigDecimal.valueOf(15.99))
                .descriptionAz("Updated Dish Description Az")
                .descriptionEn("Updated Dish Description En")
                .descriptionRu("Updated Dish Description Ru")
                .available(true)
                .build();

        String json = TestDataLoader.serializeToJsonString(expected);

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .multiPart("data", "data.json", json.getBytes(StandardCharsets.UTF_8), "application/json")
                .when()
                .put(eateryIdCategoryIdDishId.replace("{eateryId}", eateryId.toString())
                        .replace("{categoryId}", categoryId.toString())
                        .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(200);

        DishDto actual = getDish(DishDto.class);
        validateDishDto(expected, actual);
        log.debug("========== 📥 Dish updated successfully ==========\n");
    }

    @Test
    @Order(7)
    void shouldTestInterceptorValidation() {
        log.debug("\n========== 📤 Testing interceptor validation ==========");

        // Try to access a dish with incorrect eateryId (should be rejected by interceptor)
        long incorrectEateryId = eateryId + 100;

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(eateryIdCategoryIdDishId.replace("{eateryId}", Long.toString(incorrectEateryId))
                        .replace("{categoryId}", categoryId.toString())
                        .replace("{dishId}", dishId.toString()))
                .then()
                .log().all()
                .statusCode(409); // Expecting forbidden due to interceptor validation

        log.debug("========== 📥 Interceptor validation test completed ==========\n");
    }

    @Test
    @Order(8)
    void shouldGetCommonDishes() {
        log.debug("\n========== 📤 Getting common dishes ==========");
        Response res = ApiUtils.sendGetRequest(baseUrl, jwtToken, "/api/dish/common/Salads", 200);
        List<CommonDishDto> idList = res.as(new io.restassured.common.mapper.TypeRef<>() {
        });
        assertFalse(idList.isEmpty(), "List of predefined dishes in category should not be empty");
        log.debug("========== 📥 Common dishes retrieved successfully ==========\n");
    }

    @Test
    @Order(9)
    void shouldCreateDishesFromTemplates() {
        log.debug("\n========== 📤 Creating dishes from templates ==========");

        List<DishDto> dishes = List.of(DishDto.builder()
                .nameAz("Updated Test Dish Az")
                .nameEn("Updated Test Dish En")
                .nameRu("Updated Test Dish Ru")
                .price(BigDecimal.valueOf(15.99))
                .descriptionAz("Updated Dish Description Az")
                .descriptionEn("Updated Dish Description En")
                .descriptionRu("Updated Dish Description Ru")
                .available(true)
                .build());

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(dishes)
                .when()
                .post("/api/dish/common/" + categoryId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        List<Long> idList = response.as(new io.restassured.common.mapper.TypeRef<>() {
        });
        dishId = idList.getFirst();
        DishDto dishDto = getDish(DishDto.class);
        validateDishDto(dishDto, dishes.getFirst());
        log.debug("========== 📥 Dishes created from templates successfully ==========\n");
    }

    private <T> T getDish(Class<T> clazz) {
        String url = eateryIdCategoryIdDishId.replace("{eateryId}", eateryId.toString())
                .replace("{categoryId}", categoryId.toString())
                .replace("{dishId}", dishId.toString());

        Response res = ApiUtils.sendGetRequest(baseUrl, jwtToken, url, 200);
        return res.as(clazz);
    }

    private <T> List<T> getDishes(Class<T> clazz) {
        String url = eateryIdCategoryIdDish.replace("{eateryId}", eateryId.toString())
                .replace("{categoryId}", categoryId.toString());

        Response res = ApiUtils.sendGetRequest(baseUrl, jwtToken, url, 200);

        // Use jsonPath().getList() to deserialize into a List of the given class.
        // The "." argument specifies that the entire JSON response body is the list.
        return res.jsonPath().getList(".", clazz);
    }

    private void validateDishDto(DishDto expected, DishDto actual) {
        assertEquals(actual.getNameAz(), expected.getNameAz());
        assertEquals(actual.getNameEn(), expected.getNameEn());
        assertEquals(actual.getNameRu(), expected.getNameRu());
        assertEquals(actual.getPrice(), expected.getPrice());
        assertEquals(actual.getDescriptionAz(), expected.getDescriptionAz());
        assertEquals(actual.getDescriptionEn(), expected.getDescriptionEn());
        assertEquals(actual.getDescriptionRu(), expected.getDescriptionRu());
        log.debug("Expected [{}], actual [{}]", expected, actual);
    }

}
