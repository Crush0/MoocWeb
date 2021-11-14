package cn.edu.just.moocweb.utils;

import cn.edu.just.moocweb.exception.ServiceException;
import cn.edu.just.moocweb.exception.UserException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.output.NullOutputStream;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.logging.*;

@Component
public class MoocUtils {
    private static final String moocURL = "https://www.icourse163.org/";
    private static final String moocLoginURL = "https://www.icourse163.org/member/login.htm#/webLoginIndex";
    private static final ChromeOptions options;
    private static final ChromeDriverService chromeDriverService;
    private static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

    private static final boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
    static{
        if(isLinux)
            System.setProperty("webdriver.chrome.driver","/usr/local/bin/chromedriver");
        else if(isWin)
            System.setProperty("webdriver.chrome.driver","E:\\tools\\chromedriver.exe");
        Logger.getLogger("o.o.selenium.remote.ProtocolHandshake").setLevel(Level.OFF);
        DriverService.Builder<ChromeDriverService, ChromeDriverService.Builder> serviceBuilder = new ChromeDriverService.Builder();
        chromeDriverService = serviceBuilder.build();
        chromeDriverService.sendOutputTo(NullOutputStream.NULL_OUTPUT_STREAM);
        options = new ChromeOptions();
        options.addArguments("blink-settings=imagesEnabled=false");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setHeadless(true);
    }
    private static String bindMooc(String username, String password, Integer type) {
        try {
            WebDriver driver = new ChromeDriver(chromeDriverService,options);
            WebDriverWait wait = new WebDriverWait(driver, 20);
            driver.get(moocLoginURL);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[4]/div[2]/div/div/div/div/div/div/div/div/div[2]/span")));
            driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/div/div/div/div/div/div/div/div[2]/span")).click();
            switch (type){
                case 0:{login_0(driver,username,password);break;}
                case 1:{login_1(driver,username,password);break;}
                case 2:{login_2(driver,username,password);break;}
            }
            if(driver.findElements(By.cssSelector(".ferrorhead[id]")).size()!=0){
                throw new ServiceException(ErrCode.BIND_ERROR,driver.findElement(By.cssSelector(".ferrorhead[id]")).getText());
            }
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#my-img")));
            }
            catch (Exception ignored){driver.quit();return null;}
            try{
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".j-teacher-main")));
                try {
                    driver.findElement(By.cssSelector(".j-teacher-main[style='display:none']"));
                    throw new UserException(ErrCode.BIND_ERROR,"不是教师账户，不能绑定");
                }
                catch (Exception ignored){}
            }
            catch (Exception e){
                driver.quit();
                throw new UserException(ErrCode.BIND_ERROR,"登录失败");
            }
            Set<Cookie> ret = driver.manage().getCookies();
            driver.quit();
            return JSON.toJSONString(ret);
        }
        catch (Exception ex){
            if(ex instanceof UserException){
                throw ex;
            }
            ex.printStackTrace();
            return null;
        }
    }

    private static void login_0(WebDriver driver,String username,String password){
        driver.switchTo().frame(0);
        driver.findElement(By.cssSelector(".inputbox input[tabindex='1']")).sendKeys(username);
        driver.findElement(By.cssSelector(".inputbox input[tabindex='2']")).sendKeys(password);
        driver.findElement(By.cssSelector("#dologin")).click();
        driver.switchTo().defaultContent();
    }

    private static void login_1(WebDriver driver,String username,String password){
        driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/div/div/div/div/div/div/div/div/div[1]/div/div[1]/div[1]/ul/li[2]")).click();
        driver.switchTo().frame(1);
        driver.findElement(By.cssSelector(".inputbox input[tabindex='1']")).sendKeys(username);
        driver.findElement(By.cssSelector(".inputbox input[tabindex='2']")).sendKeys(password);
        driver.findElement(By.cssSelector("#submitBtn")).click();
        driver.switchTo().defaultContent();
    }

    private static void login_2(WebDriver driver,String username,String password){
        driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/div/div/div/div/div/div/div/div/div[1]/div/div[1]/div[1]/ul/li[3]")).click();
        driver.findElement(By.cssSelector("input[placeholder='  帐号']")).sendKeys(username);
        driver.findElement(By.cssSelector("input[placeholder='  密码']")).sendKeys(password);
        driver.findElement(By.cssSelector("span.submit-button")).click();
    }

    public static boolean canLogin(String cookiesStr){
        WebDriver driver = ChromeDriver.builder().addAlternative(options).build();
        WebDriverWait wait = new WebDriverWait(driver, 20);
        try {
            driver.get(moocLoginURL);
            toCookieSet(cookiesStr).forEach(item -> driver.manage().addCookie(item));
            driver.get(moocURL);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#my-img")));
            driver.quit();
            return true;
        }
        catch (Exception e){
            driver.quit();
            return false;
        }
    }

    private static Set<Cookie> toCookieSet(String cookieStr){
            return JSON.parseObject(cookieStr, new TypeReference<Set<Cookie>>(){});
    }

    public static String getCookie(String username,String password,Integer type) {
        int count = 0;
        String str = null;
        while (count++ < 5) {
            str = bindMooc(username, password, type);
            if (str != null)
                break;
        }
        return str;
    }
}
