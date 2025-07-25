package az.qrfood.backend.selenium;
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
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium test class for testing the category management flow.
 */
public class CategoryFlowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Value("${}")
    private String testEmail;
    @Value("${}")
    private String testPassword;
    private int pause = 200;

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
        testEmail = "JohnKimber@qrfood.az";
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
            // If a language selector is not found, log and continue with the test
            System.out.println("Language selector not found or not accessible. Continuing with test.");
        }

        // 5. Navigate directly to the Category Management page
        driver.get("http://192.168.1.76:5173/admin/categories");

        // Wait for the page to load by checking for the Add Category button
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-category-button")));

        Thread.sleep(pause);

        // 6. Click Add Category
        // Using ID-based selector with explicit wait
        WebElement addCategoryButton = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("add-category-button")));
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
        // Using ID-based selectors
        // Enter Azerbaijani name
        WebElement nameAzInput = driver.findElement(By.id("category-name-az"));
        nameAzInput.clear();
        nameAzInput.sendKeys(nameAz);

        // Enter English name
        WebElement nameEnInput = driver.findElement(By.id("category-name-en"));
        nameEnInput.clear();
        nameEnInput.sendKeys(nameEn);

        // Enter Russian name
        WebElement nameRuInput = driver.findElement(By.id("category-name-ru"));
        nameRuInput.clear();
        nameRuInput.sendKeys(nameRu);

        Thread.sleep(pause);

        // 9. Upload image
        // Using ID-based selector
        WebElement fileInput = driver.findElement(By.id("category-image-upload"));
        String imagePath = "/home/nizami/Dropbox/projects/Java/qrfood/src/test/resources/image/" + imageName;
        fileInput.sendKeys(imagePath);

        Thread.sleep(pause);

        // 10. Click the Add Category button to submit the form
        // Using ID-based selector
        WebElement submitButton = driver.findElement(By.id("category-form-submit"));
        submitButton.click();

        // 11. Wait for the category to be added and verify the buttons
        Thread.sleep(2000); // Wait a bit longer for the operation to complete

        // 12. Verify that there are 3 buttons with the expected IDs
        // Using CSS selectors to find buttons with IDs that start with specific prefixes
        List<WebElement> editCategoryButtons = driver.findElements(By.cssSelector("button[id^='edit-category-']"));
        List<WebElement> editDishesButtons = driver.findElements(By.cssSelector("button[id^='edit-dishes-']"));
        List<WebElement> deleteButtons = driver.findElements(By.cssSelector("button[id^='delete-category-']"));

        // Assert that all three types of buttons are present
        assertTrue(!editCategoryButtons.isEmpty(), "Edit Category button should be present");
        assertTrue(!editDishesButtons.isEmpty(), "Edit Dishes button should be present");
        assertTrue(!deleteButtons.isEmpty(), "Delete button should be present");
    }
}
