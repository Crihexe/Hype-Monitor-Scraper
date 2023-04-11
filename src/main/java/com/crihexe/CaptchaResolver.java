package com.crihexe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CaptchaResolver {
	
	public enum CaptchaType {
		NONE,
		PRESS_AND_HOLD,
		CF_CHALLENGE;
		// fare il type in cui non carica la pagina (timeout)
	}
	
	public CaptchaResolver() throws FileNotFoundException {
		setupReports();
	}
	
	public void setupReports() throws FileNotFoundException {
		File dir = new File(Options.captchaReportFolder);
		dir.mkdirs();
		if(!dir.exists()) throw new FileNotFoundException("Can't create report folder. Check the path in the options.json file \"captchaReportFolder\"");
	}
	
	public void reportCaptchaInfos() throws Exception {
		JSONObject obj = new JSONObject();
		
		String screenFilename = Utils.saveImageAsPNG(FirefoxEmulator.screenshot(), Options.captchaReportFolder + "/" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS").format(new Date()));
		
		obj
		.put("url", FirefoxEmulator.getOpenedURL())
		.put("body", FirefoxEmulator.getBody())
		.put("source", FirefoxEmulator.getSource())
		.put("screenshot", new File(screenFilename).getAbsolutePath());
		;
		obj.put("proxy", new JSONObject().put("ip", Options.proxyIp).put("port", Options.proxyPort));
		
		Utils.saveJSONtoFile(obj, Options.captchaReportIndentFactor, new File(screenFilename + "-report.json"));
	}
	
	public CaptchaType detect(boolean json) {
		wait(100);
		
		if(json)
			if(FirefoxEmulator.getBody().startsWith("{"))
				return CaptchaType.NONE;
		
		/*try {	// if there is no captcha element found, it will throws an exception.
			FirefoxEmulator.findElement(By.cssSelector(Options.captchaCssSelector));
			return true;	// returning positive if found. captcha detected
		} catch(Exception e) {}
		
		return false;*/
		
//		System.out.println(FirefoxEmulator.findElement(By.xpath("//div[@class=\"hcaptcha-box\"]")));
		System.out.println(FirefoxEmulator.findElement(By.id(Options.captchaCFCContainerIDSelector)));
		if(FirefoxEmulator.existsElement(By.id(Options.captchaCFCContainerIDSelector))) {
			return CaptchaType.CF_CHALLENGE;
		} else if(FirefoxEmulator.existsElement(By.cssSelector(Options.captchaPAHCssSelector))) {
			return CaptchaType.PRESS_AND_HOLD;
		}
		
		if(json)
			try {
				System.err.println("UNKNOWN CAPTCHA TYPE!!!!! TAKING INFOS AND SCREENSHOT...");
				reportCaptchaInfos();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("CANNOT SAVE SCREENSHOT OF UNKNOWN CAPTCHA TYPE");
			}
		
		return CaptchaType.NONE;
	}
	
	public boolean bypass(boolean json) {
		return bypass(json, 0);
	}
	
	public boolean bypass(boolean json, int tries) {
		if(tries > Options.captchaMaxRetry) return false;
		
		CaptchaType type = detect(json);
		if(type != CaptchaType.NONE) {
			System.err.println(type.name() + " | CAPTCHA DETECTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.err.println("Waiting " + Options.captchaDetectDelayMS + "ms");
			wait(Options.captchaDetectDelayMS);
		}
		
		if(type == CaptchaType.PRESS_AND_HOLD)
			resolvePressAndHold();
		if(type == CaptchaType.CF_CHALLENGE)
			//resolveCloudFlareChallenge();
		
		bypass(json, tries+1);
			
			/*WebElement captchaElement = FirefoxEmulator.findElement(By.cssSelector(Options.captchaCssSelector));
			
			FirefoxEmulator.getActions().clickAndHold(captchaElement).perform();
			
			int counter = 0;
			while(counter++ < 1000) {
				BufferedImage elementImg = screenshot(captchaElement);
				if(elementImg == null) break;
				
				int pixelX = elementImg.getWidth()/2+Options.captchaButtonWidth/2 - 1;
				int pixelY = Options.captchaButtonHeight/2;
				
				int rgb = elementImg.getRGB(pixelX, pixelY);
				
				if(rgb == Options.captchaCompletedColorInt)
					break;
				
				wait(Options.captchaRefreshRateMS);
			}
			
			System.err.println("Releasing button after " + Options.captchaReleaseDelayMS + "ms...");
			wait(Options.captchaReleaseDelayMS);
			
			FirefoxEmulator.getActions().release().perform();
			
			System.err.println("Hopeully resolved. Retry in " + Options.captchaRetryDelayMS + "ms...");
			wait(Options.captchaRetryDelayMS);
			
			bypass(json);*/
		
		return true;
	}
	
	public void resolvePressAndHold() {
		WebElement captchaElement = FirefoxEmulator.findElement(By.cssSelector(Options.captchaPAHCssSelector));
		
		FirefoxEmulator.getActions().clickAndHold(captchaElement).perform();
		
		int counter = 0;
		while(counter++ < 100) {
			BufferedImage elementImg = screenshot(captchaElement);
			if(elementImg == null) break;
			
			int pixelX = elementImg.getWidth()/2+Options.captchaPAHButtonWidth/2 - 1;
			int pixelY = Options.captchaPAHButtonHeight/2;
			
			int rgb = elementImg.getRGB(pixelX, pixelY);
			
			if(rgb == Options.captchaPAHCompletedColorInt)
				break;
			
			wait(Options.captchaPAHRefreshRateMS);
		}
		
		System.err.println("Releasing button after " + Options.captchaPAHReleaseDelayMS + "ms...");
		wait(Options.captchaPAHReleaseDelayMS);
		
		FirefoxEmulator.getActions().release().perform();
		
		System.err.println("Hopeully resolved. Retry in " + Options.captchaPAHRetryDelayMS + "ms...");
		wait(Options.captchaPAHRetryDelayMS);
	}
	
	public void resolveCloudFlareChallenge() {
		WebDriverWait wait = new WebDriverWait(FirefoxEmulator.getDriver(), Duration.ofSeconds(30));
		
		WebElement checkBox;
		
		try {
			// http://zvon.org/comp/r/tut-XPath_1.html
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//div[@class='hcaptcha-box']/iframe")));	// TODO potrebbe switchare a questo frame e non essere capace di continuare
			System.out.println("Frame available");
			try {
				System.out.println(FirefoxEmulator.getBody());
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				System.out.println(FirefoxEmulator.findElement(By.xpath("//label[@class='ctp-checkbox-label']/input[@type='checkbox']")));
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				System.out.println(FirefoxEmulator.getSource());
			} catch(Exception e) {
				e.printStackTrace();
			}
			checkBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@class='ctp-checkbox-label']/input[@type='checkbox']")));
			System.out.println(checkBox);
		} catch(TimeoutException e) {
			e.printStackTrace();
			// TODO TODO gestire nel caso in cui ci metta troppo a caricare. In questo momento se non carica in tempo riprova per le altre "retry" volte mentre lascia che continui a caricarsi
			// le prime opzioni che mi vengono in mente sono:
			// ricaricare
			// provare a cambiare proxy, quindi ricaricare
			try {
				checkBox = FirefoxEmulator.findElement(By.xpath("//label[@class='ctp-checkbox-label']/input[@type='checkbox']"));
				System.out.println(checkBox.getLocation());
				checkBox.click();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
			System.out.println("NON LO SO QUALCOSA E' ANDATO MALE");
			return;
		}
				
		checkBox.click();
		
//		wait(1000000000);
	}
	
	private BufferedImage screenshot(WebElement element) {
		try {
			return Utils.getImage(element.getScreenshotAs(OutputType.BYTES));
		} catch (WebDriverException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FirefoxEmulator.screenshot(element);
	}
	
	private void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {e.printStackTrace();}
	}

}
