package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_CREATE_TABLES;
import static az.qrfood.backend.selenium.SeleniumUtil.PHASE_LOGIN;
import static az.qrfood.backend.selenium.SeleniumUtil.markTime;
import static az.qrfood.backend.selenium.SeleniumUtil.pause;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.util.TestUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class TestTableManagement {

    private WebDriver driver;
    private WebDriverWait wait;
    private Testov testov;
    private String howFast;
    String host;
    final static boolean visualEffect = false;
    private long totalTime;
    StaffItem admin;

    @BeforeEach
    public void setUp() throws IOException {
        host = System.getenv("HOST");
        String fileWithData = System.getenv("JSON_SOURCE");
        howFast = System.getenv("HOW_FAST");

        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(fileWithData), Testov.class);
        Assertions.assertNotNull(testov);
        admin = testov.getStaff().stream().filter(s -> s.getRoles().contains("EATERY_ADMIN")).findFirst().orElseThrow();

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

        // Move and resize a window to the desired monitor /position
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

    @Test
    public void flow() {


        // ================== LOGIN USER ==================
        EateryBuilder.openPage(driver, host, "login", howFast);
        EateryBuilder.login(driver, wait, admin.getEmail(), admin.getPassword(), testov.getEatery().getName(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_LOGIN);


        // ================== CREATE TABLES ==================
        EateryBuilder.createTables(driver, wait, testov.getTables(), howFast);
        pause(howFast);
        totalTime += markTime(PHASE_CREATE_TABLES);
        log.debug("Total time [{}]", totalTime);

    }


}