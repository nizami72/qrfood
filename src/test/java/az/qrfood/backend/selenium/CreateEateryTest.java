package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.Util.NORM;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Selenium test class for testing the admin registration page functionality.
 */
@Log4j2
public class CreateEateryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Test data
    private String testName;
    private String testEmail;
    private String testPassword;
    private String testRestaurantName;
    Properties properties = new Properties();
    private long t = System.currentTimeMillis();
    private static String PHASE_START = "Started",
            PHASE_REGISTRATION = "Registartion And Login",
            PHASE_CREATE_CATEGORY = "Create cat",
            PHASE_CREATE_DISHES = "Create dished",
            PHASE_CREATE_TABLES = "Create Tables",
            PHASE_END = "Ended";

    @Test
    public void test() {
//        startBrowser("login");
//        login(FAST);
//        navigate("nav006", "/admin/users");
//        deleteUser();

        startBrowser("register");
        testAdminRegistrationAndLogin(NORM);
        login(NORM);
        createCategories(NORM);
        createDishes(NORM);
        createTables(NORM);
        waitUntilEnterAndClose();
    }

    @Test
    public void loginAndShowOrder() {
        startBrowser("login");
        login(NORM);
        navigate("nav005", "/admin/orders");
        Util.pause(4000);
    }

    private void createTables(String norm) {
        navigate("nav004", "/admin/tables");
        Util.findButtonByTextAndClick(driver, "Masa əlavə et", NORM);
        Util.typeIntoInputById(driver, "Masa 1", "tblcrrt01", norm);
        Util.typeIntoInputById(driver, "6", "tblcrrt02", norm);
        Util.typeIntoInputById(driver, "VIP Masa", "tblcrrt03", norm);
        Util.findButtonByTextAndClick(driver, "Masa əlavə et", NORM);
        markTime(PHASE_CREATE_TABLES);
    }

    private void deleteUser() {
        Util.findButtonByTextAndClick(driver, "Sil", NORM);
        Util.alertAccept(wait, driver);
        Util.findButtonByTextAndClick(driver, "Çıxış", NORM);
    }

    private void startBrowser(String path) {
        driver.get("http://localhost:5173/admin/" + path);
        markTime(PHASE_START);
    }

    private void testAdminRegistrationAndLogin(String temp) {

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
        Util.click(driver, registerButton);

        // 4. Wait for and accept the alert
        Util.alertAccept(wait, driver);
        wait.until(ExpectedConditions.urlContains("/admin/login"));
        // 1. Verify redirection to login page
        assertTrue(driver.getCurrentUrl().contains("/admin/login"),
                "URL should contain '/admin/login' after successful registration");

    }

    private void login(String temp) {

        // 2. Login with the registered credentials
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));
        WebElement loginEmailInput = driver.findElement(By.id("101"));
        Util.typeIntoInput(driver, loginEmailInput, testEmail, temp);

        WebElement loginPasswordInput = driver.findElement(By.id("102"));
        Util.typeIntoInput(driver, loginPasswordInput, testPassword, temp);

        WebElement loginButton = driver.findElement(By.id("103"));
        Util.click(driver, loginButton);

        // 3. Verify redirection to the restaurant page
        wait.until(ExpectedConditions.urlContains("/admin/restaurants"));
        assertTrue(driver.getCurrentUrl().contains("/admin/restaurants"),
                "URL should contain '/admin/restaurants' after successful login");

        // 4. Verify that there is at least one h2 element with the restaurant name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        List<WebElement> restaurantHeaders = driver.findElements(By.tagName("h2"));

        assertTrue(restaurantHeaders.size() >= 1,
                "There should be at least 1 restaurant header, but found " + restaurantHeaders.size());

        boolean foundRestaurant = restaurantHeaders.stream()
                .anyMatch(header -> header.getText().equals(testRestaurantName));

        assertTrue(foundRestaurant, "Restaurant '" + testRestaurantName + "' should be present");
        Util.pause(NORM);

        markTime(PHASE_REGISTRATION);

    }

    private void navigate(String id, String expectedUrl) {
        // 1. Open the category page
        WebElement nameInput = driver.findElement(By.id(id));
        Util.click(driver, nameInput);
        wait.until(ExpectedConditions.urlContains(expectedUrl));
        Util.pause(NORM);
    }

    private void createCategories(String temp) {
        navigate("nav002", "/admin/categories");
        Util.findButtonByTextAndClick(driver, "Kateqoriya əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "ch003", temp);
        Util.checkCheckbox(driver, "ch004", temp);
        Util.checkCheckbox(driver, "ch005", temp);
        Util.findButtonByTextAndClick(driver, "Seçilmiş kateqoriyaları yarat", NORM);
    }

    private void createDishes(String temp) {
        navigate("nav003", "menu");
        Util.selectOptionByBySelectText(driver, 1, "Kateqoriya seçin");
        Util.findButtonByTextAndClick(driver, "Yemək əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "dsh001", temp);
        Util.checkCheckbox(driver, "dsh002", temp);
        Util.checkCheckbox(driver, "dsh003", temp);
        Util.findButtonByTextAndClick(driver, "Add Selected Dishes (", NORM);
        Util.selectOptionByBySelectText(driver, 2, "Kateqoriya seçin");
        Util.findButtonByTextAndClick(driver, "Yemək əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "dsh001", temp);
        Util.checkCheckbox(driver, "dsh002", temp);
        Util.checkCheckbox(driver, "dsh003", temp);
        Util.findButtonByTextAndClick(driver, "Add Selected Dishes (", NORM);
        markTime(PHASE_CREATE_DISHES);
    }

    private void waitUntilEnterAndClose() {
        System.out.println("Тест завершён. Нажмите Enter, чтобы закрыть браузер...");
        new Scanner(System.in).nextLine();
    }

    @BeforeEach
    public void setUp() throws IOException {
        // Load properties from application-test.properties
        properties.load(new FileReader("src/test/resources/application-test.properties"));
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

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        // Now move and resize using JavaScript or Robot
        driver.manage().window().setPosition(new Point(1900, -10));
        // Optional: Get your monitor's real resolution if scaling is enabled
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

}