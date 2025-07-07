package az.qrfood.backend.selenium;

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

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium test class for testing the login page functionality.
 */
public class LoginPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        // Set up Chrome driver with headless option
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginAndVerifyRestaurants() throws InterruptedException {
        // 1. Open the login page
        driver.get("http://localhost:5173/admin/login");

//        wait.wait(2000);
        Thread.sleep(3000);
        // 2. Enter login credentials
        WebElement emailInput = driver.findElement(By.id("101"));
        emailInput.clear();
        emailInput.sendKeys("nizami.budagov@gmail.com");

        WebElement passwordInput = driver.findElement(By.id("102"));
        passwordInput.clear();
        passwordInput.sendKeys("qqqq1111");

        // 3. Click the Enter button
        WebElement loginButton = driver.findElement(By.id("103"));
        loginButton.click();

        // 4. Wait for redirection and verify URL
        wait.until(ExpectedConditions.urlContains("/admin/restaurant"));
        assertTrue(driver.getCurrentUrl().contains("/admin/restaurant"), 
                "URL should contain '/admin/restaurant' after successful login");

        // 5. Verify that there are at least 3 h2 elements with specific restaurant names
        List<WebElement> restaurantHeaders = driver.findElements(By.tagName("h2"));

        // Ensure there are at least 3 restaurant headers
        assertTrue(restaurantHeaders.size() >= 3, 
                "There should be at least 3 restaurant headers, but found " + restaurantHeaders.size());

        // Check for specific restaurant names
        boolean foundSunriseCafe = false;
        boolean foundBakuDelights = false;
        boolean foundOldCityBistro = false;

        for (WebElement header : restaurantHeaders) {
            String headerText = header.getText();
            if (headerText.equals("Sunrise Cafe")) {
                foundSunriseCafe = true;
            } else if (headerText.equals("Baku Delights")) {
                foundBakuDelights = true;
            } else if (headerText.equals("Old City Bistro")) {
                foundOldCityBistro = true;
            }
        }

        assertTrue(foundSunriseCafe, "Restaurant 'Sunrise Cafe' should be present");
        assertTrue(foundBakuDelights, "Restaurant 'Baku Delights' should be present");
        assertTrue(foundOldCityBistro, "Restaurant 'Old City Bistro' should be present");
    }
}
