package com.crihexe;

import java.io.File;

import org.json.JSONArray;

import com.crihexe.firefox.FirefoxEmulator;
import com.crihexe.manager.RepositoryUpdater;
import com.crihexe.manager.ScrapingManager;
import com.crihexe.scraping.ScraperService;
import com.crihexe.utils.option.OptionLoader;

public class Main {
	
	private RepositoryUpdater repo;
	private ScraperService scraper;
	private ScrapingManager manager;

	public Main() throws Exception {
		System.out.println(new File("./options.json").getAbsolutePath());
		if(!OptionLoader.checkOptionsFile()) throw new Exception("Options file not found. Should be located in ./options.json");
		OptionLoader.load();
		
		repo = new RepositoryUpdater();
		if(!repo.health()) {
			System.err.println("Hype Monitor could be unhealthy. Aborting...");
			//System.exit(4);
		}
		
		FirefoxEmulator.setup();
		FirefoxEmulator.start();
		
		manager = new ScrapingManager(repo);
		
		scraper = new ScraperService();
		JSONArray result = scraper.scrapeApi(repo.getScrapingRequests());
		System.out.println(result.toString());
		repo.uploadScrapingResults(result);
		
		FirefoxEmulator.stop();
	}
	
	public static void main(String[] args) throws Exception {
		new Main();
	}

}
