package com.crihexe.scraping;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.crihexe.firefox.FirefoxEmulator;
import com.crihexe.japi.JAPI;
import com.crihexe.japi.request.GetFullSneakerRequest;
import com.crihexe.japi.response.Sneaker;
import com.crihexe.manager.RepositoryUpdater;
import com.crihexe.scraping.model.ScrapingRequest;
import com.crihexe.scraping.model.ScrapingRequestV2;
import com.crihexe.scraping.scheduling.FullSneakerResearchJob;
import com.crihexe.utils.ScraperJSONBuilder;
import com.crihexe.utils.Utils;
import com.crihexe.utils.option.Options;

public class ScraperService {
	

	private JAPI japi;
	private HttpClient http;
	private CaptchaResolver captcha;
	
	private RepositoryUpdater repo;
	
	private Scheduler scheduler;
	
	private Queue<Sneaker> fullUpdateQueue = new LinkedList<Sneaker>();
	
	public ScraperService(RepositoryUpdater repo) throws Exception {
		if(!FirefoxEmulator.isRunning()) throw new Exception("No Firefox Browser running!");
		http = HttpClients.createDefault();
		captcha = new CaptchaResolver();
		this.repo = repo;
		japi = new JAPI(Options.hypeMonitorUrl);
		
		initScheduler();
	}
	
	public void initScheduler() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();
		
		JobDataMap jdm = new JobDataMap();
		jdm.put("scraper", this);
		jdm.put("queue", fullUpdateQueue);
		
		JobDetail job = newJob(FullSneakerResearchJob.class)
				.withIdentity("job1", "group1")
				.usingJobData(jdm)
				.build();
		
		Trigger trigger = newTrigger()
			    .withIdentity("trigger3", "group1")
			    .withSchedule(simpleSchedule()
			            .withIntervalInHours(2)
			            .repeatForever())
			    .startNow()
			    .build();
		
		scheduler.scheduleJob(job, trigger);
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
	
	public JSONArray scrapeApiV2(List<ScrapingRequestV2> requests) throws Exception {
		
		SearchEngine searchEngine = new SearchEngine(captcha);
		for(int i = 0; i < requests.size(); i++) 
			searchEngine.append(requests.get(i).sku);
		
		JSONObject json = new JSONObject(searchEngine.search());
		
		ScraperJSONBuilder jsonBuilder = new ScraperJSONBuilder(json).setSimpleMode(true);
		jsonBuilder.putKey("props.pageProps.req.appContext.states.query.value.queries[99999].state.data.browse.results.edges[*].node");	// il 99999 � un numero grande che non ci sar� mai nel risultato. per come funziona getJSONArrayBounds() mettendo un numero troppo grande prender� sempre e solo l'ultimo indice
		
		JSONArray simpleJSON = jsonBuilder.buildSimple();
		JSONArray outputJSON = new JSONArray();
		
		for(int i = 0; i < Math.min(simpleJSON.length(), requests.size()); i++) outputJSON.put(simpleJSON.getJSONObject(i).put("sku", requests.get(i).sku));
		
		System.out.println("scraper output: " + outputJSON);
		
		return outputJSON;
	}
	
	public JSONArray scrapeApiV3(List<Long> ids) throws Exception {
		// fai un getfullsneaker a /api/scraper/... (ancora da creare) per ogni index della list e da qui ricavo sku e slug e posso fare tutto
		
		SearchEngine searchEngine = new SearchEngine(captcha);
		
		List<Sneaker> sneakers = new ArrayList<Sneaker>();
		
		for(Long id : ids) {
			Sneaker sneaker = japi.send(new GetFullSneakerRequest(id), Sneaker.class);
			
			boolean needUpdate = sneaker.extractStatusBit(2);	// extract third status bit
			if(!needUpdate) continue;
			boolean fullUpdate = sneaker.extractStatusBit(1); // extract second status bit
			
			//fullUpdate = false;	// TODO OOOOOO QUESTA E' TEMPORANEISSIMA DA TOGLIERE CAZZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
			
			if(fullUpdate) {
				if(fullUpdateQueue.contains(sneaker)) continue;
				fullUpdateQueue.add(sneaker);
			} else {
				searchEngine.append(sneaker.sku);
				sneakers.add(sneaker);
			}
		}
		
		JSONObject json = new JSONObject(searchEngine.search());
		
		ScraperJSONBuilder jsonBuilder = new ScraperJSONBuilder(json).setSimpleMode(true);
		jsonBuilder.putKey("props.pageProps.req.appContext.states.query.value.queries[99999].state.data.browse.results.edges[*].node");	// il 99999 � un numero grande che non ci sar� mai nel risultato. per come funziona getJSONArrayBounds() mettendo un numero troppo grande prender� sempre e solo l'ultimo indice
		
		JSONArray simpleJSON = jsonBuilder.buildSimple();
		JSONArray outputJSON = new JSONArray();
		
		for(int i = 0; i < Math.min(sneakers.size(), simpleJSON.length()); i++) {
			JSONObject sneakerObj = simpleJSON.getJSONObject(i);
			
			String slug = sneakerObj.getString("urlKey");
			boolean found = false;
			for(int j = 0; j < sneakers.size(); j++) {
				Sneaker sneaker = sneakers.get(j);
				if(sneaker.slug.equals(slug)) {
					//sneaker.setStatusBit(false, bitindex); // lo status andrebbe cambiato in teoria siccome con questa operazione stiamo aggiornando i suoi dati. quindi lo status dovrebbe dire che ora i suoi dati sono stati aggiornati e non ha bisogno nuovamente di un aggiornamento o robe del genere. da rivedere perché comunque potrebbe essere che dopoe ssere stato aggiornato solo secondo i dati di mercato debba rimanere invariato lo status per permettere di essere aggiornata di nuovo
					
					sneakerObj.put("id", sneaker.id);
					sneakerObj.put("sku", sneaker.sku);
					sneakerObj.put("slug", sneaker.slug);
					sneakerObj.remove("urlKey");
					sneakerObj.put("status", sneaker.status);
					
					found = true;
					break;
				}
			}
			
			if(found) outputJSON.put(sneakerObj);
		}
		
		return outputJSON;
		
	}

	public void performFullSneakerResearch(Sneaker sneaker) {
		SearchEngine searchEngine = new SearchEngine(captcha);
		
		try {
			String json = searchEngine.search(sneaker);
			
			ScraperJSONBuilder jsonBuilder = new ScraperJSONBuilder(json).setSimpleMode(true);
			jsonBuilder.putKey("props.pageProps.req.appContext.states.query.value.queries[99999].state.data.product");	// il 99999 � un numero grande che non ci sar� mai nel risultato. per come funziona getJSONArrayBounds() mettendo un numero troppo grande prender� sempre e solo l'ultimo indice
			System.out.println("FULL SNEAKER RESPONSE = " + jsonBuilder.buildSimple());
			jsonBuilder = new ScraperJSONBuilder(jsonBuilder.buildSimple().getJSONObject(0));
			
			jsonBuilder.putAs("traits[3].value", "releaseDate");	// se non va è colpa dell'array traits che non è sempre in questo ordine. in questo caso si deve controllare ogni per ogni index dell'array se traits[i].name = a quello che cerco, se si prendo value
			jsonBuilder.putAs("title", "name");
			jsonBuilder.putAs("traits[1].value", "colorway");	// se non va è colpa dell'array traits che non è sempre in questo ordine. in questo caso si deve controllare ogni per ogni index dell'array se traits[i].name = a quello che cerco, se si prendo value
			jsonBuilder.putAs("traits[2].value", "retail");	// se non va è colpa dell'array traits che non è sempre in questo ordine. in questo caso si deve controllare ogni per ogni index dell'array se traits[i].name = a quello che cerco, se si prendo value
			jsonBuilder.putAs("brand", "brand");
			jsonBuilder.putAs("primaryTitle", "primaryModel");
			jsonBuilder.putAs("secondaryTitle", "secondaryModel");
			jsonBuilder.putAs("gender", "gender");
			jsonBuilder.putAs("description", "description");
			jsonBuilder.putAs("market.bidAskData.highestBid", "highestBid");
			jsonBuilder.putAs("market.bidAskData.lowestAsk", "lowestAsk");
			jsonBuilder.putAs("market.salesInformation.lastSale", "lastSale");
			jsonBuilder.putAs("market.salesInformation.salesLast72Hours", "salesLast72");
			
			JSONObject outputSneaker = jsonBuilder.build();
			outputSneaker.put("averagePrice", outputSneaker.get("lastSale"));	// in attesa di un market update
			
			outputSneaker.put("id", sneaker.id);
			outputSneaker.put("sku", sneaker.sku);
			outputSneaker.put("slug", sneaker.slug);
			sneaker.setStatusBit(false, 1);	// setto lo status per richiedere un market update
			sneaker.setStatusBit(true, 7);	// setto lo status per essere pronto
			System.out.println("STATUS: " + sneaker.status);
			outputSneaker.put("status", sneaker.status);
			
			repo.uploadFullSneakerResearch(outputSneaker);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
