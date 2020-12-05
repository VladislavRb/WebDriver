import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class AddItemsToBasketTest {

    private WebDriver driver;
    private JavascriptExecutor jsExecutor;

    private WebElement getWebElementByXpath(Wait<WebDriver> wait, String xpath) {
        return wait
                .until(ExpectedConditions
                        .presenceOfElementLocated(By.xpath(xpath)));
    }

    private String extractSneakersInfo(String rawSneakersString) {
        int vendorCodeStartIndex = rawSneakersString.indexOf("\n");

        return rawSneakersString.substring(0, vendorCodeStartIndex);
    }

    @BeforeTest (alwaysRun = true)
    public void browserSetup() {
        String path = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", path + "\\src\\drivers\\chromedriver.exe");

        driver = new ChromeDriver();
        jsExecutor = (JavascriptExecutor) driver;
    }

    @Test
    public void addSneakersToBasketListTest() {
        List<String> expectedResults = Arrays.asList(
                "Товар добавлен в корзину",
                "Кроссовки мужские Nike Md Runner 2",
                "176,00 руб.",
                "Перейти в корзину"
                );

        driver.get("http://www.sportmaster.by/catalogitem/krossovki_mugskie_nike_md_runner_2749794n06010/");

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Timeout for waiting was exceeded!");

        List<WebElement> sneakersSizes = wait
                .until(ExpectedConditions
                        .presenceOfAllElementsLocatedBy(By.xpath("//li[@class='cb-item-actions-data-sizes']//li")));

        WebElement firstAvailableSizeInput = sneakersSizes.stream()
                .filter(webElement -> webElement.findElement(By.tagName("input")).isEnabled())
                .findFirst()
                .get()
                .findElement(By.tagName("input"));

        jsExecutor.executeScript(String.format("document.getElementById('%s').setAttribute('class', 'checked')",
                firstAvailableSizeInput.getAttribute("id")));

        WebElement goToBasketLink = getWebElementByXpath(wait, "//a[text()='В корзину']");
        jsExecutor.executeScript("arguments[0].click()", goToBasketLink);

        WebElement itemPopupWindow = getWebElementByXpath(wait, "//div[@class='cb-item-popup']");

        List<String> actualResults = Arrays.asList(
                itemPopupWindow.findElement(By.xpath("//p[@class='cb-item-popup-head-heading']")).getAttribute("innerText").trim(),
                extractSneakersInfo(itemPopupWindow.findElement(By.xpath("//div[@class='cb-item-popup-body-text']")).getAttribute("innerText").trim()),
                itemPopupWindow.findElement(By.xpath("//div[@class='cb-item-price-old']")).getText(),
                itemPopupWindow.findElement(By.xpath("//a[contains(@class, 'go_to_order_basket')]")).getAttribute("innerText").trim()
        );

        Assert.assertEquals(actualResults, expectedResults);
    }

    @AfterTest (alwaysRun = true)
    public void browserQuit() {
        driver.quit();
        driver = null;
    }
}
