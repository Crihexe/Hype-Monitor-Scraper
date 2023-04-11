package com.crihexe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RepositoryUpdater {
	
	private HttpClient http;
	
	private ObjectMapper mapper;
	
	private String healthUrl;
	
	public RepositoryUpdater() {
		http = HttpClients.createDefault();
		mapper = new ObjectMapper();
		healthUrl = Options.hypeMonitorUrl + Options.hypeMonitorPathHealth;
	}
	
	public HttpResponse getRequest(String url) throws Exception {
		HttpGet get = new HttpGet(url);
		try {
			return http.execute(get);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Can't connect to Hype Monitor (" + url + ")");
		}
	}
	
	public boolean health() throws Exception {
		HttpResponse response = getRequest(healthUrl);
		if(response.getStatusLine().getStatusCode() == 200)
			return true;
		return false;
	}
	
	public List<ScrapingRequest> getScrapingRequests() throws Exception {
		List<ScrapingRequest> requests = new ArrayList<ScrapingRequest>();
		
		/*String json = "["
						+ "{\"url\":\"https://stockx.com/api/products/\","
						+ "\"path\":\"nike-dunk-low-retro-white-black-2021?includes=market&currency=USD&country=US\","
						+ "\"requesting_keys\":[\"Product.id\",\"Product.traits\",\"Product.traits[1].key\",\"Product.media.360[15:*]\",\"Product.traits[0].value\",\"Product.media.has360\"]}"
					+ "]";*/
		String body = "{\"operationName\":\"FetchSalesGraph\",\"variables\":{\"isVariant\":true,\"productId\":\"d43359fe-d118-441a-9f84-a208fbf58cd9\",\"startDate\":\"all\",\"endDate\":\"2023-04-07\",\"intervals\":100,\"currencyCode\":\"USD\"},\"query\":\"query FetchSalesGraph($productId: String!, $currencyCode: CurrencyCode, $intervals: Int, $startDate: String, $endDate: String, $isVariant: Boolean! = false) {\\n  variant(id: $productId) @include(if: $isVariant) {\\n    id\\n    salesChart(\\n      currencyCode: $currencyCode\\n      intervals: $intervals\\n      startDate: $startDate\\n      endDate: $endDate\\n    ) {\\n      ...SalesGraph\\n      __typename\\n    }\\n    __typename\\n  }\\n  product(id: $productId) @skip(if: $isVariant) {\\n    id\\n    title\\n    productCategory\\n    salesChart(\\n      currencyCode: $currencyCode\\n      intervals: $intervals\\n      startDate: $startDate\\n      endDate: $endDate\\n    ) {\\n      ...SalesGraph\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment SalesGraph on SalesChart {\\n  series {\\n    xValue\\n    yValue\\n    __typename\\n  }\\n  __typename\\n}\"}";
		String json = "["
				+ "{\"url\":\"https://stockx.com/api/\","
				+ "\"path\":\"p/e/\","
				+ "\"body\":\"" + body + "\","
				+ "\"requesting_keys\":[\"Product.id\",\"Product.traits\",\"Product.traits[1].key\",\"Product.media.360[15:*]\",\"Product.traits[0].value\",\"Product.media.has360\"]}"
			+ "]";
		System.out.println(body);
		System.out.println(json);
		requests = Arrays.asList(mapper.readValue(json, ScrapingRequest[].class));

		
		/*HttpResponse response = getRequest(healthUrl);
		if(response.getStatusLine().getStatusCode() == 200)
			requests = Arrays.asList(mapper.readValue(EntityUtils.toString(response.getEntity()), ScrapingRequest[].class));
				*/
		return requests;
	}
	
	public void uploadScrapingResults(JSONArray result) {
		
	}

}
