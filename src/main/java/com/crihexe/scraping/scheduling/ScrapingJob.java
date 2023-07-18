package com.crihexe.scraping.scheduling;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.crihexe.manager.RepositoryUpdater;
import com.crihexe.scraping.ScraperService;

public class ScrapingJob implements Job {
	
	public ScrapingJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		ScraperService scraper = (ScraperService) data.get("scraper");
		RepositoryUpdater repo = (RepositoryUpdater) data.get("repo");
		
		try {
			repo.uploadScrapingResults(scraper.scrapeApiV3(repo.getScrapingRequestsV3()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
