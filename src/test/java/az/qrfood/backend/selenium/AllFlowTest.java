package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.Util.FAST;
import static az.qrfood.backend.selenium.Util.NORM;
import static az.qrfood.backend.selenium.Util.NORM15;
import static az.qrfood.backend.selenium.Util.pause;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Selenium test class for testing the admin registration page functionality.
 */
@Log4j2
public class AllFlowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Test data
    private String testName;
    private String testEmail;
    private String testPassword;
    private String testRestaurantName;
    private String host;
    Properties properties = new Properties();
    private long t = System.currentTimeMillis();
    private static String PHASE_OPEN_PAGE = "Open page",
            PHASE_REGISTRATION = "Registration",
            PHASE_LOGIN = "Login",
            PHASE_CREATE_DISHES = "Create dished",
            PHASE_CREATE_TABLES = "Create Tables",
            PHASE_DELETE_USER = "Delete user";

    //        @Test
//    @Order(1)
    public void registration() {
        openPage("register", 400);
        registration(NORM);
        pause(500);
    }

    //    @Test
    @Order(2)
    public void loginAndOpenEateryPage() {
        openPage("login", 500);
        login(NORM);
    }

    //    @Test
    @Order(3)
    public void editEatery() {
        openPage("login", 500);
        login(NORM);
        editEatery(NORM);
        pause(2000);
    }

    //    @Test
    @Order(4)
    public void editCategory() {
        openPage("login", 500);
        login(NORM);
        createCategories(NORM15);
        pause(2000);

    }


    //    @Test
    @Order(4)
    public void editDishes() {
        openPage("login", 400);
        login(NORM);
        createDishes(NORM);
        pause(1000);
    }


    //    @Test
    @Order(5)
    public void createTable() {
        openPage("login", 400);
        login(NORM);
        createTables(NORM);
        pause(2000);

    }


    @Test
    public void allFlow() {
        openPage("login", 500);
        if (login(FAST)) {
            navigate("nav006", "/admin/users", NORM);
            deleteUser(FAST);
        }

//1
        openPage("register", 400);
        registration(NORM);

        pause(500);
        login(NORM);

        editEatery(NORM);
        pause(2000);
//        4
        createCategories(NORM15);
        pause(2000);

        createDishes(NORM);
        pause(1000);

        createUser(NORM);
        pause(2000);
//6
        createTables(NORM);
        pause(2000);


    }

//    @Test
    public void loginAndShowOrder() {
        openPage("login", 100);
        login(NORM);
        navigate("nav004", "/admin/tables", NORM);
        Util.pause(4000);
        navigate("nav005", "/admin/orders", NORM);
        Util.pause(40000);
    }

//    @Test
    public void loginAndAddUser() {
        openPage("login", 100);
        login(NORM);
        createUser(NORM);
    }



    private void createTables(String norm) {
        navigate("nav004", "/admin/tables", norm);
        Util.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
        Util.typeIntoInputById(driver, "Masa 1", "tblcrrt01", norm);
        Util.typeIntoInputById(driver, "6", "tblcrrt02", norm);
        Util.typeIntoInputById(driver, "VIP Masa", "tblcrrt03", norm);
        Util.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
        WebElement qrCode = driver.findElement(By.cssSelector("[id^='qr-card']"));
        Util.highlight2(driver, qrCode, norm);
        Util.findButtonByTextAndClick(driver, "Çap et", norm);
        Util.pause(4000);
        markTime(PHASE_CREATE_TABLES);
    }

    private void deleteUser(String temp) {
        Util.findButtonByTextAndClick(driver, "Sil", temp);
        Util.alertAccept(wait, driver, NORM);
        Util.findButtonByTextAndClick(driver, "Çıxış", temp);
        markTime(PHASE_DELETE_USER);
    }

    private void openPage(String path, int pause) {
        driver.get(host + "/admin/" + path);
        // Maximize the window first (optional, but often a good starting point)
        driver.manage().window().maximize();
        // Set to full-screen mode
//        driver.manage().window().fullscreen();
        Util.pause(pause);
        // Keep the browser open for a few seconds to observe
        markTime(PHASE_OPEN_PAGE);
    }

    private void registration(String temp) {

        // 2. Enter registration details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("106")));
        WebElement nameInput = driver.findElement(By.id("106"));
        Util.typeIntoInput(driver, nameInput, testName, temp);

        WebElement emailInput = driver.findElement(By.id("107"));
        Util.typeIntoInput(driver, emailInput, testEmail, temp);

        WebElement passwordInput = driver.findElement(By.id("108"));
        Util.typeIntoInput(driver, passwordInput, testPassword, temp);

        WebElement confirmPasswordInput = driver.findElement(By.id("109"));
        Util.typeIntoInput(driver, confirmPasswordInput, testPassword, temp);

        WebElement restaurantNameInput = driver.findElement(By.id("110"));
        Util.typeIntoInput(driver, restaurantNameInput, testRestaurantName, temp);

        // 3. Click the Register button
        WebElement registerButton = driver.findElement(By.id("111"));
        Util.click(driver, registerButton, temp);

        // 4. Wait for and accept the alert
        Util.alertAccept(wait, driver, NORM);
        wait.until(ExpectedConditions.urlContains("/admin/login"));
        // 1. Verify redirection to login page
        assertTrue(driver.getCurrentUrl().contains("/admin/login"),
                "URL should contain '/admin/login' after successful registration");
        markTime(PHASE_REGISTRATION);

    }

    private boolean login(String temp) {
        try {
            // 1. Wait for the login form
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));

            WebElement loginEmailInput = driver.findElement(By.id("101"));
            Util.typeIntoInput(driver, loginEmailInput, testEmail, temp);

            WebElement loginPasswordInput = driver.findElement(By.id("102"));
            Util.typeIntoInput(driver, loginPasswordInput, testPassword, temp);

            WebElement loginButton = driver.findElement(By.id("103"));
            Util.click(driver, loginButton, temp);

            // 2. Check if login succeeded by checking URL
            boolean loggedIn;
            try {
                wait.until(ExpectedConditions.urlContains("/admin/restaurants"));
                loggedIn = driver.getCurrentUrl().contains("/admin/restaurants");
            } catch (TimeoutException e) {
                System.out.println("Login failed: URL did not change to /admin/restaurants");
                return false; // Exit method silently if login failed
            }

            if (!loggedIn) {
                System.out.println("Login failed: Not redirected to /admin/restaurants");
                return false;
            }

            // 3. Verify restaurant header
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
            List<WebElement> restaurantHeaders = driver.findElements(By.tagName("h2"));

            if (restaurantHeaders.isEmpty()) {
                System.out.println("No restaurant headers found after login");
                return false;
            }

            boolean foundRestaurant = restaurantHeaders.stream()
                    .anyMatch(header -> header.getText().equals(testRestaurantName));

            if (!foundRestaurant) {
                log.debug("Expected restaurant [{}] not found", testRestaurantName);
                return false;
            }
        } catch (NoSuchElementException | TimeoutException e) {
            log.debug("Login process encountered a problem: " + e.getMessage());
        }
        markTime(PHASE_LOGIN);
        return true;

    }

    private void navigate(String id, String expectedUrl, String temp) {
        WebElement nameInput = driver.findElement(By.id(id));
        Util.click(driver, nameInput, temp);
        wait.until(ExpectedConditions.urlContains(expectedUrl));
        Util.pause(NORM);
    }

    private void createCategories(String temp) {
        navigate("nav002", "/admin/categories", temp);
        Util.findButtonByTextAndClick(driver, "Kateqoriya əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "ch003", temp);
        Util.checkCheckbox(driver, "ch004", temp);
        Util.checkCheckbox(driver, "ch005", temp);
        Util.checkCheckbox(driver, "ch006", temp);
        Util.checkCheckbox(driver, "ch007", temp);
        Util.findButtonByTextAndClick(driver, "Seçilmiş kateqoriyaları yarat", NORM);
    }

    private void createDishes(String temp) {
        navigate("nav003", "menu", temp);
        Util.selectOptionByBySelectText(driver, 1, "Kateqoriya seçin", temp);
        Util.findButtonByTextAndClick(driver, "Yemək əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "dsh001", temp);
        Util.checkCheckbox(driver, "dsh002", temp);
        Util.checkCheckbox(driver, "dsh003", temp);
        Util.checkCheckbox(driver, "dsh004", temp);
        Util.findButtonByTextAndClick(driver, "Add Selected Dishes (", NORM);
        Util.selectOptionByBySelectText(driver, 2, "Kateqoriya seçin", temp);
        markTime(PHASE_CREATE_DISHES);
    }

    private void createUser(String temp) {
        navigate("nav006", "users", temp);
        Util.findButtonByTextAndClick(driver, "İstifadəçi əlavə et", NORM);
        Util.typeIntoInputById(driver, "Olivia Scott User", "user-name", NORM);
        Util.typeIntoInputById(driver, "OliviaScottUser@qaz.az", "user-username", NORM);
        Util.typeIntoInputById(driver, "qqqq1111", "user-password", NORM);
        Util.findButtonByTextAndClick(driver, "İstifadəçi əlavə et", NORM);

    }

    @BeforeEach
    // NOTE: load properties from create-eatery-test.properties
    public void setUp() throws IOException {
        properties.load(new FileReader("src/test/resources/create-eatery-test.properties"));

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            if (value != null && value.startsWith("${") && value.endsWith("}")) {
                String envKey = value.substring(2, value.length() - 1);
                String envValue = System.getenv(envKey);
                if (envValue != null) {
                    properties.setProperty(key, envValue);
                }
            }
        }

        host = properties.getProperty("host");
        testName = properties.getProperty("eatery.admin.name");
        testEmail = properties.getProperty("eatery.admin.email");
        testPassword = properties.getProperty("password");
        testRestaurantName = properties.getProperty("eatery.name");

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

        // Move and resize window to desired monitor/position
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


    private void markTime(String phase) {
        if (t == 0) {
            log.debug(phase);
            t = System.currentTimeMillis();
        } else {
            long now = System.currentTimeMillis();
            long passed = now - t;
            t = now;
            log.debug("Flow [{}] duration [{}] second", phase, passed / 1000);
        }
    }

    private void editEatery(String temp) {

        Util.findButtonByTextAndClick(driver, "Redaktə et", NORM);
        WebElement el = driver.findElement(By.id("115"));
        Util.typeIntoInput(driver, el, "68 Üzeyir Hacıbəyov, Bakı", temp);
        WebElement phone = driver.findElement(By.id("116"));
        Util.typeIntoInput(driver, phone, "50 123 4578", temp);
        WebElement lat = driver.findElement(By.id("118"));
        Util.typeIntoInput(driver, lat, "40.12345", temp);
        WebElement lang = driver.findElement(By.id("119"));
        Util.typeIntoInput(driver, lang, "49.9876", temp);
        Util.findButtonByTextAndClick(driver, "Yadda saxla", NORM);
    }

}