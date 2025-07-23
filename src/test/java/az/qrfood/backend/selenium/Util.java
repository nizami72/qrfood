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

    static long betweenStepsDelay = 200;

    public static String FAST = "FAST", NORM = "NORM", NORM15 = "NORM05", TYPING_DELAY = "DELAY", BETWEEN_STEP = "BETWEEN_STEP";
    private static final Map<String, Map<String, Integer>> m = Map.of(
            FAST, Map.of(
                    TYPING_DELAY, 2,
                    BETWEEN_STEP, 20
            ),
            NORM, Map.of(
                    TYPING_DELAY, 30,
                    BETWEEN_STEP, 800
            ),
            NORM15, Map.of(
                    TYPING_DELAY, 15,
                    BETWEEN_STEP, 300
            )
    );

    public static void highlight2(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String originalStyle = element.getAttribute("style");

        for (int i = 0; i < 2; i++) {
            js.executeScript("arguments[0].style.border='1px solid red';", element);
            pause(betweenStepsDelay);
            js.executeScript("arguments[0].style.border='';", element);
            pause(betweenStepsDelay);
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
        pause(m.get(temp).get(BETWEEN_STEP));
        highlight2(d, e);
        typeText(e, text, m.get(temp).get(TYPING_DELAY));
    }

    public static void click(WebDriver d, WebElement e) {
        pause(betweenStepsDelay);
        highlight2(d, e);
        e.click();
    }

    public static void alertAccept(WebDriverWait wait, WebDriver d) {
        wait.until(ExpectedConditions.alertIsPresent());
//        highlight2(d, e);
        Alert alert = d.switchTo().alert();
        alert.accept();
        pause(betweenStepsDelay);
    }

    public static void findButtonByTextAndClick(WebDriver driver, String text, String temp) {
        WebElement button = driver.findElement(By.xpath("//button[text()='arg123']".replace("arg123", text)));
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

    public static void selectOptionByBySelectText(WebDriver driver, int index, String text) {
        WebElement selectElement = driver.findElement(
                By.xpath("//select[option[text()='arg1']]".replace("arg1", text))
        );
        Select select = new Select(selectElement);
        select.selectByIndex(index);
        pause(betweenStepsDelay);


    }

}