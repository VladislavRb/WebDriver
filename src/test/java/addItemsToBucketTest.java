import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class addItemsToBucketTest {

    private WebDriver driver;

    @BeforeMethod (alwaysRun = true)
    public void browserSetup() {
        driver = new ChromeDriver();
    }

    @Test
    public void addSneakersToBucketListTest(){}

    @AfterMethod (alwaysRun = true)
    public void browserQuit() {
        driver.quit();
        driver = null;
    }
}
