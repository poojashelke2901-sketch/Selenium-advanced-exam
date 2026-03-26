package tests;


	 import org.apache.commons.io.FileUtils;
     import org.openqa.selenium.*;
	 import org.openqa.selenium.chrome.ChromeDriver;
	 import org.openqa.selenium.support.ui.ExpectedConditions;
	 import org.openqa.selenium.support.ui.WebDriverWait;
	 import org.testng.Assert;
	 import org.testng.annotations.AfterClass;
	 import org.testng.annotations.BeforeClass;
	 import org.testng.annotations.Test;

     import java.io.File;
     import java.io.IOException;
     import java.time.Duration;
	 import java.util.ArrayList;
	 import java.util.List;
	 import java.util.Set;

	 public class SeleniumAdvancedExam  {

	 WebDriver driver;
	 WebDriverWait wait;

	 @BeforeClass
	 public void setup() {
	 driver = new ChromeDriver();
	 driver.manage().window().maximize();
	 wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	 }

	 @Test
	 public void testScenario() throws InterruptedException {

	 // 1. Navigate
	 driver.get("https://www.testmuai.com/");

	 // 2. Wait until DOM ready
	 wait.until(webDriver ->
	 ((JavascriptExecutor) webDriver)
	 .executeScript("return document.readyState")
	 .equals("complete"));

	 // 3. Scroll to 'Explore Agentic Clouds'
	 // Locator for the Agentic Cloud link (adjust if yours differs) 
	 By agenticBy = By.cssSelector("a[href*='agentic-cloud']");

	 // 1) Wait for visibility and grab the element
	 WebElement exploreLink = wait.until(ExpectedConditions.visibilityOfElementLocated(agenticBy));

	 // 2) Scroll into view and offset for sticky header
	 JavascriptExecutor js = (JavascriptExecutor) driver;
	 long headerH = 0L;
	 try {
	 Object h = js.executeScript(
	 "const el = document.querySelector('header,.navbar,.chfw-navbar,.chfw-navbar__links');" +
	 "return el && el.offsetHeight ? el.offsetHeight : 0;");
	 if (h instanceof Long) headerH = (Long) h;
	 if (h instanceof Number) headerH = ((Number) h).longValue();
	 } catch (Exception ignored) {}
	 js.executeScript("arguments[0].scrollIntoView({block:'start'});", exploreLink);
	 js.executeScript("window.scrollBy(0, -arguments[0]-20);", headerH);

	 // 3) Ensure it is clickable now
	 wait.until(ExpectedConditions.elementToBeClickable(exploreLink));

	 takeScreenshot("Step3_Scroll");
	 
	 // 4) Open in a NEW TAB (Ctrl/Cmd+Enter); fallback to window.open
	 String os = System.getProperty("os.name").toLowerCase();
	 Keys mod = os.contains("mac") ? Keys.COMMAND : Keys.CONTROL;
	 try {
	 exploreLink.sendKeys(Keys.chord(mod, Keys.ENTER));
	 } catch (Exception e) {
	 js.executeScript("window.open(arguments[0].href,'_blank');", exploreLink);
	 }

	 // 5) Wait until the new tab is present
	 wait.until(ExpectedConditions.numberOfWindowsToBe(2));

	 // 4. Click and verify new tab
	 exploreLink.click();

	 // 5. Handle windows
	 Set<String> handles = driver.getWindowHandles();
	 List<String> windowList = new ArrayList<>(handles);

	 System.out.println("Window Handles: " + windowList);

	 Assert.assertEquals(windowList.size(), 2);

	 // Switch to new tab
	 driver.switchTo().window(windowList.get(1));

	 // 6. Verify URL
	 String expectedUrlContains = "agentic";
	 Assert.assertTrue(driver.getCurrentUrl().contains(expectedUrlContains));

	 takeScreenshot("Step6_NewTab");
	 
	 // 7. Scroll to specific section
	 WebElement section = wait.until(ExpectedConditions.presenceOfElementLocated(
	 By.xpath("//*[contains(text(),'Seamlessly Scale with Agentic Cloud')]")
	 ));

	 ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", section);
	 
	// 8. Click 'Try Now For Free' with retry logic
	 By tryNowBy = By.xpath("//a[contains(text(),'Try Now For Free')]");

	 WebElement tryNow = null;
	 for (int i = 0; i < 3; i++) { // retry up to 3 times
	     try {
	         tryNow = wait.until(ExpectedConditions.elementToBeClickable(tryNowBy));
	         tryNow.click();
	         break; // success, exit loop
	     } catch (StaleElementReferenceException e) {
	         System.out.println("Stale element, retrying... attempt " + (i + 1));
	     }
	 }

	 // 9. Validate title
	 wait.until(ExpectedConditions.titleContains("Sign up"));
	 String actualTitle = driver.getTitle();

	 Assert.assertTrue(actualTitle.contains("Sign up for free"));

	  takeScreenshot("Step9_Signup");
	  
	 // 10. Close current window
	 driver.close();

	 // Switch back to main window
	 driver.switchTo().window(windowList.get(0));

	 // 11. Print window count
	 System.out.println("Window count: " + driver.getWindowHandles().size());

	 // 12. Navigate to blog
	 driver.get("https://www.testmuai.com/blog");

	 
	// 13. Click Community (re-locate after navigation)
	 By communityBy = By.linkText("Community");
	 WebElement community = wait.until(ExpectedConditions.elementToBeClickable(communityBy));
	 try {
	     community.click();
	 } catch (StaleElementReferenceException e) {
	     System.out.println("Community link went stale, re-finding...");
	     community = wait.until(ExpectedConditions.elementToBeClickable(communityBy));
	     community.click();
	 }

	 // Verify URL
	 wait.until(ExpectedConditions.urlContains("community"));
	 String actualUrl = driver.getCurrentUrl();
	 Assert.assertTrue(actualUrl.startsWith("https://community.testmuai.com/"),
	     "Unexpected URL: " + actualUrl);
	 
	 takeScreenshot("Step13_Community");

	 // 14. Close browser
	 driver.quit();
	 }
	 
	// Screenshot method (IMPORTANT NOTE)
	    public void takeScreenshot(String name) {
	        try {
	            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	            FileUtils.copyFile(src, new File("screenshots/" + name + ".png"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    }
	 }
