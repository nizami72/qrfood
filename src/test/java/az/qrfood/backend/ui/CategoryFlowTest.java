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
import java.util.LinkedHashMap;
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
        options.addArguments("--window-position=" + 1920 + "," + 20);

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
        // 1. Open the login page
        driver.get("http://192.168.1.76:5173/admin/login");

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

        // 4. Switch language to EN using element with id 113
        try {
            WebElement languageSelector = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("113")));
            Select languageSelect = new Select(languageSelector);
            languageSelect.selectByValue("en");
            Thread.sleep(pause);
            System.out.println("Successfully switched language to EN");
        } catch (Exception e) {
            // If language selector is not found, log and continue with the test
            System.out.println("Language selector not found or not accessible. Continuing with test.");
        }

        // 5. Wait for admin page to load and click Category Management
        // Note: Using XPath here as there is no element ID for the Category Management button
        WebElement categoryManagementButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(), 'Category Management')]")));
        categoryManagementButton.click();

        Thread.sleep(pause);

        // 6. Click Add Category
        // Note: Using XPath here as there is no element ID for the Add Category button
        WebElement addCategoryButton = driver.findElement(By.id("126"));
        addCategoryButton.click();

        Thread.sleep(pause);

        // 7. Get category data from FakeData
        List<LinkedHashMap> categories = FakeData.categories();

        LinkedHashMap<String, Object> c = categories.get(FakeData.getRandomInt(0, categories.size()));

        // Get category names from the JSON data
        String nameAz = c.get("nameAz").toString();
        String nameEn = c.get("nameEn").toString();
        String nameRu = c.get("nameRu").toString();
        String imageName = c.get("image").toString();

        // 8. Enter category names in different languages
        // Note: Using XPath here as there are no element IDs for the input fields
        // Enter Azerbaijani name
        WebElement nameAzInput = driver.findElement(By.id("127"));
        nameAzInput.clear();
        nameAzInput.sendKeys(nameAz);

        // Enter English name
        WebElement nameEnInput = driver.findElement(By.id("128"));
        nameEnInput.clear();
        nameEnInput.sendKeys(nameEn);

        // Enter Russian name
        WebElement nameRuInput = driver.findElement(By.id("129"));
        nameRuInput.clear();
        nameRuInput.sendKeys(nameRu);

        Thread.sleep(pause);

        // 9. Upload image
        // Note: Using XPath here as there is no element ID for the file input
        WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));
        String imagePath = "/home/nizami/Dropbox/projects/Java/qrfood/src/test/resources/image/" + imageName;
        fileInput.sendKeys(imagePath);

        Thread.sleep(pause);

        // 10. Click Add Category button to submit the form
        // Note: Using XPath here as there is no element ID for the Add Category button
        WebElement submitButton = driver.findElement(
            By.xpath("//button[contains(text(), 'Add Category')]"));
        submitButton.click();

        // 11. Wait for the category to be added and verify the buttons
        Thread.sleep(2000); // Wait a bit longer for the operation to complete

        // 12. Verify that there are 3 buttons with the expected text
        // Note: Using tagName here as there are no element IDs for the buttons
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
