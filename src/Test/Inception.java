package Test;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Inception {
	public static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    public static final String AUTOMATE_KEY =System.getenv("BROWSERSTACK_ACCESS_KEY");
    public static final String LOGINID=System.getenv("LOGINID");
    public static final String LOGINPASSWORD=System.getenv("LOGINPASSWORD");
    public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);  // A pool of 2 threads are being created here. You can change this as per your parallel limit
        Set<Callable<String>> callables = new HashSet<Callable<String>>();
        Inception obj1 = new Inception();
        callables.add(new Callable<String>() {
            public String call() throws Exception {
                Hashtable<String, String> capsHashtable = new Hashtable<String, String>();
                capsHashtable.put("browser", "Chrome");
                capsHashtable.put("browser_version", "94.0");
                capsHashtable.put("os", "Windows");
                capsHashtable.put("os_version", "10");
                capsHashtable.put("build", "Inception test");
                capsHashtable.put("name", "Thread 1");
                obj1.executeTest(capsHashtable);
                return "Task 1 completed";


            }
        });
        callables.add(new Callable<String>() {
            public String call() throws Exception {
                Hashtable<String, String> capsHashtable = new Hashtable<String, String>();
                capsHashtable.put("browser", "Firefox");
                capsHashtable.put("browser_version", "95.0");
                capsHashtable.put("os", "Windows");
                capsHashtable.put("os_version", "11");
                capsHashtable.put("build", "Inception test");
                capsHashtable.put("name", "Thread 2");
                obj1.executeTest(capsHashtable);
                return "Task 2 completed";
            }
        });
        callables.add(new Callable<String>() {
            public String call() throws Exception {
                Hashtable<String, String> capsHashtable = new Hashtable<String, String>();
                capsHashtable.put("browser", "Edge");
                capsHashtable.put("browser_version", "96.0");
                capsHashtable.put("os", "Windows");
                capsHashtable.put("os_version", "10");
                capsHashtable.put("build", "Inception test");
                capsHashtable.put("name", "Thread 3");
               obj1.executeTest(capsHashtable);
                return "Task 3 completed";
            }
        });
        List<Future<String>> futures;
        futures = executorService.invokeAll(callables);
        for(Future<String> future : futures){
            System.out.println("future.get = " + future.get());
        }
        executorService.shutdown();
    }

public void executeTest(Hashtable<String, String> capsHashtable) {
    String key;
     String browser = capsHashtable.get("browser");
    DesiredCapabilities caps = new DesiredCapabilities();
    Set<String> keys = capsHashtable.keySet();
    Iterator<String> keysIterator = keys.iterator();
    while (keysIterator.hasNext()) {
        key = keysIterator.next();
        caps.setCapability(key, capsHashtable.get(key));
    }
    WebDriver driver;
    try {
        driver = new RemoteWebDriver(new URL(URL), caps);
        final JavascriptExecutor jse = (JavascriptExecutor) driver;
        try {
        	driver.get("https://live.browserstack.com/");
			driver.manage().window().maximize();
			WebDriverWait w= new WebDriverWait(driver, 60);
           
		   driver.findElement(By.id("user_email_login")).sendKeys(LOGINID);
         driver.findElement(By.id("user_password")).sendKeys(LOGINPASSWORD);
         driver.findElement(By.id("user_submit")).click();
         w.until(ExpectedConditions.urlContains("dashboard"));
         Thread.sleep(3000);
         if(browser!="Firefox")
         {
        	 if(browser=="Chrome") {
        		 driver.findElement(By.xpath("//div[@class='browser-list']/div[2]/div[4]/div/div[4]")).click();

        	 }
        	 else {
        		 driver.findElement(By.xpath("//div[@class='browser-list']/div[2]/div[4]/div/div[6]")).click();
        
        	 }
        		 w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toolbar__head']")));
        
   
         driver.findElement(By.xpath("//div[@id='vertical-toolbar']/div/ul/li[5]")).click();
          WebElement downloadlocal= driver.findElement(By.xpath("//div[@id='content']/div[@id='installExtension']/div[@id='dashboard-download-local-testing']"));

         if(downloadlocal.isDisplayed())
        	 driver.findElement(By.xpath("//div[@id='content']/div[@id='installExtension']/a")).click();
  
         }
         else
         {
        	 driver.findElement(By.xpath("//div[@class='browser-list']/div[2]/div[4]/div/div[5]")).click();
             w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toolbar__head']")));
          
         }
       
         WebElement canvas= driver.findElement(By.xpath("//div[@id='dashboard-wrapper']/div/div/canvas"));
         WebElement a = driver.switchTo().activeElement();
         a.sendKeys("browserstack",Keys.ENTER);

         if(browser=="Firefox")
         {
        	 driver.findElement(By.xpath("//div[@id='vertical-toolbar']/div/ul/li[5]")).click();
         }
        
         markTestStatus("passed", "Success!", driver);
         } 
        
        catch (Exception e) {
        	 markTestStatus("failed", "Some elements failed to load", driver);
              }
         driver.quit();
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }
}
public static void markTestStatus(String status, String reason, WebDriver driver) {
    final JavascriptExecutor jse = (JavascriptExecutor) driver;
   jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \""+ status + "\", \"reason\": \"" + reason + "\"}}");
   }
} 