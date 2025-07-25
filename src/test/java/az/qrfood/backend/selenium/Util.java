package az.qrfood.backend.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.Map;

public class Util {


    public static String FAST = "FAST", NORM = "NORM", NORM15 = "NORM05", TYPING_DELAY = "DELAY",
            BETWEEN_STEP = "BETWEEN_STEP", ALERT_PAUSE = "ALERT_PAUSE", HIGHLIGHT_PAUSE = "HIGHLIGHT_PAUSE";
    private static final Map<String, Map<String, Integer>> m = Map.of(
            FAST, Map.of(
                    TYPING_DELAY, 2,
                    BETWEEN_STEP, 20,
                    HIGHLIGHT_PAUSE, 400,
                    ALERT_PAUSE, 1000
            ),
            NORM, Map.of(
                    TYPING_DELAY, 20,
                    BETWEEN_STEP, 300,
                    HIGHLIGHT_PAUSE, 200,
                    ALERT_PAUSE, 1000
            ),
            NORM15, Map.of(
                    TYPING_DELAY, 15,
                    BETWEEN_STEP, 300,
                    HIGHLIGHT_PAUSE, 400,
                    ALERT_PAUSE, 1000
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

}