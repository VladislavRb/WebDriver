import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class addItemsToBucketTest {

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

    @BeforeMethod (alwaysRun = true)
    public void browserSetup() {
        driver = new ChromeDriver();
        jsExecutor = (JavascriptExecutor) driver;
    }

    @Test
    public void addSneakersToBucketListTest() {
        driver.get("http://www.sportmaster.by/catalogitem/krossovki_mugskie_nike_md_runner_2749794n06010/");

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(3))
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

        WebElement goToBucketLink = getWebElementByXpath(wait, "//a[text()='В корзину']");
        jsExecutor.executeScript("arguments[0].click()", goToBucketLink);

        WebElement labelInPopupHeader = getWebElementByXpath(wait, "//p[@class='cb-item-popup-head-heading']");
        String popupBodyText = getWebElementByXpath(wait, "//div[@class='cb-item-popup-body-text']").getAttribute("innerText");

        Assert.assertEquals(extractSneakersInfo(popupBodyText.trim()),"Кроссовки мужские Nike Md Runner 2");
    }

    @AfterMethod (alwaysRun = true)
    public void browserQuit() {
        driver.quit();
        driver = null;
    }
}
