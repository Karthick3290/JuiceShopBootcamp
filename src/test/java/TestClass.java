import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class TestClass extends BaseUI {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeTest
    public void setup(){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "chrome");

        try {
            driver = new RemoteWebDriver(new URL("http://hub.com:4444/wd/hub"),
                    capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    void dismissPopUpBanner() {
        if (driver.findElements(By.xpath("//button[@aria-label='Close Welcome Banner']")).size() > 0)
            driver.findElement(By.xpath("//button[@aria-label='Close Welcome Banner']")).click();
        if (driver.findElements(By.cssSelector("div[aria-label='cookieconsent']")).size() > 0 && driver.findElement(By.cssSelector("div[aria-label='cookieconsent']")).isDisplayed())
            driver.findElement(By.cssSelector("a[aria-label='dismiss cookie message']")).click();
    }

    @Test(priority = 1)
    public void register() {
        dismissPopUpBanner();
        driver.get("http://"+url+":3000/#/register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(email);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("repeatPassword"))).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select[@name='securityQuestion']"))).click();
        driver.findElement(By.xpath(String.format("//span[(@class='mat-option-text') and text()[normalize-space() = '%s']]", "Your eldest siblings middle name?"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("securityAnswerControl"))).sendKeys("Krishnan");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registerButton"))).click();
    }

    @Test(priority = 2)
    public void login() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(email);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginButton"))).click();
        boolean verifyLogin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@aria-label='Add to Basket'])[1]"))).isDisplayed();
        Assert.assertEquals(verifyLogin, true);
    }

    @Test(priority = 3)
    public void addToCart() {
        driver.findElement(By.xpath("(//button[@aria-label='Add to Basket'])[1]")).click();
        Assert.assertEquals(driver.findElement(By.xpath("/html/body/app-root/div/mat-sidenav-container/mat-sidenav-content/app-navbar/mat-toolbar/mat-toolbar-row/button[4]/span/span[2]")).isDisplayed(), true);
    }

    @Test(priority = 4)
    public void deleteItemFromCart() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button//span[contains(text(),'Your Basket')]"))).click();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//mat-cell[contains(@class,'remove')]//button")).click();
    }

    @AfterClass
    public void close(){
        driver.quit();
    }
}
