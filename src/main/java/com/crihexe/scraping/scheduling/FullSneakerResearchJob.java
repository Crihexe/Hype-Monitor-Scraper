package com.crihexe.scraping.scheduling;

import java.util.Queue;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.crihexe.japi.response.Sneaker;
import com.crihexe.scraping.ScraperService;

public class FullSneakerResearchJob implements Job {
	
	public FullSneakerResearchJob() {
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		ScraperService scraper = (ScraperService) data.get("scraper");
		Queue<Sneaker> fullUpdateQueue = (Queue<Sneaker>) data.get("queue");
		
		if(fullUpdateQueue.size() > 0) scraper.performFullSneakerResearch(fullUpdateQueue.poll());
		
	}

}
