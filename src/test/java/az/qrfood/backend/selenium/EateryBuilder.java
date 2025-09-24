package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.SeleniumUtil.NORM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Table;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class EateryBuilder {

    static int buttonIndex = 0;

    static Iterator<Integer> idx = List.of(1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3).iterator();

    public static void registerEateryAdmin(
            WebDriver driver, WebDriverWait wait,
            String userName, String userPass,
            String userMail, String eateryName, String temp) {

        // 2. Enter registration details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        WebElement nameInput = driver.findElement(By.id("name"));
        SeleniumUtil.typeIntoInput(driver, nameInput, userName, temp);

        WebElement emailInput = driver.findElement(By.id("email"));
        SeleniumUtil.typeIntoInput(driver, emailInput, userMail, temp);

        WebElement passwordInput = driver.findElement(By.id("password"));
        SeleniumUtil.typeIntoInput(driver, passwordInput, userPass, temp);

        WebElement confirmPasswordInput = driver.findElement(By.id("confirmPassword"));
        SeleniumUtil.typeIntoInput(driver, confirmPasswordInput, userPass, temp);

        WebElement restaurantNameInput = driver.findElement(By.id("restaurantName"));
        SeleniumUtil.typeIntoInput(driver, restaurantNameInput, eateryName, temp);

        // 3. Click the Register button
        SeleniumUtil.findButtonByTextAndClick(driver, "Qeydiyyatdan keçin", temp);

        // 4. Wait for and accept the alert
        wait.until(ExpectedConditions.urlContains("/admin/login"));
        // 1. Verify redirection to login page
        assertTrue(driver.getCurrentUrl().contains("/admin/login"),
                "URL should contain '/admin/login' after successful registration");

    }

    public static void createCategories(WebDriver driver, WebDriverWait wait, String temp) {
        navigate(driver, wait, "nav002", "/admin/categories", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Kateqoriya əlavə et", NORM);
        SeleniumUtil.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        SeleniumUtil.checkCheckbox(driver, "ch003", temp);
        SeleniumUtil.checkCheckbox(driver, "ch004", temp);
        SeleniumUtil.checkCheckbox(driver, "ch005", temp);
        SeleniumUtil.checkCheckbox(driver, "ch006", temp);
        SeleniumUtil.checkCheckbox(driver, "ch007", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Seçilmiş kateqoriyaları yarat", NORM);
    }

    public static void createCategories(WebDriver driver, WebDriverWait wait, String temp, List<String> categoryNames) {
        navigate(driver, wait, "nav002", "/admin/categories", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Kateqoriya əlavə et", NORM);
        SeleniumUtil.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        for (String cat : categoryNames) {
            SeleniumUtil.checkCheckboxByWord(driver, cat, temp);
        }
        SeleniumUtil.findButtonByTextAndClick(driver, "Seçilmiş kateqoriyaları yarat", NORM);
    }

    public static void navigate(WebDriver driver, WebDriverWait wait, String id, String expectedUrl, String temp) {
        WebElement nameInput = driver.findElement(By.id(id));
        SeleniumUtil.click(driver, nameInput, temp);
        wait.until(ExpectedConditions.urlContains(expectedUrl));
        SeleniumUtil.pause(NORM);
    }

    public static boolean login(WebDriver driver, WebDriverWait wait,
                                String testEmail, String testPassword,
                                String testRestaurantName, String temp) {
        try {
            // 1. Wait for the login form
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));

            WebElement loginEmailInput = driver.findElement(By.id("101"));
            SeleniumUtil.typeIntoInput(driver, loginEmailInput, testEmail, temp);

            WebElement loginPasswordInput = driver.findElement(By.id("102"));
            SeleniumUtil.typeIntoInput(driver, loginPasswordInput, testPassword, temp);

            WebElement loginButton = driver.findElement(By.id("103"));
            SeleniumUtil.click(driver, loginButton, temp);

            // 2. Check if login succeeded by checking URL
            boolean loggedIn;
            try {
                wait.until(ExpectedConditions.urlContains("/admin/restaurants"));
                loggedIn = driver.getCurrentUrl().contains("/admin/restaurants");
            } catch (TimeoutException e) {
                System.out.println("Login failed: URL did not change to /admin/restaurants");
                return false; // Exit method silently if login failed
            }

            if (!loggedIn) {
                System.out.println("Login failed: Not redirected to /admin/restaurants");
                return false;
            }

            // 3. Verify restaurant header
            String actualSelectedEatery = SeleniumUtil.findSelectedOptionTextById(driver, "select-restaurant-desktop");
            if(actualSelectedEatery == null || actualSelectedEatery.length() < 2) {
                actualSelectedEatery = SeleniumUtil.findSelectedOptionTextById(driver, "select-restaurant-mobile");
            }
            assertTrue(actualSelectedEatery != null && actualSelectedEatery.length() > 2, "Actual eatery name is empty or null");
            assertEquals(actualSelectedEatery, testRestaurantName, "Actual eatery name is empty or different");
        } catch (NoSuchElementException | TimeoutException e) {
            log.debug("Login process encountered a problem [{}] ", e.getMessage());
        }
        return true;

    }

    public static void openPage(WebDriver driver, String host, String path, String pause) {
        String url = host + "/admin/" + path;
        log.debug("Try to open page [{}]", url);
        driver.get(url);
//        driver.manage().window().maximize();
    }

    public static void createDishes(WebDriver driver, WebDriverWait wait, String temp) {
        navigate(driver, wait, "nav003", "menu", temp);
        SeleniumUtil.selectOptionWithText(driver, 1, "Kateqoriya seçin", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Yemək əlavə et", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh001", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh002", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh003", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh004", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Add Selected Dishes (", temp);
    }

    public static void createDishes(WebDriver driver, WebDriverWait wait, String temp, List<String> diches) {
        SeleniumUtil.findButtonByTextAndClick(driver, "Yemək əlavə et", NORM);
        SeleniumUtil.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        for (String cat : diches) {
            SeleniumUtil.checkCheckboxByWordDish(driver, cat, temp);
        }
        SeleniumUtil.findButtonByIdAndClick(driver, "predef-add-selected", NORM);
    }

    public static void editEatery(WebDriver driver, String temp) {
        SeleniumUtil.findButtonByTextAndClick(driver, "Redaktə et", NORM);
        WebElement el = driver.findElement(By.id("115"));
        SeleniumUtil.typeIntoInput(driver, el, "68 Üzeyir Hacıbəyov, Bakı", temp);
        WebElement phone = driver.findElement(By.id("116"));
        SeleniumUtil.typeIntoInput(driver, phone, "50 123 4578", temp);
        WebElement lat = driver.findElement(By.id("118"));
        SeleniumUtil.typeIntoInput(driver, lat, "40.12345", temp);
        WebElement lang = driver.findElement(By.id("119"));
        SeleniumUtil.typeIntoInput(driver, lang, "49.9876", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Yadda saxla", NORM);
    }

    public static void createUser(WebDriver driver, WebDriverWait wait, StaffItem stuffItem, String temp) {

        String id;
        if (stuffItem.getRoles().contains("KITCHEN_ADMIN")) {
            id = "role-KITCHEN_ADMIN";
        } else if (stuffItem.getRoles().contains("CASHIER")) {
            id = "role-CASHIER";
        } else if (stuffItem.getRoles().contains("WAITER")) {
            id = "role-WAITER";
        } else return;

        navigate(driver, wait, "nav006", "users", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "İstifadəçi əlavə et", temp);
        SeleniumUtil.typeIntoInputById(driver, stuffItem.getProfile().getName(), "user-name", temp);
        SeleniumUtil.typeIntoInputById(driver, stuffItem.getEmail(), "user-username", temp);
        SeleniumUtil.typeIntoInputById(driver, stuffItem.getProfile().getPhone(), "user-phone", temp);
        SeleniumUtil.typeIntoInputById(driver, stuffItem.getPassword(), "user-password", temp);
        SeleniumUtil.checkCheckbox(driver, id, temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "İstifadəçi əlavə et", temp);
    }

    public static void createTables(WebDriver driver, WebDriverWait wait, List<Table> tableItems, String norm) {
        for (Table tableItem : tableItems) {
            navigate(driver, wait, "nav004", "/admin/tables", norm);
            SeleniumUtil.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
            SeleniumUtil.typeIntoInputById(driver, tableItem.getNumber(), "tblcrrt01", norm);
            SeleniumUtil.typeIntoInputById(driver, tableItem.getSeats(), "tblcrrt02", norm);
            SeleniumUtil.typeIntoInputById(driver, tableItem.getNote(), "tblcrrt03", norm);
            SeleniumUtil.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
            SeleniumUtil.pause(norm);
            log.debug("Table created [{}]", tableItem);
            assignTableWaiter(driver, wait, tableItem, idx.next(), norm);
        }
    }

    public static void assignTableWaiter(WebDriver driver, WebDriverWait wait, Table tableItem,
                                         int selectIdx, String norm) {
//        driver.navigate().refresh();
        SeleniumUtil.pause(norm);
        SeleniumUtil.pause(norm);
        SeleniumUtil.findButtonsByTextAndClick(driver, "Ofisiant təyin et", buttonIndex++, norm);
        SeleniumUtil.selectRandomOptionByText(driver, "Ofisiant seçin", norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Təyin et", norm);
        SeleniumUtil.pause(norm);
        log.debug("Waiter assigned");
    }

    public static void printQrCode(WebDriver driver, WebDriverWait wait, String norm) {
        navigate(driver, wait, "nav004", "/admin/tables", norm);
        WebElement qrCode = driver.findElement(By.cssSelector("[id^='qr-card']"));
        SeleniumUtil.highlight2(driver, qrCode, norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Çap et", norm);
        SeleniumUtil.pause(norm);
    }

    public static WebDriver createDriver(List<WebDriver> webDrivers, AtomicInteger windowIndex) {
        // --- START: Manual Device Definition ---

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        // 1. Create a map for the device metrics
        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 393);
        deviceMetrics.put("height", 851);
        deviceMetrics.put("pixelRatio", 2.75);
        // Add the preferences to disable the password manager
        deviceMetrics.put("credentials_enable_service", false);
        deviceMetrics.put("profile.password_manager_enabled", false);

        // 2. Create a map for the mobile emulation options and add the metrics
        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (Linux; Android 12; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Mobile Safari/537.36");

        // --- END: Manual Device Definition ---

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation); // Use the manual definition
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        // Disable the "Chrome is being controlled..." message
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        options.setExperimentalOption("prefs", prefs);


        WebDriver driver = new ChromeDriver(options);

        driver.manage().window().setSize(new Dimension(393, 1000));
        // Position the new window so it doesn't overlap
        int xPosition = 0 + windowIndex.getAndIncrement() * 520; // 393px width + ~17px scrollbar/border
        driver.manage().window().setPosition(new Point(xPosition, 0));
        webDrivers.add(driver);
        return driver;
    }

    /**
     * Creates a new ChromeDriver instance and positions it in a
     * specific quadrant of the screen.
     *
     * @return A configured WebDriver instance.
     */
    public WebDriver createDriver1(AtomicInteger windowIndex) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        // Remove "--headless=new" if you want to see the windows

        WebDriver driver = new ChromeDriver(options);

        // Define screen and window dimensions
        final int screenWidth = 1900;
        final int screenHeight = 1080;
        final int windowWidth = screenWidth / 2;
        final int windowHeight = screenHeight / 2;

        // First, set the size of the new window
        driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));

        // Determine the position based on the static index
        Point position;
        int quadrant = windowIndex.getAndIncrement() % 4; // Use modulo to cycle through 0, 1, 2, 3

        switch (quadrant) {
            case 0: // Top-Left Quadrant
                position = new Point(1920, 0);
                break;
            case 1: // Top-Right Quadrant
                position = new Point(1900 + windowWidth, 0);
                break;
            case 2: // Bottom-Left Quadrant
                position = new Point(1900, windowHeight);
                break;
            case 3: // Bottom-Right Quadrant
                position = new Point(1900 + windowWidth, windowHeight);
                break;
            default: // Fallback, should not be reached
                position = new Point(0, 0);
                break;
        }

        driver.manage().window().setPosition(position);
        return driver;
    }

}