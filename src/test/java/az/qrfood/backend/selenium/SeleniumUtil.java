package az.qrfood.backend.selenium;

import static az.qrfood.backend.selenium.TestTestovCreator.visualEffect;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Log4j2
public class SeleniumUtil {

    //<editor-fold desc="Fields">
    public static String FAST = "FAST", NORM = "NORM", NORM_X_2 = "NORM_X_2", TYPING_DELAY = "DELAY",
            BETWEEN_STEP = "BETWEEN_STEP", ALERT_PAUSE = "ALERT_PAUSE", HIGHLIGHT_PAUSE = "HIGHLIGHT_PAUSE";

    private static long t = System.currentTimeMillis();

    public static String PHASE_OPEN_PAGE = "Open page",
            PHASE_REGISTRATION = "Registration",
            PHASE_LOGIN = "Login",
            PHASE_CREATE_DISHES = "Create dished",
            PHASE_CREATE_TABLES = "Create Tables",
            PHASE_DELETE_USER = "Delete user",
            FINAL_PAUSE = "FINAL_PAUSE",
            PHASE_CREATE_CATEGORIES = "Create categories",
            PHASE_CREATE_STAFF = "Create staff",
            PHASE_EDIT_USER = "Edit user",
            PHASE_EDIT_EATERY = "Edit eatery";

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
    //</editor-fold>

    //<editor-fold desc="Pause">
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
    //</editor-fold>

    //<editor-fold desc="Type Text">
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

    public static void typeIntoInput(WebDriver d, By by, String text, String temp) {
        typeIntoInput(d, d.findElement(by), text, temp);
    }
    //</editor-fold>

    //<editor-fold desc="Find Element & Click">
    public static void click(WebDriver d, WebElement e, String temp) {
        pause(m.get(temp).get(BETWEEN_STEP));
        highlight2(d, e, temp);
        e.click();
    }


    public static void findButtonByTextAndClick(WebDriver driver, String text, String temp) {
        WebElement button = driver.findElement(By.xpath("//button[text()='arg123']".replace("arg123", text)));
        highlight2(driver, button, temp);
        button.click();
        pause(m.get(temp).get(BETWEEN_STEP));
    }

    public static void findButtonByIdAndClick(WebDriver driver, String id, String temp) {
        WebElement button = driver.findElement(By.id(id));
        highlight2(driver, button, temp);
        button.click();
        pause(m.get(temp).get(BETWEEN_STEP));
        pause(m.get(temp).get(BETWEEN_STEP));
        pause(m.get(temp).get(BETWEEN_STEP));
    }

    public static void findElementByTextAndClick(WebDriver driver, String element, String text, String temp) {
        WebElement button = driver.findElement(By.xpath("//" + element + "[text()='arg123']".replace("arg123", text)));
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

    public static void findByCssSelectorAndClick(WebDriver driver, String cssSelector) {
        WebElement menuButton = driver.findElement(By.cssSelector(cssSelector));
        menuButton.click();
    }
    //</editor-fold>

    //<editor-fold desc="Checkbox">
    public static void checkCheckbox(WebDriver driver, String id, String temp) {
        WebElement checkbox = driver.findElement(By.id(id));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        pause(m.get(temp).get(BETWEEN_STEP));
    }

    public static void checkCheckboxByWord(WebDriver driver, String catNameEn, String temp) {
        String xpathExpression = String.format("//tr[td[text()='%s']]//input[@type='checkbox']", catNameEn);
        WebElement checkbox = driver.findElement(By.xpath(xpathExpression));
        checkbox.click();
    }

    public static void checkCheckboxByWordDish(WebDriver driver, String catNameEn, String temp) {
        String xpathExpression = String.format(
                "//div[contains(@class, 'border') and .//p[text()='%s']]//input[@type='checkbox']",
                catNameEn
        );
        WebElement checkbox = driver.findElement(By.xpath(xpathExpression));
        checkbox.click();
    }
    //</editor-fold>

    //<editor-fold desc="Select Option">
    public static void selectOptionWithText(WebDriver driver, int index, String text, String temp) {
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

    public static void selectOptionIdAndText(WebDriver driver, String id, String text, String temp) {
        WebElement dropdownElement = driver.findElement(By.id(id));
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText(text);
    }
    //</editor-fold>

    public static void alertAccept(WebDriverWait wait, WebDriver d, String temp) {
        pause(m.get(temp).get(ALERT_PAUSE));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = d.switchTo().alert();
        alert.accept();
        pause(m.get(temp).get(BETWEEN_STEP));
    }

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
        return passed / 1000;
    }

    /**
     * Finds a dropdown (select element) by its ID and returns the text of the currently selected option.
     *
     * @param driver    The WebDriver instance.
     * @param elementId The ID of the HTML select element.
     * @return The visible text of the selected option as a String.
     */
    public static String findSelectedOptionTextById(WebDriver driver, String elementId) {
        // 1. Find the dropdown element by its ID
        WebElement dropdownElement = driver.findElement(By.id(elementId));

        // 2. Create a Select object to wrap the element
        Select dropdown = new Select(dropdownElement);

        // 3. Get the WebElement for the first selected option
        WebElement selectedOption = dropdown.getFirstSelectedOption();

        // 4. Return the visible text of that option
        return selectedOption.getText();
    }

    public static void highlight2(WebDriver driver, WebElement element, String temp) {
        if (!visualEffect) return;
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
}