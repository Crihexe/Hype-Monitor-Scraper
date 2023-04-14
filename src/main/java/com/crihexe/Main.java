package com.crihexe;

import java.awt.Color;
import java.io.File;

import org.json.JSONArray;

import com.crihexe.connector.RepositoryUpdater;
import com.crihexe.firefox.FirefoxEmulator;
import com.crihexe.scraping.ScraperService;
import com.crihexe.utils.option.OptionLoader;
import com.crihexe.utils.option.Options;

public class Main {
	
	private RepositoryUpdater repo;
	private ScraperService scraper;

	public Main() throws Exception {
		System.out.println(new File("./options.json").getAbsolutePath());
		if(!OptionLoader.checkOptionsFile()) throw new Exception("Options file not found. Should be located in ./options.json");
		OptionLoader.load();
		
		repo = new RepositoryUpdater();
		if(!repo.health()) {
			System.err.println("Hype Monitor could be unhealthy. Aborting...");
			//System.exit(4);
		}
		
		if(!Options.autoDetectGeckoDriver) 
			System.setProperty("webdriver.gecko.driver", Options.geckoDriverPath);
		
		FirefoxEmulator.setup();
		FirefoxEmulator.start();
		
		scraper = new ScraperService();
		JSONArray result = scraper.scrapeApi(repo.getScrapingRequests());
		System.out.println(result.toString());
		repo.uploadScrapingResults(result);
		
		FirefoxEmulator.stop();
	}
	
	public static void change(Integer offset) {
		offset++;
	}
	
	public static void main(String[] args) throws Exception {
		new Main();
	}

}
