package com.crihexe;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.crihexe.firefox.FirefoxEmulator;
import com.crihexe.manager.RepositoryUpdater;
import com.crihexe.scraping.ScraperService;
import com.crihexe.scraping.scheduling.ScrapingJob;
import com.crihexe.utils.option.OptionLoader;

public class Main {
	
	private RepositoryUpdater repo;
	private ScraperService scraper;
	
	private Scheduler scheduler;

	public Main() throws Exception {
		System.out.println(new File("./options.json").getAbsolutePath());
		if(!OptionLoader.checkOptionsFile()) throw new Exception("Options file not found. Should be located in ./options.json");
		OptionLoader.load();
		
		repo = new RepositoryUpdater();
		try {
			if(!repo.health()) {
				System.err.println("Hype Monitor could be unhealthy. Aborting...");
				System.exit(4);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		FirefoxEmulator.setup();
		FirefoxEmulator.start();
		
		scraper = new ScraperService(repo);
		
		initScheduler();
		
//		repo.uploadScrapingResults(scraper.scrapeApiV3(repo.getScrapingRequestsV3()));
	}
	
	public void initScheduler() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();
		
		JobDataMap jdm = new JobDataMap();
		jdm.put("repo", repo);
		jdm.put("scraper", scraper);
		
		JobDetail job = newJob(ScrapingJob.class)
				.withIdentity("job2", "group2")
				.usingJobData(jdm)
				.build();
		
		Trigger trigger = newTrigger()
			    .withIdentity("trigger4", "group2")
			    .withSchedule(simpleSchedule()
			            .withIntervalInHours(8)
			            .repeatForever())
			    .startNow()
			    .build();
		
		scheduler.scheduleJob(job, trigger);
	}
	
	public static void main(String[] args) throws Exception {
		new Main();
	}

}
