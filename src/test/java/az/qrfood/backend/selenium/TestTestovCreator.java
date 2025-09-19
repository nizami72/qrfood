package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_CATEGORIES;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_DISHES;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_STAFF;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_TABLES;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_EDIT_EATERY;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_LOGIN;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_REGISTRATION;
import static az.qrfood.backend.selenium.SeleniumUtil.markTime;
import static az.qrfood.backend.selenium.SeleniumUtil.pause;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.TestUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class TestTestovCreator {

    //<editor-fold desc="Fields">
    private final TestConfig config = ConfigFactory.create(TestConfig.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private Testov testov;
    private String howFast;
    String host;
    final static boolean visualEffect = false;
    private long totalTime;
    StaffItem admin;
    private String loginUrl;
    private String superAdminMail = "nizami.budagov@gmail.com";
    private String superAdminPass = "qqqq1111";
    private String eateriesByAdminUrl;
    private String eateryAdminUrl;
    //</editor-fold>

    @BeforeEach
    public void setUp() throws IOException {
        host = config.host();
        eateriesByAdminUrl = config.eateryAdminEateries();
        eateryAdminUrl = config.eateryAdminUrl();

        loginUrl = config.loginUrl();
        String fileWithData = System.getenv("JSON_SOURCE");
        howFast = System.getenv("HOW_FAST");


        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(fileWithData), Testov.class);
        Assertions.assertNotNull(testov);
        admin = testov.getStaff().stream().filter(s -> s.getRoles().contains("EATERY_ADMIN")).findFirst()
                .orElseThrow();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("start-maximized"); // Launch maximized first

        // Disable the "Chrome is being controlled..." message
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Disable Chrome password manager
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().fullscreen();

        // Move and resize a window to the desired monitor /position
        driver.manage().window().setPosition(new Point(1900, -10));
        driver.manage().window().setSize(new Dimension(1960, 1380));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(200));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void flow() {

        // ================== DELETE Eatery IF EXISTS ==================
        deleteEateryBeforeCreation();

        // ================== REGISTER USER ==================
        registerUser();

        // ================== LOGIN USER ==================
        login();

        // ================= EDIT EATERY ==================
        editEatery();

        // ================== CREATE CATEGORY ==================
        createCategories();

        // ================== CREATE DISHES ==================
        createDishes();

        // ================== CREATE STAFF ==================
        createStaff();

        // ================== CREATE TABLES AND ASSIGNMENTS ==================
        createTables();

    }

    private void registerUser() {
        EateryBuilder.openPage(driver, host, "register", howFast);
        EateryBuilder.registerEateryAdmin(driver, wait, admin.getProfile().getName(), admin.getPassword(), admin.getEmail(),
                testov.getEatery().getName(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_REGISTRATION);
    }

    private void login() {
        EateryBuilder.openPage(driver, host, "login", howFast);
        EateryBuilder.login(driver, wait, admin.getEmail(), admin.getPassword(), testov.getEatery().getName(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_LOGIN);
    }

    private void editEatery() {
        EateryBuilder.editEatery(driver, howFast);
        pause(howFast);
        totalTime += markTime(PHASE_EDIT_EATERY);

    }

    private void createCategories() {
        List<String> categoryNames = testov.getCategories().stream()
                .map(CategoryDto::getNameEn)
                .toList();
        EateryBuilder.createCategories(driver, wait, howFast, categoryNames);
        pause(howFast);
        totalTime += markTime(PHASE_CREATE_CATEGORIES);
    }

    private void createDishes() {
        EateryBuilder.navigate(driver, wait, "nav003", "/admin/menu", howFast);
        List<CategoryDto> categories = testov.getCategories();
        for (CategoryDto category : categories) {
            SeleniumUtil.selectOptionIdAndText(driver, "dishes-category-select", category.getNameAz(), howFast);
            pause(howFast);
            List<String> dishDtos = category.getDishes().stream()
                    .map(DishDto::getNameEn)
                    .toList();
            EateryBuilder.createDishes(driver, wait, howFast, dishDtos);
        }

        pause(howFast);
        totalTime += markTime(PHASE_CREATE_DISHES);
    }

    private void createStaff() {
        for (StaffItem staff : testov.getStaff()) {
            EateryBuilder.createUser(driver, wait, staff, howFast);
        }
        pause(howFast);
        totalTime += markTime(PHASE_CREATE_STAFF);
    }

    private void createTables() {
        EateryBuilder.createTables(driver, wait, testov.getTables(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_CREATE_TABLES);
        log.debug("Total time [{}]", totalTime);
    }

    private void deleteEateryBeforeCreation() {
        String jwt = ApiUtils.login(superAdminMail, superAdminPass, null, host, loginUrl);
        Long id = getEateryId(jwt, testov.getEatery().getName(), host, Utils.replacePlaceHolders(eateriesByAdminUrl, admin.getEmail()));
        ApiUtils.sendDeleteRequest(host, jwt, eateryAdminUrl + "/" + id, 200);
    }

    public static Long getEateryId(String jwt, String eateryName, String host, String url) {
        Response res = ApiUtils.sendGetRequest(host, jwt, url);
        if (res.statusCode() != 200) return null;
        List<Map<String, Object>> d = res.as(List.class);
        Object id = d.stream()
                .filter(eateryDto -> eateryDto
                        .get("name")
                        .equals(eateryName))
                .findFirst()
                .orElseThrow()
                .get("id");
        return Long.valueOf(id.toString());
    }


}