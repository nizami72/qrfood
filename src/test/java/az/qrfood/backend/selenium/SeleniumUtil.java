package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.TestTestovCreator.visualEffect;
import static org.junit.jupiter.api.Assertions.assertTrue;
import az.qrfood.backend.selenium.dto.StaffItem;
import az.qrfood.backend.selenium.dto.Table;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Log4j2
public class SeleniumUtil {

    public static String FAST = "FAST", NORM = "NORM", NORM_X_2 = "NORM_X_2", TYPING_DELAY = "DELAY",
            BETWEEN_STEP = "BETWEEN_STEP", ALERT_PAUSE = "ALERT_PAUSE", HIGHLIGHT_PAUSE = "HIGHLIGHT_PAUSE";

    public static String PHASE_OPEN_PAGE = "Open page",
            PHASE_REGISTRATION = "Registration",
            PHASE_LOGIN = "Login",
            PHASE_CREATE_DISHES = "Create dished",
            PHASE_CREATE_TABLES = "Create Tables",
            PHASE_DELETE_USER = "Delete user",
            FINAL_PAUSE = "FINAL_PAUSE",
            PHASE_CREATE_CATEGORIES = "Create categories",
            PHASE_CREATE_STAFF = "Create staff",
            PHASE_EDIT_USER = "Edit user";

    private static final Map<String, Map<String, Integer>> m = Map.of(
            FAST, Map.of(
                    TYPING_DELAY, 5,
                    BETWEEN_STEP, 20,
                    HIGHLIGHT_PAUSE, 100,
                    ALERT_PAUSE, 100,
                    FINAL_PAUSE, 100
            ),
            NORM, Map.of(
                    TYPING_DELAY, 20,
                    BETWEEN_STEP, 300,
                    HIGHLIGHT_PAUSE, 200,
                    ALERT_PAUSE, 1000,
                    FINAL_PAUSE, 3000
            ),
            NORM_X_2, Map.of(
                    TYPING_DELAY, 40,
                    BETWEEN_STEP, 600,
                    HIGHLIGHT_PAUSE, 400,
                    ALERT_PAUSE, 2000,
                    FINAL_PAUSE, 6000
            )
    );

    public static void highlight2(WebDriver driver, WebElement element, String temp) {
        if(!visualEffect) return;
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

    public static void typeIntoInputById(WebDriver d, int text, String elementId, String temp) {
        WebElement nameInput = d.findElement(By.id(elementId));
        typeIntoInput(d, nameInput, String.valueOf(text), temp);
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

    public static void findButtonsByTextAndClick(WebDriver driver, String text, int buttonIndex, String temp) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[text()='arg123']".replace("arg123", text)));
        WebElement currentButton = buttons.get(buttonIndex);
        highlight2(driver, currentButton, temp);
        currentButton.click();
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

    public static void selectRandomOptionByText(WebDriver driver, String text, String temp) {
        WebElement selectElement = driver.findElement(
                By.xpath("//select[option[text()='arg1']]".replace("arg1", text))
        );
        Select select = new Select(selectElement);

        // получаем все доступные опции
        List<WebElement> options = select.getOptions();

        if (options.size() > 2) {
            // исключаем первый элемент, если он "пустой"/"default"
            int randomIndex = new Random().nextInt(options.size() - 1) + 1;
            select.selectByIndex(randomIndex);
        } else {
            // если опция всего одна — выбираем её
            select.selectByIndex(1);
        }

        pause(m.get(temp).get(BETWEEN_STEP));
    }


    private static long t = System.currentTimeMillis();

    public static long markTime(String phase) {
        long passed = 0;
        if (t == 0) {
            log.debug(phase);
            t = System.currentTimeMillis();
        } else {
            long now = System.currentTimeMillis();
            passed = now - t;
            t = now;
            log.debug("Flow [{}] duration [{}] second", phase, passed / 1000);
        }
        return passed/1000;
    }

    public static void registerEateryAdmin(
            WebDriver driver, WebDriverWait wait,
            String userName, String userPass,
            String userMail, String eateryName, String temp) {

        // 2. Enter registration details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("106")));
        WebElement nameInput = driver.findElement(By.id("106"));
        SeleniumUtil.typeIntoInput(driver, nameInput, userName, temp);

        WebElement emailInput = driver.findElement(By.id("107"));
        SeleniumUtil.typeIntoInput(driver, emailInput, userMail, temp);

        WebElement passwordInput = driver.findElement(By.id("108"));
        SeleniumUtil.typeIntoInput(driver, passwordInput, userPass, temp);

        WebElement confirmPasswordInput = driver.findElement(By.id("109"));
        SeleniumUtil.typeIntoInput(driver, confirmPasswordInput, userPass, temp);

        WebElement restaurantNameInput = driver.findElement(By.id("110"));
        SeleniumUtil.typeIntoInput(driver, restaurantNameInput, eateryName, temp);

        // 3. Click the Register button
        WebElement registerButton = driver.findElement(By.id("111"));
        SeleniumUtil.click(driver, registerButton, temp);

        // 4. Wait for and accept the alert
        SeleniumUtil.alertAccept(wait, driver, temp);
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
            WebElement restaurantDiv = driver.findElement(
                    By.xpath("//div[contains(@class, 'text-gray-700') and contains(text(),'" + testRestaurantName + "')]")
            );

            String restaurantName = restaurantDiv.getText();

            if (restaurantName.isEmpty()) {
                System.out.println("No restaurant headers found after login");
                return false;
            } else {
                log.debug("Restaurant header found [{}]", restaurantName);
            }

            boolean foundRestaurant = restaurantName.equals(testRestaurantName);

            if (!foundRestaurant) {
                log.debug("Expected restaurant [{}] not found", testRestaurantName);
                return false;
            }

        } catch (NoSuchElementException | TimeoutException e) {
            log.debug("Login process encountered a problem [{}] ", e.getMessage());
        }
        return true;

    }

    public static void openPage(WebDriver driver, String host, String path, String pause) {
        driver.get(host + "/admin/" + path);
//        driver.manage().window().maximize();
    }

    public static void createDishes(WebDriver driver, WebDriverWait wait, String temp) {
        navigate(driver, wait, "nav003", "menu", temp);
        SeleniumUtil.selectOptionByBySelectText(driver, 1, "Kateqoriya seçin", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Yemək əlavə et", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Əvvəlcədən təyin edilmiş siyahı", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh001", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh002", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh003", temp);
        SeleniumUtil.checkCheckbox(driver, "dsh004", temp);
        SeleniumUtil.findButtonByTextAndClick(driver, "Add Selected Dishes (", temp);
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

    static Iterator<Integer> idx = List.of(1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3).iterator();

    public static void createTables(WebDriver driver, WebDriverWait wait, List<Table> tableItems, String norm) {
        for (Table tableItem : tableItems) {
            navigate(driver, wait, "nav004", "/admin/tables", norm);
            SeleniumUtil.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
            SeleniumUtil.typeIntoInputById(driver, tableItem.getNumber(), "tblcrrt01", norm);
            SeleniumUtil.typeIntoInputById(driver, tableItem.getSeats(), "tblcrrt02", norm);
            SeleniumUtil.typeIntoInputById(driver, tableItem.getNote(), "tblcrrt03", norm);
            SeleniumUtil.findButtonByTextAndClick(driver, "Masa əlavə et", norm);
            SeleniumUtil.pause(norm);
            assignTableWaiter(driver, wait, tableItem, idx.next(), norm);
        }
    }

    static int buttonIndex = 0;

    public static void assignTableWaiter(WebDriver driver, WebDriverWait wait, Table tableItem,
                                         int selectIdx, String norm) {
        driver.navigate().refresh();
        SeleniumUtil.findButtonsByTextAndClick(driver, "Ofisiant təyin et", buttonIndex++, norm);
        SeleniumUtil.selectRandomOptionByText(driver, "Ofisiant seçin", norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Təyin et", norm);
    }

    public static void printQrCode(WebDriver driver, WebDriverWait wait, String norm) {
        navigate(driver, wait, "nav004", "/admin/tables", norm);
        WebElement qrCode = driver.findElement(By.cssSelector("[id^='qr-card']"));
        SeleniumUtil.highlight2(driver, qrCode, norm);
        SeleniumUtil.findButtonByTextAndClick(driver, "Çap et", norm);
        SeleniumUtil.pause(norm);
    }

}