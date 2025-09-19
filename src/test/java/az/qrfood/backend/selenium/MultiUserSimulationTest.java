package az.qrfood.backend.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Testov;
import az.qrfood.backend.util.ApiUtils;
import az.qrfood.backend.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Use PER_CLASS lifecycle to run @BeforeAll once for all tests in this class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultiUserSimulationTest {

    //<editor-fold desc="Fields">
    private final TestConfig config = ConfigFactory.create(TestConfig.class);
    private String host;
    private String howFast;
    String fileWithData;
    static private Testov testov;

    private String loginUrl;
    private String eateriesByAdminUrl;

    private String superAdminMail = "nizami.budagov@gmail.com";
    private String superAdminPass = "qqqq1111";

    private StaffItem admin;
    private List<StaffItem> waiterStaff;
    private static List<String> menuUrls;
    private String waiterUrl;
    private String feLoginUrl;
    private static final int WAITER_COUNT = 2;
    private static final String CSV_FILE = "simulation_results.csv";
    private static AtomicInteger windowIndex = new AtomicInteger(0);
    List<WebDriver> webDrivers = new ArrayList<>();
    private static final List<String> MOBILE_DEVICES = Arrays.asList(
            "Pixel 5",
            "iPhone 12 Pro",
            "Samsung Galaxy S20 Ultra",
            "iPad Mini"
    );
    //</editor-fold>

    @BeforeAll
    void setupAll() throws JsonProcessingException {
        fileWithData = config.fileWithData();
        testov = TestUtil.json2Pojo(TestUtil.readFileFromResources(fileWithData), Testov.class);
        Assertions.assertNotNull(testov);
        admin = testov.getStaff()
                .stream()
                .filter(s -> s.getRoles().contains("EATERY_ADMIN"))
                .findFirst()
                .orElseThrow();

        host = config.host();
        howFast = config.howFast();
        eateriesByAdminUrl = config.eateryAdminEateries();
        loginUrl = config.loginUrl();
        waiterUrl = config.feOrdersUrl();
        feLoginUrl = config.feLoginUrl();

        menuUrls = menuUrls();

        // Sets up the ChromeDriver binary once for all tests
        WebDriverManager.chromedriver().setup();
        Assertions.assertNotNull(testov);
        waiterStaff = testov.getStaff().stream()
                .filter(staffItem -> staffItem.getRoles().contains("WAITER"))
                .toList();
        Assertions.assertFalse(waiterStaff.isEmpty());
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        Thread.sleep(120000);
        for (WebDriver driver : webDrivers) {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Test
    @DisplayName("Simulates multiple clients and multiple waiters processing assigned tables end-to-end")
    void runMultiUserSimulation() throws IOException, InterruptedException {
        // Using try-with-resources to ensure the writer is closed automatically
        try (FileWriter csvWriter = new FileWriter(CSV_FILE)) {
            csvWriter.write("timestamp,userType,url,result,responseTimeMs\n");

            // Create a thread pool with enough threads for all concurrent users
            ExecutorService executor = Executors.newFixedThreadPool(menuUrls.size() + 1);

            // Submit client simulation tasks
            for (String url : menuUrls) {
                executor.submit(() -> simulateClient(url, csvWriter));
            }

            // Submit waiter simulation tasks for different table ranges
            for (int w = 0; w < WAITER_COUNT; w++) {
                final int waiterIndex = w;
                executor.submit(() -> simulateWaiter(waiterIndex, csvWriter));
            }

            // IMPORTANT: Shut down the executor and wait for tasks to complete
            executor.shutdown();
            // Wait for a reasonable time for all tasks to finish.
            // This prevents the test from ending prematurely.
            boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);

            // Assert that the simulation completed within the timeout
            assertTrue(finished, "The simulation timed out and did not complete within 5 minutes.");
        }
    }

    private void simulateClient(String url, FileWriter csvWriter) {
        WebDriver driver = EateryBuilder.createDriver(webDrivers, windowIndex);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        long startTime = System.currentTimeMillis();

        try {
            driver.get(url);
            chooseCategory(driver);
            chooseDish(driver, howFast);
            SeleniumUtil.findElementByTextAndClick(driver, "span", "Cart (", howFast);
            SeleniumUtil.findButtonByTextAndClick(driver, "Sifariş vermək", howFast);
            long duration = System.currentTimeMillis() - startTime;
            logResult(csvWriter, "CLIENT", url, "SUCCESS", duration);
            System.out.println("✅ Client finished order at " + url + " in " + duration + " ms");
        } catch (Exception e) {
            logResult(csvWriter, "CLIENT", url, "FAIL: " + e.getMessage(), -1);
            System.err.println("❌ Client error at " + url + ": " + e.getMessage());
        } finally {
//            driver.quit();
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
            SeleniumUtil.findByCssSelectorAndClick(driver, "button[aria-label='Toggle menu']");
            EateryBuilder.navigate(driver, wait, "nav005", "/admin/orders", howFast);

            // Determine waiter table range
            int[] range = computeTableRangeForWaiter(waiterIndex);

            // Try to find an order belonging to this waiter (by table number text), fallback to first
            List<WebElement> orders = wait.until(
                    ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".order-card"), 0)
            );
            WebElement target = null;
            for (WebElement card : orders) {
                String text = card.getText();
                // Try to extract table number from card text like "Table 45" or "Masa 45"
                int found = extractAnyNumber(text);
                if (range[0] == -1 || (found >= range[0] && found <= range[1])) {
                    target = card;
                    break;
                }
            }
            if (target == null) target = orders.get(0);
            target.click();

            // Progress order through statuses if such buttons exist
            clickIfPresent(wait, By.cssSelector(".mark-accepted"));
            clickIfPresent(wait, By.cssSelector(".mark-preparing"));
            clickIfPresent(wait, By.cssSelector(".mark-ready"));
            clickIfPresent(wait, By.cssSelector(".mark-served"));

            long duration = System.currentTimeMillis() - startTime;
            logResult(csvWriter, "WAITER-" + waiterIndex + "[" + range[0] + "-" + range[1] + "]", waiterUrl, "SUCCESS", duration);
            System.out.println("👨‍🍳 Waiter " + waiterIndex + " updated order status in " + duration + " ms");

        } catch (Exception e) {
            logResult(csvWriter, "WAITER", waiterUrl, "FAIL: " + e.getMessage(), -1);
            System.err.println("❌ Waiter error: " + e.getMessage());
        } finally {
//            driver.quit();
        }
    }

       private int extractAnyNumber(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
            else if (sb.length() > 0) break;
        }
        try {
            return sb.length() > 0 ? Integer.parseInt(sb.toString()) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void clickIfPresent(WebDriverWait wait, By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
        } catch (Exception ignore) {
            // silently ignore if not present
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

    private static int parseTableId(String clientUrl) {
        try {
            // expects .../table/{id}/...
            String[] parts = clientUrl.split("/table/");
            if (parts.length < 2) return -1;
            String tail = parts[1];
            String idStr = tail.split("/")[0];
            return Integer.parseInt(idStr);
        } catch (Exception e) {
            return -1;
        }
    }

    private static int[] computeTableRangeForWaiter(int waiterIndex) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (String url : menuUrls) {
            int t = parseTableId(url);
            if (t > 0) {
                min = Math.min(min, t);
                max = Math.max(max, t);
            }
        }
        if (min == Integer.MAX_VALUE) return new int[]{-1, -1};
        int total = (max - min + 1);
        int slice = Math.max(1, (int) Math.ceil(total / (double) WAITER_COUNT));
        int start = min + waiterIndex * slice;
        int end = Math.min(max, start + slice - 1);
        return new int[]{start, end};
    }

    private static void chooseCategory(WebDriver driver) {
        // 1. Find all the buttons inside the specific div
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By categoryButtonsLocator = By.cssSelector("div.flex.space-x-2 button");
        List<WebElement> categoryButtons = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(categoryButtonsLocator, 0)
        );
// 2. Check if the list is not empty to avoid errors
        if (!categoryButtons.isEmpty()) {
            // 3. Create a Random object
            Random random = new Random();

            // 4. Get a random index from 0 to the number of buttons - 1
            int randomIndex = random.nextInt(categoryButtons.size());

            // 5. Select the button at the random index
            WebElement randomButton = categoryButtons.get(randomIndex);

            // Optional: Print the name of the button you're about to click
            System.out.println("Clicking on random category: " + randomButton.getText());

            // 6. Click the randomly selected button
            randomButton.click();
        } else {
            System.out.println("No category buttons were found on the page.");
        }
    }

    private static void chooseDish(WebDriver driver, String howFast) {
        SeleniumUtil.findButtonByTextAndClick(driver, "+", howFast);
    }

    private List<String> menuUrls() {
        String jwt = ApiUtils.login(superAdminMail, superAdminPass, null, host, loginUrl);

        Long id = TestTestovCreator.getEateryId(jwt, testov.getEatery().getName(), host,
                Utils.replacePlaceHolders(eateriesByAdminUrl, admin.getEmail()));

        Assertions.assertNotNull(id);
        String u = config.qrContentUrls().replace("{eateryId}", id.toString());
        Response res = ApiUtils.sendGetRequest(host, jwt, u);
        if(res.getStatusCode() == 200) {
            return res.as(List.class);
        }
        throw new RuntimeException("Failed to get menu urls");
    }




}
