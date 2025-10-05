package az.qrfood.backend.kitchendepartment;

import az.qrfood.backend.kitchendepartment.dto.KitchenDepartmentDto;
import az.qrfood.backend.util.AbstractTest;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.FakeData;
import az.qrfood.backend.util.TestUtil;
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

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KitchenDepartmentApiTest extends AbstractTest {

    @Value("${eatery.id.kitchen-department}")
    private String kitchenDepartmentUrl;
    @Value("${eatery.id.kitchen-department.id}")
    private String kitchenDepartmentUrlId;

    private Long departmentId;
    private String departmentName;

    @Test
    @Order(1)
    void createDepartment() {
        log.debug("\n==================== 📥 CREATE DEPARTMENT =====================");

        String url = TestUtil.formatUrl(kitchenDepartmentUrl , eateryId.toString());
        departmentName = FakeData.categoryName() + " Dept";
        Map<String, Object> payload = Map.of(
                "name", departmentName,
                "restaurantId", eateryId
        );

        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(TestUtil.formatUrl(url))
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        KitchenDepartmentDto dto = response.as(KitchenDepartmentDto.class);
        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(departmentName, dto.getName());
        departmentId = dto.getId();
        log.info("Created department with ID [{}] and name [{}]", departmentId, departmentName);
    }

    @Test
    @Order(2)
    void getDepartmentsForRestaurant() {
        log.debug("\n==================== 🟢 GET DEPARTMENTS FOR RESTAURANT =====================");
        String url = TestUtil.formatUrl(kitchenDepartmentUrl , eateryId.toString());
        Response response = ApiUtils.sendGetRequest(baseUrl, jwtToken, url, 200);
        List<KitchenDepartmentDto> departments = response.as(new io.restassured.common.mapper.TypeRef<List<KitchenDepartmentDto>>() {});
        assertFalse(departments.isEmpty(), "Departments list should not be empty");

        boolean found = departments.stream().anyMatch(d -> d.getId().equals(departmentId) && d.getName().equals(departmentName));
        assertTrue(found, "Created department must be present in the list");
    }

    @Test
    @Order(3)
    void updateDepartment() {
        log.debug("\n==================== 🔄 UPDATE DEPARTMENT =====================");
        String newName = departmentName + " Updated";

        Map<String, Object> payload = Map.of("name", newName);

        String url = TestUtil.formatUrl(kitchenDepartmentUrlId , eateryId.toString(), departmentId.toString());
        Response response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(url)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        KitchenDepartmentDto dto = response.as(KitchenDepartmentDto.class);
        assertNotNull(dto);
        assertEquals(departmentId, dto.getId());
        assertEquals(newName, dto.getName());

        // Verify via GET list

        String urlGet = TestUtil.formatUrl(kitchenDepartmentUrl , eateryId.toString());
        Response listResponse = ApiUtils.sendGetRequest(baseUrl, jwtToken, urlGet, 200);
        List<KitchenDepartmentDto> departments = listResponse.as(new io.restassured.common.mapper.TypeRef<List<KitchenDepartmentDto>>() {});
        boolean found = departments.stream().anyMatch(d -> d.getId().equals(departmentId) && d.getName().equals(newName));
        assertTrue(found, "Updated department must be present with new name");

        departmentName = newName;
    }

    @Test
    @Order(4)
    void deleteDepartment() {
        log.debug("\n==================== 🔴 DELETE DEPARTMENT =====================");

        String url = TestUtil.formatUrl(kitchenDepartmentUrlId , eateryId.toString(), departmentId.toString());

        given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(url)
                .then()
                .log().all()
                .statusCode(200);

        // Verify it's no longer in the list

        String urlGet = TestUtil.formatUrl(kitchenDepartmentUrl , eateryId.toString());
        Response listResponse = ApiUtils.sendGetRequest(baseUrl, jwtToken, urlGet, 200);
        List<KitchenDepartmentDto> departments = listResponse.as(new io.restassured.common.mapper.TypeRef<List<KitchenDepartmentDto>>() {});
        boolean found = departments.stream().anyMatch(d -> d.getId().equals(departmentId));
        assertFalse(found, "Deleted department must not be present");
    }
}
