package com.crihexe.scraping;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crihexe.firefox.FirefoxEmulator;
import com.crihexe.scraping.model.ScrapingRequest;
import com.crihexe.scraping.model.ScrapingRequestV2;
import com.crihexe.utils.ScraperJSONBuilder;
import com.crihexe.utils.Utils;

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
	
	public JSONArray scrapeApiV2(List<ScrapingRequestV2> requests) throws Exception {
		
		SearchEngine searchEngine = new SearchEngine(captcha);
		for(int i = 0; i < requests.size(); i++) 
			searchEngine.append(requests.get(i).sku);
		
		JSONObject json = new JSONObject(searchEngine.search());
		
		ScraperJSONBuilder jsonBuilder = new ScraperJSONBuilder(json).setSimpleMode(true);
		jsonBuilder.putKey("props.pageProps.req.appContext.states.query.value.queries[99999].state.data.browse.results.edges[*].node");	// il 99999 è un numero grande che non ci sarà mai nel risultato. per come funziona getJSONArrayBounds() mettendo un numero troppo grande prenderà sempre e solo l'ultimo indice
		
		JSONArray simpleJSON = jsonBuilder.buildSimple();
		
		System.out.println("scraper output: " + simpleJSON);
		
		return simpleJSON;
	}

}
