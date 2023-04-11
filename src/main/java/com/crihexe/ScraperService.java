package com.crihexe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ScraperService {
	
	private HttpClient http;
	private CaptchaResolver captcha;
	
	public ScraperService() throws Exception {
		if(!FirefoxEmulator.isRunning()) throw new Exception("No Firefox Browser running!");
		http = HttpClients.createDefault();
		captcha = new CaptchaResolver();
	}
	
	public String getRequest(ScrapingRequest request) throws Exception {
		String url = request.url + request.path;
		
		FirefoxEmulator.open(url);
		captcha.bypass(true);
		
		String response = FirefoxEmulator.getBody();
		
		System.out.println("RESPONSE = " + response);
		
		
		
		/*if(!response.startsWith("{")) {
			System.err.println("BOT DETECTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.err.println("Waiting 3 secs");
			
			Thread.sleep(3000);
			
			System.err.println("Trying to locate captcha button");
			
			WebElement captchaElement = FirefoxEmulator.findElement(By.cssSelector("#px-captcha"));
			

			
			ImageIO.write(FirefoxEmulator.screenshot(captchaElement), "png", new File("screenshot.png"));
			
//			System.err.println("Waiting 3 secs");
			
			Actions actions = FirefoxEmulator.getActions();
			
			actions
			.clickAndHold(captchaElement)
			;
			
			actions.perform();
			
			System.out.println(FirefoxEmulator.findElement(By.tagName("body")).getText());
			
			while(true) Thread.sleep(20000);
			
//			System.out.println(FirefoxEmulator.findElement(By.tagName("body")).getText());
			
		}*/
		
		return response;
	}
	
	public JSONObject scrapeApi(ScrapingRequest request) throws Exception {
		try {
			ScraperJSONBuilder builder = new ScraperJSONBuilder(getRequest(request));
			for(String s : request.requestingKeys)
				builder.putKey(s);
			
			return builder.build();
		} catch(JSONException e) {
			e.printStackTrace();
			return Utils.generateErrorJSON("Invalid JSON");
		}
	}
	
	public JSONArray scrapeApi(List<ScrapingRequest> requests) throws Exception {
		JSONArray result = new JSONArray();
		
		for(ScrapingRequest r : requests)
			result.put(scrapeApi(r));
		
		return result;
	}

}
