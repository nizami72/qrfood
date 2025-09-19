package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.SeleniumUtil.FINAL_PAUSE;
import static az.qrfood.backend.selenium.SeleniumUtil.NORM;
import static az.qrfood.backend.selenium.SeleniumUtil.pause;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Log4j2
public class DishTest {

    //<editor-fold desc="Fields">
    private WebDriver driver;
    private WebDriverWait wait;
    private String testName;
    private String testEmail;
    private String testPassword;
    private String testRestaurantName;
    private String host;
    Properties properties = new Properties();
    //</editor-fold>

    @BeforeEach
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

        // Move and resize the window to the desired monitor/position
        driver.manage().window().setPosition(new Point(1900, -10));
        driver.manage().window().setSize(new Dimension(1960, 1380));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(200));
    }

    @Test
    public void createDishes() {
        EateryBuilder.openPage(driver, host, "login" , NORM);
        EateryBuilder.login(driver, wait, testEmail, testPassword, testRestaurantName, NORM);
        EateryBuilder.createDishes(driver, wait, NORM);
        pause(NORM, FINAL_PAUSE);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}