package com.crihexe.firefox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;

import com.crihexe.utils.Utils;
import com.crihexe.utils.option.Options;

public class FirefoxEmulator {

	private static FirefoxOptions options = new FirefoxOptions();
	private static WebDriver firefox;

	private static boolean running = false;

	public static void setup() {
		if(!Options.autoDetectGeckoDriver) 
			System.setProperty("webdriver.gecko.driver", Options.geckoDriverPath);
		
		options = createOptions();
	}

	private static FirefoxOptions createOptions() {
		FirefoxOptions options = new FirefoxOptions();
		FirefoxProfile profile = new FirefoxProfile();
		
		//profile.setPreference("intl.accept_languages", Options.language);
		if(Options.usingProxy) {
			System.err.println("USING PROXY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("USING PROXY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			profile.setPreference("network.proxy.type", Options.proxyType);
			profile.setPreference("network.proxy.http", Options.proxyIp);
			profile.setPreference("network.proxy.http_port", Options.proxyPort);
			profile.setPreference("network.proxy.ssl", Options.proxyIp);
			profile.setPreference("network.proxy.ssl_port", Options.proxyPort);
		}
		
		profile.setPreference("devtools.jsonview.enabled", false);
		
		if(Options.headless) options.addArguments("-headless");
		if(Options.usingCustomBinary) options.setBinary(getFirefoxBinary());
		
		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.5563.64 Safari/537.36");
		options.setProfile(profile);
		
		return options;
	}

	public static Actions getActions() {
		return new Actions(firefox);
	}
	
	public static void open(String url) {
		try {
			firefox.get(url);
		} catch(Exception e) {
			e.printStackTrace();
			// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO 
			// gestire quando non carica la page. (tipo riprova cambiando proxy oppure senza proxy oppure boh)
		}
		
	}

	public static String getBody() {
		//System.out.println(firefox.getPageSource());

		String body = firefox.findElement(By.tagName("body")).getText();
//		System.out.println("FIREFOX: " + body);

		return body;
	}
	
	public static <X> X screenshot(OutputType<X> target) {
		return asTS().getScreenshotAs(target);
	}
	
	public static BufferedImage screenshot() throws IOException {
		return Utils.getImage(screenshot(OutputType.BYTES));
	}
	
	public static BufferedImage screenshot(WebElement element) {
		try {
			BufferedImage fullImage = Utils.getImage(screenshot(OutputType.BYTES));
			
			Point point = element.getLocation();
			Dimension size = element.getSize();
			
			BufferedImage elementImage = fullImage.getSubimage(point.getX(), point.getY(), size.getWidth(), size.getHeight());
			
			return elementImage;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static WebElement findElement(By by) throws NoSuchElementException {
		WebElement element = null;
		try {
			element = firefox.findElement(by);
		} catch(Exception e) {}
		return element;
	}
	
	public static boolean existsElement(By by) {
		return findElement(by) != null;
	}
	
	public static String getOpenedURL() {
		return firefox.getCurrentUrl();
	}
	
	public static String getSource() {
		return firefox.getPageSource();
	}

	public static void closeWindow(String handle) {
		String currentHandle;
		for(Iterator<String> it = firefox.getWindowHandles().iterator(); it.hasNext();) {
			currentHandle = it.next();
			if(currentHandle.equalsIgnoreCase(handle)) {
				firefox.switchTo().window(handle).close();
				return;
			}
		}
	}

	public static FirefoxBinary getFirefoxBinary() {
		File pathBinary = new File(Options.firefoxBinary);
		FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);   
		return firefoxBinary;
	}
	
	public static WebDriver getDriver() {
		return firefox;
	}
	
	public static TakesScreenshot asTS() {
		return (TakesScreenshot) firefox;
	}

	public static void start() {
		firefox = new FirefoxDriver(options);
		firefox.get("about:blank");

		running = true;
	}

	public static void stop() {
		running = false;
		firefox.quit();
	}

	public static boolean isRunning() {
		return running;
	}

}
