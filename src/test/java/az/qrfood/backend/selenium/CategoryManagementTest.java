package az.qrfood.backend.selenium;

import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.util.TestUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class CategoryManagementTest {

    //<editor-fold desc="Fields">
    private final TestConfig config = ConfigFactory.create(TestConfig.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private Testov testov;
    private String howFast;
    private String host;
    private StaffItem admin;

    // --- Test Data ---
    private final String uniqueId = String.valueOf(System.currentTimeMillis());
    private final String categoryNameAz = "Test Kateqoriyası " + uniqueId;
    private final String categoryNameEn = "Test Category " + uniqueId;
    private final String categoryNameRu = "Тестовая Категория " + uniqueId;
    private final String updatedCategoryNameAz = "Yenilənmiş Kateqoriya " + uniqueId;

    // --- Locators ---
    private final By addNewCategoryButton = By.id("add-category-button");
    private final By nameAzInput = By.id("category-name-az");
    private final By nameEnInput = By.id("category-name-en");
    private final By nameRuInput = By.id("category-name-ru");
    private final By imageUploadInput = By.id("category-image-upload");
    private final By saveButton = By.xpath("//form//button[contains(text(), 'Yadda saxla')]");
    //</editor-fold>

    @BeforeEach
    public void setUp() throws IOException {
        // Read configuration from environment variables
        String fileWithData = System.getenv("JSON_SOURCE");
        howFast = System.getenv("HOW_FAST");
        host = System.getenv("HOST");

        // Load test data from JSON
        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(fileWithData), Testov.class);
        Assertions.assertNotNull(testov);
        admin = testov.getStaff().stream()
                .filter(s -> s.getRoles().contains("EATERY_ADMIN"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Admin staff member not found in test data."));

        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Set window size and position if needed
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1960, 1380));

        // Login before each test
        loginToAdmin();
    }

    /**
     * Main test method that orchestrates the entire CRUD flow.
     * This is the single entry point for the test execution.
     */
    @Test
    public void testFullCrudFlow() {
        log.info("Starting test: Full CRUD Flow. Speed: {}", howFast);
        createCategory();
        readCategory();
        updateCategory();
        deleteCategory();
        log.info("Finished test: Full CRUD Flow.");
    }

    private void createCategory() {
        log.info("Step 1: Creating category '{}'", categoryNameAz);

        wait.until(ExpectedConditions.elementToBeClickable(addNewCategoryButton)).click();
        visualPause(howFast); // Pause to see the empty form

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(nameAzInput));
        SeleniumUtil.typeIntoInput(driver, element, categoryNameAz, howFast);

        element = driver.findElement(nameEnInput);
        SeleniumUtil.typeIntoInput(driver, element, categoryNameEn, howFast);

        element = driver.findElement(nameRuInput);
        SeleniumUtil.typeIntoInput(driver, element, categoryNameRu, howFast);

        File tempFile = createDummyImageFile();

        driver.findElement(imageUploadInput).sendKeys(tempFile.getAbsolutePath());
        tempFile.deleteOnExit();
        visualPause(howFast); // Pause to see the filled form

        driver.findElement(saveButton).click();

        WebElement newCategory = findCategoryCardByText(categoryNameAz);
        Assertions.assertTrue(newCategory.isDisplayed(), "New category was not found after creation.");
        log.info("Step 1: Successfully created category.");
        visualPause(howFast); // Pause to see the new category in the list
    }

    private void readCategory() {
        log.info("Step 2: Reading category '{}'", categoryNameAz);
        WebElement createdCategory = findCategoryCardByText(categoryNameAz);
        Assertions.assertTrue(createdCategory.isDisplayed(), "Created category could not be read from the list.");
        log.info("Step 2: Successfully read category.");
        visualPause(howFast); // Pause to show the category that was "read"
    }

    private void updateCategory() {
        log.info("Step 3: Updating category to '{}'", updatedCategoryNameAz);

        WebElement categoryCard = findCategoryCardByText(categoryNameAz);
        WebElement editButton = categoryCard.findElement(By.xpath(".//button[contains(text(), 'Kateqoriyanı redaktə et')]"));
        editButton.click();
        visualPause(howFast); // Pause to see the form with existing data

        WebElement nameAzField = wait.until(ExpectedConditions.visibilityOfElementLocated(nameAzInput));
        wait.until(ExpectedConditions.attributeToBeNotEmpty(nameAzField, "value"));
        nameAzField.clear();
        SeleniumUtil.typeIntoInput(driver, nameAzField, updatedCategoryNameAz, howFast);
        visualPause(howFast); // Pause to see the updated text in the form

        driver.findElement(saveButton).click();

        WebElement updatedCategory = findCategoryCardByText(updatedCategoryNameAz);
        Assertions.assertTrue(updatedCategory.isDisplayed(), "Updated category was not found.");
        Assertions.assertTrue(isCategoryNotPresent(categoryNameAz), "Old category name is still present after update.");
        log.info("Step 3: Successfully updated category.");
        visualPause(howFast); // Pause to see the updated category in the list
    }

    private void deleteCategory() {
        log.info("Step 4: Deleting category '{}'", updatedCategoryNameAz);

        WebElement categoryCard = findCategoryCardByText(updatedCategoryNameAz);
        WebElement deleteButton = categoryCard.findElement(By.xpath(".//button[contains(text(), 'Sil')]"));
        deleteButton.click();
        visualPause(howFast); // Pause before accepting the alert

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException | TimeoutException e) {
            log.warn("Standard confirmation alert did not appear.");
        }

        Assertions.assertTrue(isCategoryNotPresent(updatedCategoryNameAz), "Category was not deleted successfully.");
        log.info("Step 4: Successfully deleted category.");
        SeleniumUtil.pause(5000);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- Helper Methods ---
    /**
     * Pauses the test execution for 2 seconds if the 'howFast' variable is set to "SLOW".
     */
    private void visualPause(String howFast) {
        SeleniumUtil.pause(howFast);
    }

    private void loginToAdmin() {
        EateryBuilder.openPage(driver, host, "login", howFast);
        EateryBuilder.login(driver, wait, admin.getEmail(), admin.getPassword(), testov.getEatery().getName(), howFast);
        EateryBuilder.navigate(driver, wait, "nav002", "/admin/categories", howFast);
    }

    private WebElement findCategoryCardByText(String categoryText) {
        String xpath = String.format("//p[contains(., '%s')]/ancestor::div[contains(@class, 'card')]", categoryText);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    private boolean isCategoryNotPresent(String categoryText) {
        try {
            String xpath = String.format("//p[contains(., '%s')]/ancestor::div[contains(@class, 'card')]", categoryText);
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        } catch (TimeoutException e) {
            return false;
        }
    }

    private File createDummyImageFile() {
        try {
            int width = 300;
            int height = 300;
            String text = "Placeholder";

            // 1. Создание изображения в памяти
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // Улучшение качества рендеринга текста
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 2. Отрисовка контента
            // Заливка фона белым цветом
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Настройка шрифта и цвета для текста
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 40));

            // Центрирование текста
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(text)) / 2;
            int y = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);

            g2d.drawString(text, x, y);

            // Освобождение ресурсов
            g2d.dispose();

            // 3. Создание временного файла
            // Это компромисс: метод должен вернуть File, но файл не должен быть постоянным.
            // Временный файл - идеальное решение.
            File tempFile = File.createTempFile("placeholder_", ".webp");

            // Гарантирует, что файл будет удален после завершения работы приложения
            tempFile.deleteOnExit();

            // 4. Запись BufferedImage в временный файл в формате WebP
            // Для этой строки требуется подключенная библиотека для поддержки WebP
            boolean success = ImageIO.write(bufferedImage, "webp", tempFile);

            if (!success) {
                throw new IOException("Не удалось найти подходящий writer для формата 'webp'. Убедитесь, что библиотека добавлена в проект.");
            }

            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
