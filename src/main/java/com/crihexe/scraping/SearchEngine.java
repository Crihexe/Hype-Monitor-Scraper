package com.crihexe.scraping;

import java.util.ArrayList;

import org.openqa.selenium.By;

import com.crihexe.firefox.FirefoxEmulator;

public class SearchEngine {
	
	private static final String SEARCH_URL = "https://stockx.com/search?s=";
	private static final String QUOTES_ENC = "%22";
	private static final String OR_ENC = "%2B";
	
	private ArrayList<String> queries = new ArrayList<String>();
	
	private CaptchaResolver captcha;
	
	public SearchEngine(CaptchaResolver captcha) {
		this.captcha = captcha;
	}
	
	public String search() throws Exception {
		String url = generateSearchQueryURL();
		
		FirefoxEmulator.open(url);
		captcha.bypass(true);
		
		String response = FirefoxEmulator.findElement(By.id("__NEXT_DATA__")).getAttribute("innerHTML");
		
		System.out.println("RESPONSE = " + response);
		
		return response;
	}
	
	public String generateSearchQueryURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(SEARCH_URL);
		for(String query : queries) {
			urlBuilder.append(QUOTES_ENC);
			urlBuilder.append(query);
			urlBuilder.append(QUOTES_ENC);
			urlBuilder.append(OR_ENC);
		}
		return urlBuilder.toString();
	}
	
	public void append(String query) {
		queries.add(query);
	}
	
}
