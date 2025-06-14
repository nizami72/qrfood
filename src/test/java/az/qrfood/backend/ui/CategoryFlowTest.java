package az.qrfood.backend.ui;
import az.qrfood.backend.category.entity.Category;
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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium test class for testing the category management flow.
 */
public class CategoryFlowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Test data
    private String testEmail;
    private String testPassword;
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

        // Set test credentials
        testEmail = "nizami.budagov@gmail.com";
        testPassword = "qqqq1111";
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testCategoryFlow() throws InterruptedException {
        // 1. Open the landing page
        driver.get("http://localhost:5173/admin/login");

        // Wait for page to load
        Thread.sleep(pause);



        // 2. Enter login credentials
        // Enter email
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));
        emailInput.clear();
        emailInput.sendKeys(testEmail);

        // Enter password
        WebElement passwordInput = driver.findElement(By.id("102"));
        passwordInput.clear();
        passwordInput.sendKeys(testPassword);

        Thread.sleep(pause);
        // 3. Click Enter button
        WebElement loginButton = driver.findElement(By.id("103"));
        loginButton.click();

        // 4. Wait for admin page to load
        Thread.sleep(pause);

        // 5. Switch language to English (EN) using element with id 113
        WebElement languageSelector = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("113")));
        Select languageSelect = new Select(languageSelector);
        languageSelect.selectByValue("en"); // Select English language
        Thread.sleep(pause);

        // 6. Click Category Management in the sidebar
        // Wait a bit longer for the admin page to fully load
        Thread.sleep(2000);

        // Look for the Categories link in the sidebar
        WebElement categoriesLink = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/categories')]")));
        categoriesLink.click();

        Thread.sleep(pause);

        // 7. Click Add Category
        WebElement addCategoryButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Add Category')]")));
        addCategoryButton.click();

        Thread.sleep(pause);

        // 6. Define category data
        // Note: We're using hardcoded values instead of FakeData.categories() because
        // the entity structure is complex with translations in a separate list
        // This is a simpler approach for the UI test
        String nameAz = "Salatlar";
        String nameEn = "Salads";
        String nameRu = "Салаты";
        String imageName = "salad.webp";

        // 7. Enter category names in different languages
        // Enter Azerbaijani name
        WebElement nameAzInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@placeholder='Category Name in Azerbaijani']")));
        nameAzInput.clear();
        nameAzInput.sendKeys(nameAz);

        Thread.sleep(pause);

        // Enter English name
        WebElement nameEnInput = driver.findElement(
            By.xpath("//input[@placeholder='Category Name in English']"));
        nameEnInput.clear();
        nameEnInput.sendKeys(nameEn);

        Thread.sleep(pause);

        // Enter Russian name
        WebElement nameRuInput = driver.findElement(
            By.xpath("//input[@placeholder='Category Name in Russian']"));
        nameRuInput.clear();
        nameRuInput.sendKeys(nameRu);

        Thread.sleep(pause);

        // 8. Upload image
        WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));
        String imagePath = "/home/nizami/Dropbox/projects/Java/qrfood/src/test/resources/image/" + imageName;
        fileInput.sendKeys(imagePath);

        Thread.sleep(pause);

        // 9. Click Add Category button to submit the form
        WebElement submitButton = driver.findElement(
            By.xpath("//button[contains(text(), 'Add Category')]"));
        submitButton.click();

        // 10. Wait for the category to be added and verify the buttons
        Thread.sleep(2000); // Wait a bit longer for the operation to complete

        // 11. Verify that there are 3 buttons with the expected text
        List<WebElement> buttons = driver.findElements(By.tagName("button"));

        boolean hasEditCategory = false;
        boolean hasEditDishes = false;
        boolean hasDelete = false;

        for (WebElement button : buttons) {
            String buttonText = button.getText();
            if (buttonText.contains("Edit Category")) {
                hasEditCategory = true;
            } else if (buttonText.contains("Edit Dishes")) {
                hasEditDishes = true;
            } else if (buttonText.contains("Delete")) {
                hasDelete = true;
            }
        }

        // Assert that all three buttons are present
        assertTrue(hasEditCategory, "Edit Category button should be present");
        assertTrue(hasEditDishes, "Edit Dishes button should be present");
        assertTrue(hasDelete, "Delete button should be present");
    }
}
