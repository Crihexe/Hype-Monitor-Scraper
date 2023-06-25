package com.crihexe.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crihexe.scraping.model.ScrapingRequest;
import com.crihexe.scraping.model.ScrapingRequestV2;
import com.crihexe.utils.ScraperJSONBuilder;
import com.crihexe.utils.option.Options;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	public List<ScrapingRequestV2> getScrapingRequestsV2() {
		List<ScrapingRequestV2> requests = new ArrayList<ScrapingRequestV2>();
		
		String json = "["
				+ "{\"sku\":\"DD1391-100\",\"lastMarketUpdate\":\"1686595894581\"},"
				+ "{\"sku\":\"DV1748-601\",\"lastMarketUpdate\":\"1686695894581\"},"
				+ "{\"sku\":\"DZ4137-106\",\"lastMarketUpdate\":\"1686795894581\"}"
				+ "]";
		
		try {
			requests = Arrays.asList(mapper.readValue(json, ScrapingRequestV2[].class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return requests;
	}
	
	public List<ScrapingRequest> getScrapingRequests() {
		List<ScrapingRequest> requests = new ArrayList<ScrapingRequest>();
		
		String json = "["
						+ "{\"url\":\"https://stockx.com/api/products/\","
						+ "\"path\":\"nike-dunk-low-retro-white-black-2021?includes=market&currency=USD&country=US\","
						+ "\"requesting_keys\":[\"Product.id\",\"Product.traits\",\"Product.traits[1].key\",\"Product.media.360[15:*]\",\"Product.traits[0].value\",\"Product.media.has360\"]}"
					+ "]";
		/*String body = "{\"operationName\":\"FetchSalesGraph\",\"variables\":{\"isVariant\":true,\"productId\":\"d43359fe-d118-441a-9f84-a208fbf58cd9\",\"startDate\":\"all\",\"endDate\":\"2023-04-07\",\"intervals\":100,\"currencyCode\":\"USD\"},\"query\":\"query FetchSalesGraph($productId: String!, $currencyCode: CurrencyCode, $intervals: Int, $startDate: String, $endDate: String, $isVariant: Boolean! = false) {\\n  variant(id: $productId) @include(if: $isVariant) {\\n    id\\n    salesChart(\\n      currencyCode: $currencyCode\\n      intervals: $intervals\\n      startDate: $startDate\\n      endDate: $endDate\\n    ) {\\n      ...SalesGraph\\n      __typename\\n    }\\n    __typename\\n  }\\n  product(id: $productId) @skip(if: $isVariant) {\\n    id\\n    title\\n    productCategory\\n    salesChart(\\n      currencyCode: $currencyCode\\n      intervals: $intervals\\n      startDate: $startDate\\n      endDate: $endDate\\n    ) {\\n      ...SalesGraph\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment SalesGraph on SalesChart {\\n  series {\\n    xValue\\n    yValue\\n    __typename\\n  }\\n  __typename\\n}\"}";
		String json = "["
				+ "{\"url\":\"https://stockx.com/api/\","
				+ "\"path\":\"p/e/\","
				+ "\"body\":\"" + body + "\","
				+ "\"requesting_keys\":[\"Product.id\",\"Product.traits\",\"Product.traits[1].key\",\"Product.media.360[15:*]\",\"Product.traits[0].value\",\"Product.media.has360\"]}"
			+ "]";
		System.out.println(body);*/
		System.out.println(json);
		try {
			requests = Arrays.asList(mapper.readValue(json, ScrapingRequest[].class));
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		/*HttpResponse response = getRequest(healthUrl);
		if(response.getStatusLine().getStatusCode() == 200)
			requests = Arrays.asList(mapper.readValue(EntityUtils.toString(response.getEntity()), ScrapingRequest[].class));
				*/
		return requests;
	}
	
	public JSONObject prepareUploadJSON(JSONArray result) {
		ScraperJSONBuilder jsonBuilder = new ScraperJSONBuilder(new JSONObject().put("data", result));
		jsonBuilder.putKey("data[*].productTraits[0].value", "price");
		jsonBuilder.putKey("data[*].market.bidAskData.highestBid", "highest_bid");
		jsonBuilder.putKey("data[*].market.bidAskData.lowestAsk", "lowest_ask");
		jsonBuilder.putKey("data[*].market.salesInformation.lastSale", "last_sale");
		jsonBuilder.putKey("data[*].market.salesInformation.salesThisPeriod", "sales_last_72");
		jsonBuilder.putKey("data[*].market.statistics.last90Days.averagePrice", "averageDeadstockPrice");
		
		
		return jsonBuilder.build();
	}
	
	public void uploadScrapingResults(JSONArray result) {
		JSONObject json = prepareUploadJSON(result);
		System.out.println("uploading json:" + json);
	}

}
