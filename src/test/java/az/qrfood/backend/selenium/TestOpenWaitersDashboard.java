package az.qrfood.backend.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Use PER_CLASS lifecycle to run @BeforeAll once for all tests in this class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestOpenWaitersDashboard {

    //<editor-fold desc="Fields">
    private final TestConfig config = ConfigFactory.create(TestConfig.class);
    private String host;
    private String howFast;
    String fileWithData;
    static private Testov testov;
    private List<StaffItem> waiterStaff;
    private String waiterUrl;
    private String feLoginUrl;
    private static final String CSV_FILE = "simulation_results.csv";
    private static final AtomicInteger windowIndex = new AtomicInteger(0);
    List<WebDriver> webDrivers = new ArrayList<>();
    private int waiterCount;
    //</editor-fold>

    @BeforeAll
    void setupAll() throws JsonProcessingException {
        host = System.getenv("HOST");

        fileWithData = config.fileWithData();
        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(fileWithData), Testov.class);
        Assertions.assertNotNull(testov);
        howFast = config.howFast();
        waiterUrl = host + config.feOrdersUrl();
        feLoginUrl = host + config.feLoginUrl();


        // Sets up the ChromeDriver binary once for all tests
        WebDriverManager.chromedriver().setup();
        Assertions.assertNotNull(testov);
        waiterStaff = testov.getStaff().stream()
                .filter(staffItem -> staffItem.getRoles().contains("WAITER"))
                .toList();
        waiterCount = waiterStaff.size();
        Assertions.assertFalse(waiterStaff.isEmpty());
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        Thread.sleep(800000);
        for (WebDriver driver : webDrivers) {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Test
    @DisplayName("Simulates multiple clients and multiple waiters processing assigned tables end-to-end")
    void runMultiUserSimulation() throws IOException, InterruptedException {
        try (FileWriter csvWriter = new FileWriter(CSV_FILE)) {
            csvWriter.write("timestamp,userType,url,result,responseTimeMs\n");
            ExecutorService executor = Executors.newFixedThreadPool(waiterCount);
            for (int w = 0; w < waiterCount; w++) {
                final int waiterIndex = w;
                executor.submit(() -> simulateWaiter(waiterIndex, csvWriter));
            }
            executor.shutdown();
            boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);
            assertTrue(finished, "The simulation timed out and did not complete within 5 minutes.");
        }
    }

    private void simulateWaiter(int waiterIndex, FileWriter csvWriter) {

        WebDriver driver = EateryBuilder.createDriver(webDrivers, windowIndex);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Longer wait for waiter
        long startTime = System.currentTimeMillis();

        try {
            driver.get(feLoginUrl);
            EateryBuilder.openPage(driver, host, "login", howFast);
            EateryBuilder.login(driver, wait, waiterStaff.get(waiterIndex).getEmail(), waiterStaff.get(waiterIndex).getPassword(), testov.getEatery().getName(), howFast);
            SeleniumUtil.findButtonByIdAndClick(driver, "btn-mobile-menu", howFast);
            EateryBuilder.navigate(driver, wait, "nav005", "/admin/orders", howFast);

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("👨‍🍳 Waiter " + waiterIndex + " updated order status in " + duration + " ms");

        } catch (Exception e) {
            logResult(csvWriter, "WAITER", waiterUrl, "FAIL: " + e.getMessage(), -1);
            System.err.println("❌ Waiter error: " + e.getMessage());
        }
    }

    private synchronized void logResult(FileWriter writer, String userType, String url, String result, long responseTime) {
        try {
            writer.write(String.format("%s,%s,%s,%s,%d\n",
                    LocalDateTime.now(), userType, url, result.replace(",", ";"), responseTime)); // Sanitize result message
            writer.flush();
        } catch (IOException e) {
            System.err.println("⚠️ Could not write to CSV: " + e.getMessage());
        }
    }






}
