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
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test2")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
public class TestTestovCreator {

    @LocalServerPort
    private int port;

    //<editor-fold desc="Fields">
    @Value("${api.auth.test-magic-link}")
    private String testMagicLinkUrl;
    @Value("${mail.hog.server.url}")
    String mailHogUrl;
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
        String fileWithData = "fakeData/Testov3Dishes3Tables.json";
        howFast = System.getenv("HOW_FAST");
        host = System.getenv("HOST");

        eateriesByAdminUrl = config.eateryAdminEateries();
        eateryAdminUrl = config.eateryAdminUrl();
        loginUrl = config.loginUrl();

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
//        registerUser();

        // ================== LOGIN USER ==================
        fireMagicLinkSending();

        loginAndOpenViaMagikLink();

        // ================= EDIT EATERY ==================
        editEatery(testov);

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
        EateryBuilder.openPage(driver, host, "login", howFast);
        EateryBuilder.registerEateryAdmin(driver, wait, admin.getEmail(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_REGISTRATION);
    }

    /**
     * Pause the flow for delay seconds.
     *
     * @param delay delay in seconds
     */
    private void pauseToConfirmEmail(long delay) {
        log.debug("Confirm email sent to [{}]", admin.getEmail());
        pause(delay * 1000);
    }

    private void fireMagicLinkSending() {
        EateryBuilder.openPage(driver, host, "auth", howFast);
        SeleniumUtil.typeIntoInput(driver, driver.findElement(By.id("email-input-unified-auth")), admin.getEmail(), howFast);
        SeleniumUtil.findButtonByIdAndClick(driver, "send-magic-link-button-unified-auth", howFast);
        pause(howFast);
        totalTime += markTime(PHASE_LOGIN);
    }

    private void login() {
        EateryBuilder.openPage(driver, host, "auth", howFast);
        SeleniumUtil.findButtonByIdAndClick(driver, "show-password-button-unified-auth", howFast);
        SeleniumUtil.typeIntoInput(driver, driver.findElement(By.id("email-input-unified-auth")), admin.getEmail(), howFast);
        SeleniumUtil.typeIntoInput(driver, driver.findElement(By.id("password-input-unified-auth")), admin.getPassword(), howFast);
        SeleniumUtil.findButtonByIdAndClick(driver, "sign-in-button-unified-auth", howFast);
        EateryBuilder.login(driver, wait, admin.getEmail(), admin.getPassword(), testov.getEatery().getName(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_LOGIN);
    }

    private void openMailAndClickLink() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mailHogUrl))
                .build();

        String responseBody = "";
        try {
            Thread.sleep(1000);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch emails from MailHog", e);
        }

       Pattern linkPattern = Pattern.compile("http://127\\.0\\.0\\.1:5173/auth/verify\\?token=[0-9a-z\\-]{0,36}");
        Matcher linkMatcher = linkPattern.matcher(responseBody);

        String confirmationLink = "";
        if (linkMatcher.find()) {
            confirmationLink = linkMatcher.group(0);
            System.out.println("Found confirmation link: " + confirmationLink);
        } else {
            throw new RuntimeException("Could not find confirmation link in email body!");
        }

        driver.get(confirmationLink);

    }

    private void editEatery(Testov testov) {
        EateryBuilder.editEatery(driver, testov, howFast);
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
        String baseUrl = "http://localhost:" + port;
        String jwt = ApiUtils.login(superAdminMail, superAdminPass, null, baseUrl, loginUrl);
        Long id = findEateryIdByName(jwt, testov.getEatery().getName(), baseUrl, Utils.replacePlaceHolders(eateriesByAdminUrl, admin.getEmail()));
        if (id == null) {
            log.debug("No Testov eatery found so nothing to delete");
            return;
        }
        ApiUtils.sendDeleteRequest(baseUrl, jwt, eateryAdminUrl + "/" + id, 200);
    }

    private void loginAndOpenViaMagikLink() {
        String baseUrl = "http://localhost:" + port;
        String jwt = ApiUtils.login(superAdminMail, superAdminPass, null, baseUrl, loginUrl);
        Response res = ApiUtils.sendGetRequest(host, jwt, testMagicLinkUrl);
        driver.get(res.asString());
    }

    public static Long findEateryIdByName(String jwt, String eateryName, String host, String url) {
        try {
            Response res = ApiUtils.sendGetRequest(host, jwt, url);
            if (res.statusCode() != 200) {
                return null;
            }

            List<Map<String, Object>> d = res.as(List.class);

            // Use Optional to safely handle the stream result
            Optional<Map<String, Object>> foundEatery = d.stream()
                    .filter(eateryDto -> eateryDto != null && eateryDto.get("name") != null &&
                            eateryDto.get("name").equals(eateryName))
                    .findFirst();

            if (foundEatery.isEmpty()) {
                return null; // Return null if not found (instead of orElseThrow())
            }

            Object id = foundEatery.get().get("id");
            if (id == null) {
                return null; // Return null if "id" key doesn't exist or its value is null
            }

            return Long.valueOf(id.toString());

        } catch (Exception e) {
            // Catch any exception (e.g., network error, parsing error, ClassCastException, NumberFormatException)
            return null;
        }
    }


}