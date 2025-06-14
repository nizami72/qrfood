package az.qrfood.backend.ui;

import az.qrfood.backend.util.FakeData;
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

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium test class for testing the admin registration page functionality.
 */
public class AdminRegisterPageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    
    // Test data
    private String testName;
    private String testEmail;
    private String testPassword;
    private String testRestaurantName;
    private int pause = 500;



    @BeforeEach
    public void setUp() {
        // Set up Chrome driver with headless option
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Generate unique test data

        testName = FakeData.user(4);
        testEmail = FakeData.mail(testName);
        testPassword = "qqqq1111";
        testRestaurantName = "Test " + testName + "'s eatery";
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAdminRegistrationAndLogin() throws InterruptedException {
        // 1. Open the admin register page
        driver.get("http://192.168.1.76:5173/admin/register");
        
        // Wait for page to load
        Thread.sleep(pause);
        
        // 2. Enter registration details
        // Enter name
        WebElement nameInput = driver.findElement(By.id("106"));
        nameInput.clear();
        nameInput.sendKeys(testName);
        
        // Enter email
        WebElement emailInput = driver.findElement(By.id("107"));
        emailInput.clear();
        emailInput.sendKeys(testEmail);

        Thread.sleep(pause);
        // Enter password
        WebElement passwordInput = driver.findElement(By.id("108"));
        passwordInput.clear();
        passwordInput.sendKeys(testPassword);

        Thread.sleep(pause);
        // Confirm password
        WebElement confirmPasswordInput = driver.findElement(By.id("109"));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(testPassword);

        Thread.sleep(pause);
        // Enter restaurant name
        WebElement restaurantNameInput = driver.findElement(By.id("110"));
        restaurantNameInput.clear();
        restaurantNameInput.sendKeys(testRestaurantName);

        Thread.sleep(pause);
        // 3. Click the Register button
        WebElement registerButton = driver.findElement(By.id("111"));
        registerButton.click();

        Thread.sleep(pause);
        // 4. Wait for and accept the alert
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();

        Thread.sleep(pause);
        // 5. Verify redirection to login page
        wait.until(ExpectedConditions.urlContains("/admin/login"));
        assertTrue(driver.getCurrentUrl().contains("/admin/login"), 
                "URL should contain '/admin/login' after successful registration");

        Thread.sleep(pause);
        // 6. Login with the registered credentials
        // Wait for login page to load
        Thread.sleep(2000);

        Thread.sleep(pause);
        // Enter email
        WebElement loginEmailInput = driver.findElement(By.id("101"));
        loginEmailInput.clear();
        loginEmailInput.sendKeys(testEmail);

        Thread.sleep(pause);
        // Enter password
        WebElement loginPasswordInput = driver.findElement(By.id("102"));
        loginPasswordInput.clear();
        loginPasswordInput.sendKeys(testPassword);

        Thread.sleep(pause);
        // Click login button
        WebElement loginButton = driver.findElement(By.id("103"));
        loginButton.click();

        Thread.sleep(pause);
        // 7. Verify redirection to restaurant page
        wait.until(ExpectedConditions.urlContains("/admin/restaurant"));
        assertTrue(driver.getCurrentUrl().contains("/admin/restaurant"), 
                "URL should contain '/admin/restaurant' after successful login");

        Thread.sleep(pause);
        // 8. Verify that there is at least one h2 element with the restaurant name
        List<WebElement> restaurantHeaders = driver.findElements(By.tagName("h2"));

        Thread.sleep(pause);
        // Ensure there is at least one restaurant header
        assertTrue(restaurantHeaders.size() >= 1, 
                "There should be at least 1 restaurant header, but found " + restaurantHeaders.size());

        Thread.sleep(pause);
        // Check for the restaurant name
        boolean foundRestaurant = false;
        for (WebElement header : restaurantHeaders) {
            String headerText = header.getText();
            if (headerText.equals(testRestaurantName)) {
                foundRestaurant = true;
                break;
            }
        }
        
        assertTrue(foundRestaurant, "Restaurant '" + testRestaurantName + "' should be present");
    }
}