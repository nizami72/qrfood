package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.SeleniumUtil.FAST;
import static az.qrfood.backend.selenium.SeleniumUtil.NORM;
import static az.qrfood.backend.selenium.SeleniumUtil.NORM_X_2;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_DISHES;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_TABLES;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_DELETE_USER;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_LOGIN;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_OPEN_PAGE;
import static az.qrfood.backend.selenium.SeleniumUtil.pause;
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

    private boolean deleteUser = true;

    // Test data
    private String testName;
    private String testEmail;
    private String testPassword;
    private String testRestaurantName;
    private String host;
    Properties properties = new Properties();
    private long t = System.currentTimeMillis();


    //        @Test
//    @Order(1)
    public void registration() {
        openPage("register", 400);
        SeleniumUtil.registerEateryAdmin(driver, wait, testName, testPassword, testEmail, testRestaurantName, NORM);
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
        SeleniumUtil.createCategories(driver, wait, NORM_X_2);
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

        // ================== DELETE USER ==================
        if (deleteUser) {
            openPage("login", 500);
            if (login(FAST)) {
                navigate("nav006", "/admin/users", NORM);
                deleteUser(FAST);
            }
        }

        // ================== REGISTER USER ==================
        openPage("register", 400);
        SeleniumUtil.registerEateryAdmin(driver, wait, testName, testPassword, testEmail, testRestaurantName, NORM);
        pause(500);

        // ================== LOGIN USER ==================
        login(NORM);

        // ================= EDIT USER ==================
        editEatery(NORM);
        pause(2000);

        // ================== CREATE CATEGORY ==================
        SeleniumUtil.createCategories(driver, wait, NORM_X_2);
        pause(2000);

        // ================== CREATE DISHES ==================
        createDishes(NORM);
        pause(1000);


        // ================== CREATE USER ==================
        createUser(NORM);
        pause(2000);

        // ================== CREATE TABLES ==================
        createTables(NORM);
        pause(2000);


    }

    //    @Test
    public void loginAndShowOrder() {
        openPage("login", 100);
        login(NORM);
        navigate("nav004", "/admin/tables", NORM);
        SeleniumUtil.pause(4000);
        navigate("nav005", "/admin/orders", NORM);
        SeleniumUtil.pause(40000);
    }

    //    @Test
    public void loginAndAddUser() {
        openPage("login", 100);
        login(NORM);
        createUser(NORM);
    }

    private void createTables(String norm) {
        navigate("nav004", "/admin/tables", norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
        SeleniumUtil.typeIntoInputById(driver, "Masa 1", "tblcrrt01", norm);
        SeleniumUtil.typeIntoInputById(driver, "6", "tblcrrt02", norm);
        SeleniumUtil.typeIntoInputById(driver, "VIP Masa", "tblcrrt03", norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
        WebElement qrCode = driver.findElement(By.cssSelector("[id^='qr-card']"));
        SeleniumUtil.highlight2(driver, qrCode, norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Çap et", norm);
        SeleniumUtil.pause(4000);
        markTime(PHASE_CREATE_TABLES);
    }

    private void deleteUser(String temp) {
        SeleniumUtil.findButtonByTextAndClick(driver, "Sil", temp);
        SeleniumUtil.alertAccept(wait, driver, NORM);
        SeleniumUtil.findButtonByTextAndClick(driver, "Çıxış", temp);
        markTime(PHASE_DELETE_USER);
    }

    private void openPage(String path, int pause) {
        driver.get(host + "/admin/" + path);
        // Maximize the window first (optional, but often a good starting point)
        driver.manage().window().maximize();
        // Set to full-screen mode
//        driver.manage().window().fullscreen();
        SeleniumUtil.pause(pause);
        // Keep the browser open for a few seconds to observe
        markTime(PHASE_OPEN_PAGE);
    }

    private boolean login(String temp) {
        try {
            // 1. Wait for the login form
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));

            WebElement loginEmailInput = driver.findElement(By.id("101"));
            SeleniumUtil.typeIntoInput(driver, loginEmailInput, testEmail, temp);

            WebElement loginPasswordInput = driver.findElement(By.id("102"));
            SeleniumUtil.typeIntoInput(driver, loginPasswordInput, testPassword, temp);

            WebElement loginButton = driver.findElement(By.id("103"));
            SeleniumUtil.click(driver, loginButton, temp);

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
        SeleniumUtil.click(driver, nameInput, temp);
        wait.until(ExpectedConditions.urlContains(expectedUrl));
        SeleniumUtil.pause(NORM);
    }

    private void createDishes(String temp) {
        navigate("nav003", "menu", temp);
        SeleniumUtil.selectOptionByBySelectText(driver, 1, "Kateqoriya seçin", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Yemək əlavə et", NORM);
        SeleniumUtil.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        SeleniumUtil.checkCheckbox(driver, "dsh001", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh002", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh003", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh004", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Add Selected Dishes (", NORM);
        pause(1500);
        SeleniumUtil.selectOptionByBySelectText(driver, 2, "Kateqoriya seçin", temp);
        markTime(PHASE_CREATE_DISHES);
    }

    private void createUser(String temp) {
        navigate("nav006", "users", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "İstifadəçi əlavə et", NORM);
        SeleniumUtil.typeIntoInputById(driver, "Olivia Scott User", "user-name", NORM);
        SeleniumUtil.typeIntoInputById(driver, "OliviaScottUser@qaz.az", "user-username", NORM);
        SeleniumUtil.typeIntoInputById(driver, "qqqq1111", "user-password", NORM);
        SeleniumUtil.findButtonByTextAndClick(driver, "İstifadəçi əlavə et", NORM);

    }

    @BeforeEach
    // NOTE: load properties from create-eatery-application-test.properties
    public void setUp() throws IOException {
        properties.load(new FileReader("src/test/resources/create-eatery-application-test.properties"));

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

        SeleniumUtil.findButtonByTextAndClick(driver, "Redaktə et", NORM);
        WebElement el = driver.findElement(By.id("115"));
        SeleniumUtil.typeIntoInput(driver, el, "68 Üzeyir Hacıbəyov, Bakı", temp);
        WebElement phone = driver.findElement(By.id("116"));
        SeleniumUtil.typeIntoInput(driver, phone, "50 123 4578", temp);
        WebElement lat = driver.findElement(By.id("118"));
        SeleniumUtil.typeIntoInput(driver, lat, "40.12345", temp);
        WebElement lang = driver.findElement(By.id("119"));
        SeleniumUtil.typeIntoInput(driver, lang, "49.9876", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Yadda saxla", NORM);
    }

}