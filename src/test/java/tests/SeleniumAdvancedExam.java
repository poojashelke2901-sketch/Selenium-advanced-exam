package tests;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

public class SeleniumAdvancedExam {

    RemoteWebDriver driver;
    WebDriverWait wait;

    String username = System.getenv("LT_USERNAME");
    String accessKey = System.getenv("LT_ACCESS_KEY");

    @Parameters({"browser", "platform", "version"})
    @BeforeClass
    public void setup(
            @Optional("chrome") String browser,
            @Optional("Windows 10") String platform,
            @Optional("128.0") String version) throws Exception {

        MutableCapabilities options;

        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions chrome = new ChromeOptions();
            chrome.setBrowserVersion(version);
            chrome.setPlatformName(platform);
            options = chrome;
        } else {
            EdgeOptions edge = new EdgeOptions();
            edge.setBrowserVersion(version);
            edge.setPlatformName(platform);
            options = edge;
        }

        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("build", "Selenium Advanced Exam");
        ltOptions.put("name", browser + " Test");
        ltOptions.put("video", true);
        ltOptions.put("network", true);
        ltOptions.put("consoleLogs", "info");
        ltOptions.put("screenshots", true);

        options.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(
                new URL("https://" + username + ":" + accessKey + "@hub.lambdatest.com/wd/hub"),
                options
        );

        System.out.println("Session ID: " + driver.getSessionId());
        System.out.println("Running on: " + browser + " | " + platform);

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testScenario() {

        // 1. Navigate
        driver.get("https://www.testmuai.com/");

        // 2. Wait for DOM
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));

        takeScreenshot("Step1_Home");

        // 3. Scroll to Explore Agentic Cloud
        WebElement explore = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href*='agentic-cloud']")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", explore);

        takeScreenshot("Step3_Scroll");

        // 4. Open in NEW TAB
        ((JavascriptExecutor) driver).executeScript(
                "window.open(arguments[0].href,'_blank');", explore);

        // 5. Handle windows
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        List<String> windows = new ArrayList<>(driver.getWindowHandles());
        System.out.println("Window Handles: " + windows);

        driver.switchTo().window(windows.get(1));

        // 6. Verify URL
        Assert.assertTrue(driver.getCurrentUrl().contains("agentic"));

        takeScreenshot("Step6_NewTab");

        // 7. Scroll to section (FIXED LOCATOR)
        WebElement section = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(),'Scale with Agentic Cloud')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", section);

        takeScreenshot("Step7_Section");

        // 8. Click Try Now (FIXED FOR EDGE)
        By tryNowBy = By.xpath("//a[contains(text(),'Try Now')]");

        WebElement tryNow = wait.until(ExpectedConditions.presenceOfElementLocated(tryNowBy));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tryNow);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(tryNow)).click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JS click");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tryNow);
        }

        // 9. Validate title (FIXED SAFE ASSERT)
        wait.until(ExpectedConditions.titleContains("Sign"));

        String title = driver.getTitle();
        System.out.println("Page Title: " + title);

        Assert.assertTrue(title.toLowerCase().contains("sign"),
                "Title validation failed: " + title);

        takeScreenshot("Step9_Signup");

        // 10. Close current window
        driver.close();

        // Switch back
        driver.switchTo().window(windows.get(0));

        // 11. Print window count
        System.out.println("Window Count: " + driver.getWindowHandles().size());

        // 12. Navigate to blog
        driver.get("https://www.testmuai.com/blog");

        // 13. Click Community
        WebElement community = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Community")));

        community.click();

        wait.until(ExpectedConditions.urlContains("community"));

        Assert.assertTrue(driver.getCurrentUrl().contains("community.testmuai.com"));

        takeScreenshot("Step13_Community");

        // 14. Close browser
        driver.quit();
    }

    public void takeScreenshot(String name) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            File folder = new File("screenshots");
            if (!folder.exists()) {
                folder.mkdir();
            }

            FileUtils.copyFile(src, new File("screenshots/" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}