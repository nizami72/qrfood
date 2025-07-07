package az.qrfood.backend.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium test class for testing the admin registration page functionality.
 */
public class AdminRegisterTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Test data
    private String testName;
    private String testEmail;
    private String testPassword;
    private String testRestaurantName;

    @BeforeEach
    public void setUp() throws IOException {
        // Load properties from application-test.properties
        Properties properties = new Properties();
        properties.load(new FileReader("src/test/resources/application-test.properties"));
        testName = properties.getProperty("eatery.admin.name");
        testEmail = properties.getProperty("eatery.admin.email");
        testPassword = properties.getProperty("password");
        testRestaurantName = properties.getProperty("eatery.name");

        // Set up Chrome driver with headless option

        WebDriverManager.chromedriver().setup(); // 🔥 Автоматически подгрузит подходящий драйвер
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--window-position=" + 1920 + "," + 20);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(200));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAdminRegistrationAndLogin() {
        // 1. Open the admin register page
        driver.get("http://localhost:5173/admin/register");

        // 2. Enter registration details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("106")));
        WebElement nameInput = driver.findElement(By.id("106"));
        nameInput.clear();
        nameInput.sendKeys(testName);

        WebElement emailInput = driver.findElement(By.id("107"));
        emailInput.clear();
        emailInput.sendKeys(testEmail);

        WebElement passwordInput = driver.findElement(By.id("108"));
        passwordInput.clear();
        passwordInput.sendKeys(testPassword);

        WebElement confirmPasswordInput = driver.findElement(By.id("109"));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(testPassword);

        WebElement restaurantNameInput = driver.findElement(By.id("110"));
        restaurantNameInput.clear();
        restaurantNameInput.sendKeys(testRestaurantName);

        // 3. Click the Register button
        WebElement registerButton = driver.findElement(By.id("111"));
        registerButton.click();

        // 4. Wait for and accept the alert
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();

        // 5. Verify redirection to login page
        wait.until(ExpectedConditions.urlContains("/admin/login"));
        assertTrue(driver.getCurrentUrl().contains("/admin/login"),
                "URL should contain '/admin/login' after successful registration");

        // 6. Login with the registered credentials
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));
        WebElement loginEmailInput = driver.findElement(By.id("101"));
        loginEmailInput.clear();
        loginEmailInput.sendKeys(testEmail);

        WebElement loginPasswordInput = driver.findElement(By.id("102"));
        loginPasswordInput.clear();
        loginPasswordInput.sendKeys(testPassword);

        WebElement loginButton = driver.findElement(By.id("103"));
        loginButton.click();

        // 7. Verify redirection to restaurant page
        wait.until(ExpectedConditions.urlContains("/admin/restaurant"));
        assertTrue(driver.getCurrentUrl().contains("/admin/restaurant"),
                "URL should contain '/admin/restaurant' after successful login");

        // 8. Verify that there is at least one h2 element with the restaurant name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        List<WebElement> restaurantHeaders = driver.findElements(By.tagName("h2"));

        assertTrue(restaurantHeaders.size() >= 1,
                "There should be at least 1 restaurant header, but found " + restaurantHeaders.size());

        boolean foundRestaurant = restaurantHeaders.stream()
                .anyMatch(header -> header.getText().equals(testRestaurantName));

        assertTrue(foundRestaurant, "Restaurant '" + testRestaurantName + "' should be present");
    }
}