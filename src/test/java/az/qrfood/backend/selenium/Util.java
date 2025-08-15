package az.qrfood.backend.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import java.util.Map;

@Log4j2
public class Util {

    public static String FAST = "FAST", NORM = "NORM", NORM_BY_2 = "NORM05", TYPING_DELAY = "DELAY",
            BETWEEN_STEP = "BETWEEN_STEP", ALERT_PAUSE = "ALERT_PAUSE", HIGHLIGHT_PAUSE = "HIGHLIGHT_PAUSE";

    public static String PHASE_OPEN_PAGE = "Open page",
            PHASE_REGISTRATION = "Registration",
            PHASE_LOGIN = "Login",
            PHASE_CREATE_DISHES = "Create dished",
            PHASE_CREATE_TABLES = "Create Tables",
            PHASE_DELETE_USER = "Delete user",
            FINAL_PAUSE = "FINAL_PAUSE";

    private static final Map<String, Map<String, Integer>> m = Map.of(
            FAST, Map.of(
                    TYPING_DELAY, 2,
                    BETWEEN_STEP, 20,
                    HIGHLIGHT_PAUSE, 400,
                    ALERT_PAUSE, 1000,
                    FINAL_PAUSE, 1000
            ),
            NORM, Map.of(
                    TYPING_DELAY, 20,
                    BETWEEN_STEP, 300,
                    HIGHLIGHT_PAUSE, 200,
                    ALERT_PAUSE, 1000,
                    FINAL_PAUSE, 3000
            ),
            NORM_BY_2, Map.of(
                    TYPING_DELAY, 40,
                    BETWEEN_STEP, 600,
                    HIGHLIGHT_PAUSE, 400,
                    ALERT_PAUSE, 2000,
                    FINAL_PAUSE, 6000
            )
    );

    public static void highlight2(WebDriver driver, WebElement element, String temp) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String originalStyle = element.getAttribute("style");

        for (int i = 0; i < 2; i++) {
            js.executeScript("arguments[0].style.border='2px solid red';", element);
            pause(m.get(temp).get(HIGHLIGHT_PAUSE));
            js.executeScript("arguments[0].style.border='';", element);
            pause(m.get(temp).get(HIGHLIGHT_PAUSE));
        }

        // Restore original style if any
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void pause(String temp) {
        try {
            Thread.sleep(m.get(temp).get(BETWEEN_STEP));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void pause(String temp, String delayType) {
        try {
            Thread.sleep(m.get(temp).get(delayType));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void typeText(WebElement element, String text, long delayMillis) {
        for (char ch : text.toCharArray()) {
            element.sendKeys(Character.toString(ch));
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void typeIntoInputById(WebDriver d, String text, String elementId, String temp) {
        WebElement nameInput = d.findElement(By.id(elementId));
        typeIntoInput(d, nameInput, text, temp);
    }

    public static void typeIntoInput(WebDriver d, WebElement e, String text, String temp) {
        e.clear();
        highlight2(d, e, temp);
        typeText(e, text, m.get(temp).get(TYPING_DELAY));
    }

    public static void click(WebDriver d, WebElement e, String temp) {
        pause(m.get(temp).get(BETWEEN_STEP));
        highlight2(d, e, temp);
        e.click();
    }

    public static void alertAccept(WebDriverWait wait, WebDriver d, String temp) {
        pause(m.get(temp).get(ALERT_PAUSE));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = d.switchTo().alert();
        alert.accept();
        pause(m.get(temp).get(BETWEEN_STEP));
    }

    public static void findButtonByTextAndClick(WebDriver driver, String text, String temp) {
        WebElement button = driver.findElement(By.xpath("//button[text()='arg123']".replace("arg123", text)));
        highlight2(driver, button, temp);
        button.click();
        pause(m.get(temp).get(BETWEEN_STEP));
    }

    public static void checkCheckbox(WebDriver driver, String id, String temp) {
        WebElement checkbox = driver.findElement(By.id(id));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        pause(m.get(temp).get(BETWEEN_STEP));
    }

    public static void selectOptionByBySelectText(WebDriver driver, int index, String text, String temp) {
        WebElement selectElement = driver.findElement(
                By.xpath("//select[option[text()='arg1']]".replace("arg1", text))
        );
        Select select = new Select(selectElement);
        select.selectByIndex(index);
        pause(m.get(temp).get(BETWEEN_STEP));


    }

    private static long t = System.currentTimeMillis();

    public static void markTime(String phase) {
        if (t == 0) {
            log.debug(phase);
            t = System.currentTimeMillis();
        } else {
            long now = System.currentTimeMillis();
            long passed = now - t;
            t = now;
            log.debug("Flow [{}] duration [{}] second", phase, passed / 1000);
        }
    }

    public static void registerEateryAdmin(
            WebDriver driver, WebDriverWait wait,
            String userName, String userPass,
            String userMail, String eateryName, String temp) {

        // 2. Enter registration details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("106")));
        WebElement nameInput = driver.findElement(By.id("106"));
        Util.typeIntoInput(driver, nameInput, userName, temp);

        WebElement emailInput = driver.findElement(By.id("107"));
        Util.typeIntoInput(driver, emailInput, userMail, temp);

        WebElement passwordInput = driver.findElement(By.id("108"));
        Util.typeIntoInput(driver, passwordInput, userPass, temp);

        WebElement confirmPasswordInput = driver.findElement(By.id("109"));
        Util.typeIntoInput(driver, confirmPasswordInput, userPass, temp);

        WebElement restaurantNameInput = driver.findElement(By.id("110"));
        Util.typeIntoInput(driver, restaurantNameInput, eateryName, temp);

        // 3. Click the Register button
        WebElement registerButton = driver.findElement(By.id("111"));
        Util.click(driver, registerButton, temp);

        // 4. Wait for and accept the alert
        Util.alertAccept(wait, driver, NORM);
        wait.until(ExpectedConditions.urlContains("/admin/login"));
        // 1. Verify redirection to login page
        assertTrue(driver.getCurrentUrl().contains("/admin/login"),
                "URL should contain '/admin/login' after successful registration");
        markTime(PHASE_REGISTRATION);

    }

    public static void createCategories(WebDriver driver, WebDriverWait wait, String temp) {
        navigate(driver, wait, "nav002", "/admin/categories", temp);
        Util.findButtonByTextAndClick(driver, "Kateqoriya əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "ch003", temp);
        Util.checkCheckbox(driver, "ch004", temp);
        Util.checkCheckbox(driver, "ch005", temp);
        Util.checkCheckbox(driver, "ch006", temp);
        Util.checkCheckbox(driver, "ch007", temp);
        Util.findButtonByTextAndClick(driver, "Seçilmiş kateqoriyaları yarat", NORM);
    }

    public static void navigate(WebDriver driver, WebDriverWait wait, String id, String expectedUrl, String temp) {
        WebElement nameInput = driver.findElement(By.id(id));
        Util.click(driver, nameInput, temp);
        wait.until(ExpectedConditions.urlContains(expectedUrl));
        Util.pause(NORM);
    }

    public static boolean login(WebDriver driver, WebDriverWait wait,
                          String testEmail, String testPassword,
                           String testRestaurantName, String temp) {
        try {
            // 1. Wait for the login form
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("101")));

            WebElement loginEmailInput = driver.findElement(By.id("101"));
            Util.typeIntoInput(driver, loginEmailInput, testEmail, temp);

            WebElement loginPasswordInput = driver.findElement(By.id("102"));
            Util.typeIntoInput(driver, loginPasswordInput, testPassword, temp);

            WebElement loginButton = driver.findElement(By.id("103"));
            Util.click(driver, loginButton, temp);

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
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
            List<WebElement> restaurantHeaders = driver.findElements(By.tagName("h2"));

            if (restaurantHeaders.isEmpty()) {
                System.out.println("No restaurant headers found after login");
                return false;
            }

            boolean foundRestaurant = restaurantHeaders.stream()
                    .anyMatch(header -> header.getText().equals(testRestaurantName));

            if (!foundRestaurant) {
                log.debug("Expected restaurant [{}] not found", testRestaurantName);
                return false;
            }
        } catch (NoSuchElementException | TimeoutException e) {
            log.debug("Login process encountered a problem: " + e.getMessage());
        }
        markTime(PHASE_LOGIN);
        return true;

    }

    public static void openPage(WebDriver driver, String host, String path, String pause) {
        driver.get(host + "/admin/" + path);
        driver.manage().window().maximize();
        Util.pause(pause);
        markTime(PHASE_OPEN_PAGE);
    }

    public static void createDishes(WebDriver driver, WebDriverWait wait, String temp) {
        navigate(driver, wait, "nav003", "menu", temp);
        Util.selectOptionByBySelectText(driver, 1, "Kateqoriya seçin", temp);
        Util.findButtonByTextAndClick(driver, "Yemək əlavə et", NORM);
        Util.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", NORM);
        Util.checkCheckbox(driver, "dsh001", temp);
        Util.checkCheckbox(driver, "dsh002", temp);
        Util.checkCheckbox(driver, "dsh003", temp);
        Util.checkCheckbox(driver, "dsh004", temp);
        Util.findButtonByTextAndClick(driver, "Add Selected Dishes (", NORM);
        pause(NORM);
        markTime(PHASE_CREATE_DISHES);
    }


}