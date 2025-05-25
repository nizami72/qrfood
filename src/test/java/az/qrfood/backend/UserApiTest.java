package az.qrfood.backend;


import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserApiTest {

    @Value("${base.url}")
    int baseUrl;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = baseUrl;
    }

    @Test
    void shouldReturnUserById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/api/users/{id}")
                .then()
                .statusCode(200)
//                .body("id", equalTo(1))
//                .body("name", equalTo("John"))
                ;
    }
}
