package com.crihexe.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crihexe.japi.JAPI;
import com.crihexe.japi.exception.Life360Exception;
import com.crihexe.japi.request.GetScrapingRequest;
import com.crihexe.japi.request.UploadScrapingResultsRequest;
import com.crihexe.scraping.model.ScrapingRequest;
import com.crihexe.scraping.model.ScrapingRequestV2;
import com.crihexe.utils.ScraperJSONBuilder;
import com.crihexe.utils.option.Options;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RepositoryUpdater {
	
	private HttpClient http;
	
	private ObjectMapper mapper;
	
	private String healthUrl;
	
	private JAPI japi;
	
	public RepositoryUpdater() {
		http = HttpClients.createDefault();
		mapper = new ObjectMapper();
		healthUrl = Options.hypeMonitorUrl + Options.hypeMonitorPathHealth;
		japi = new JAPI(Options.hypeMonitorUrl);
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
				+ "{\"sku\":\"DV1748-601\",\"exists\":false}"
				+ "]";
		
		try {
			requests = Arrays.asList(mapper.readValue(json, ScrapingRequestV2[].class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return requests;
	}
	
	public List<Long> getScrapingRequestsV3() {
		List<Long> ids = new ArrayList<Long>();
		
		try {
			ids = japi.send(new GetScrapingRequest(), new TypeReference<List<Long>>() {});
		} catch (NullPointerException | JSONException | IllegalArgumentException | IllegalAccessException
				| Life360Exception | JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return ids;
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
	
	public JSONArray prepareUploadJSON(JSONArray result) {
//		result = new JSONArray("[{\"sku\":\"swagSKU\",\"gender\":\"men\",\"description\":\"Nike and Jordan Brand are returning back to the Spider-Verse for their second Spider-Man themed Air Jordan 1, with the release of the Air Jordan 1 High OG Spider-Man Across the Spider-Verse. \\n<br>\\n<br>\\nAlso known as the Next Chapter, the Jordan 1 High OG Spider-Man Across the Spider-Verse is the next iteration of the original Air Jordan 1 Chicago colorway. The limited edition sneakers are constructed using various leathers, suedes, and other premium materials across the uppers. The patterns used across the sneakers are designed to mimic the Miles Morales/Spider-Man animation style. \\n<br>\\n<br>\\nThe Air Jordan 1 High OG Spider-Man Across the Spider-Verse released May 20, 2023, with a retail price of $200. \",\"listingType\":\"STANDARD\",\"media\":{\"smallImageUrl\":\"https://images.stockx.com/images/Air-Jordan-1-High-OG-Spider-Man-Across-the-Spider-Verse-Product.jpg?fit=fill&bg=FFFFFF&w=300&h=214&fm=webp&auto=compress&q=90&dpr=2&trim=color&updated_at=1683568981\",\"thumbUrl\":\"https://images.stockx.com/images/Air-Jordan-1-High-OG-Spider-Man-Across-the-Spider-Verse-Product.jpg?fit=fill&bg=FFFFFF&w=140&h=100&fm=webp&auto=compress&q=90&dpr=2&trim=color&updated_at=1683568981\"},\"variants\":[{\"traits\":{\"size\":\"3.5\"},\"id\":\"9cb03a82-eab1-4f2e-bce8-c1880aa59ec3\",\"favorite\":null},{\"traits\":{\"size\":\"4\"},\"id\":\"8e5cdcba-4676-41f7-9c48-191763fb6415\",\"favorite\":null},{\"traits\":{\"size\":\"4.5\"},\"id\":\"eb84f244-80b1-424f-9faa-656a3222d0ce\",\"favorite\":null},{\"traits\":{\"size\":\"5\"},\"id\":\"342b8ad7-c507-41d8-9f0a-983cf97d6815\",\"favorite\":null},{\"traits\":{\"size\":\"5.5\"},\"id\":\"a3901ddd-02ec-419a-96a3-e5640f16eaf6\",\"favorite\":null},{\"traits\":{\"size\":\"6\"},\"id\":\"9f6faa11-ac62-481a-9b56-35c759c78180\",\"favorite\":null},{\"traits\":{\"size\":\"6.5\"},\"id\":\"c7fff78d-680e-48bc-af55-b826aaa220b4\",\"favorite\":null},{\"traits\":{\"size\":\"7\"},\"id\":\"b6c3090f-5d58-4d28-9f73-9e96604b671c\",\"favorite\":null},{\"traits\":{\"size\":\"7.5\"},\"id\":\"c9515125-c0ff-485d-9f1f-c5abe51456e4\",\"favorite\":null},{\"traits\":{\"size\":\"8\"},\"id\":\"2b8ec962-2a38-472f-94dc-b63bf9bc0e3b\",\"favorite\":null},{\"traits\":{\"size\":\"8.5\"},\"id\":\"ce445cb1-d92d-4ca8-9813-11c0f782a55d\",\"favorite\":null},{\"traits\":{\"size\":\"9\"},\"id\":\"d03855de-8713-46ed-a750-d91498269319\",\"favorite\":null},{\"traits\":{\"size\":\"9.5\"},\"id\":\"140e3a54-3ce1-4005-b902-de784148be52\",\"favorite\":null},{\"traits\":{\"size\":\"10\"},\"id\":\"36c2c53b-6820-4eb7-bb44-08761c8fe072\",\"favorite\":null},{\"traits\":{\"size\":\"10.5\"},\"id\":\"edcad7c0-98c9-4cff-b39d-db5bbe1378e8\",\"favorite\":null},{\"traits\":{\"size\":\"11\"},\"id\":\"cd14f3af-4fa6-4cf9-ba43-d859ba09b375\",\"favorite\":null},{\"traits\":{\"size\":\"11.5\"},\"id\":\"4d00bb2e-3093-45c5-91d4-38034e7bec64\",\"favorite\":null},{\"traits\":{\"size\":\"12\"},\"id\":\"0e103cb5-81ef-4066-8065-fb42915cc305\",\"favorite\":null},{\"traits\":{\"size\":\"12.5\"},\"id\":\"66f5d5c4-f2ef-4f05-b964-1728c334d8a0\",\"favorite\":null},{\"traits\":{\"size\":\"13\"},\"id\":\"efbcc31f-a614-4f53-a357-3165db40e9d4\",\"favorite\":null},{\"traits\":{\"size\":\"14\"},\"id\":\"fceec109-4fdb-478f-8503-0ad614559cc9\",\"favorite\":null},{\"traits\":{\"size\":\"15\"},\"id\":\"5d8bcbea-e880-4ce4-9485-c731ec6bcf9b\",\"favorite\":null},{\"traits\":{\"size\":\"16\"},\"id\":\"997c5a2e-3223-4cb6-899a-b1ecc0314841\",\"favorite\":null},{\"traits\":{\"size\":\"17\"},\"id\":\"eea4ab1c-6462-4bd7-9145-d746fdd2cea5\",\"favorite\":null},{\"traits\":{\"size\":\"18\"},\"id\":\"a9acbf81-73ec-4888-9477-f1af5f5e6cdc\",\"favorite\":null}],\"title\":\"Jordan 1 Retro High OG Spider-Man Across the Spider-Verse\",\"browseVerticals\":[\"sneakers\"],\"urlKey\":\"air-jordan-1-high-og-spider-man-across-the-spider-verse\",\"productCategory\":\"sneakers\",\"market\":{\"salesInformation\":{\"salesThisPeriod\":507,\"salesLastPeriod\":481,\"pricePremium\":0.44,\"lastSaleDate\":\"2023-06-29T18:01:05Z\",\"changeValue\":-10,\"volatility\":0.110817,\"changePercentage\":-0.039121,\"lastSale\":263},\"deadStock\":{\"sold\":23675,\"averagePrice\":249},\"state\":{\"numberOfCustodialAsks\":null},\"bidAskData\":{\"lastHighestBidTime\":\"2023-06-28T15:28:51Z\",\"highestBid\":444,\"lastLowestAskTime\":\"2023-06-29T05:58:19Z\",\"lowestAsk\":195},\"currencyCode\":\"EUR\",\"statistics\":{\"last90Days\":{\"averagePrice\":249}}},\"condition\":\"New\",\"productTraits\":[{\"name\":\"Retail Price\",\"value\":\"200\"},{\"name\":\"Release Date\",\"value\":\"2023-05-20\"}],\"name\":\"Spider-Man Across the Spider-Verse\",\"model\":\"Jordan 1 Retro High OG\",\"id\":\"9d40ce4f-8617-4f6d-ae95-25e49b07e744\",\"brand\":\"Jordan\",\"favorite\":null},{\"gender\":\"men\",\"description\":\"From the school-spirited College Colors Program to the vibrant Nike CO.JP collection, Nike Dunks have seen many colorways since the design\\u2019s inception in 1985. But with each new colorway, the Dunk\\u2019s classic color-blocking has remained in some capacity. Nike put its timeless color-blocking to work with the Nike Dunk Low Retro White Black.\\n<br>\\n<br>\\nThe upper Nike Dunk Low Retro White Black is constructed of white leather with black leather overlays and Swooshes. Classic Nike branding is featured on the nylon tongue, nodding to traditional Dunk design elements. A white midsole and black outsole complete the design.\\n<br>\\n<br>\\nThe Nike Dunk Low Retro White Black released in January of 2021 and retailed for $100.\\n<br>\\n<br>\\nTo shop all Nike Dunks, <a href=\\\"https://stockx.com/nike/dunk\\\">click here.<\\/a>\\n\",\"listingType\":\"STANDARD\",\"media\":{\"smallImageUrl\":\"https://images.stockx.com/images/Nike-Dunk-Low-Retro-White-Black-2021-Product.jpg?fit=fill&bg=FFFFFF&w=300&h=214&fm=webp&auto=compress&q=90&dpr=2&trim=color&updated_at=1633027409\",\"thumbUrl\":\"https://images.stockx.com/images/Nike-Dunk-Low-Retro-White-Black-2021-Product.jpg?fit=fill&bg=FFFFFF&w=140&h=100&fm=webp&auto=compress&q=90&dpr=2&trim=color&updated_at=1633027409\"},\"variants\":[{\"traits\":{\"size\":\"3.5\"},\"id\":\"55da4110-5983-439f-9ae4-d7d054f67723\",\"favorite\":null},{\"traits\":{\"size\":\"4\"},\"id\":\"8f97fc3d-245b-485a-b262-f965ca98bd8e\",\"favorite\":null},{\"traits\":{\"size\":\"4.5\"},\"id\":\"49a38ddb-b69d-4a1e-9f9d-21b0007960e0\",\"favorite\":null},{\"traits\":{\"size\":\"5\"},\"id\":\"e04a5c69-0ab6-4d20-a6b3-13d08436a13c\",\"favorite\":null},{\"traits\":{\"size\":\"5.5\"},\"id\":\"42888997-a1f0-4115-a590-0e51d2efdf01\",\"favorite\":null},{\"traits\":{\"size\":\"6\"},\"id\":\"0009c909-6fa3-4a38-9920-43eaa7a7fe46\",\"favorite\":null},{\"traits\":{\"size\":\"6.5\"},\"id\":\"f6e4995b-0bdf-477f-b757-47b79ffa22b3\",\"favorite\":null},{\"traits\":{\"size\":\"7\"},\"id\":\"95a26e09-0c04-4fdc-b8e0-36342c260871\",\"favorite\":null},{\"traits\":{\"size\":\"7.5\"},\"id\":\"0654bcf7-895c-47b2-baff-9940e34f8556\",\"favorite\":null},{\"traits\":{\"size\":\"8\"},\"id\":\"24f67f42-1da8-4fdd-87da-f491f8875185\",\"favorite\":null},{\"traits\":{\"size\":\"8.5\"},\"id\":\"c9c459e6-0de4-4432-aba5-bcdc09f6b6ee\",\"favorite\":null},{\"traits\":{\"size\":\"9\"},\"id\":\"a2ea632e-b87a-46a1-b35d-739b8050fdf2\",\"favorite\":null},{\"traits\":{\"size\":\"9.5\"},\"id\":\"6ac30e67-8a53-4f5f-b268-d65f57ee1003\",\"favorite\":null},{\"traits\":{\"size\":\"10\"},\"id\":\"65fb1eaf-7e2c-4f96-8f9d-74e93b4a36f4\",\"favorite\":null},{\"traits\":{\"size\":\"10.5\"},\"id\":\"b208e557-0a94-4012-a640-533ce10fee9a\",\"favorite\":null},{\"traits\":{\"size\":\"11\"},\"id\":\"01cac555-be33-4953-9f2d-b2ad8ac6d8d0\",\"favorite\":null},{\"traits\":{\"size\":\"11.5\"},\"id\":\"29b377fd-37b0-4e5f-b751-42264fbbf757\",\"favorite\":null},{\"traits\":{\"size\":\"12\"},\"id\":\"bd020a9c-dee0-44f3-bd6b-6be301107d7d\",\"favorite\":null},{\"traits\":{\"size\":\"12.5\"},\"id\":\"854bf93f-c6f3-47e7-b36c-4e718d62bd58\",\"favorite\":null},{\"traits\":{\"size\":\"13\"},\"id\":\"b9ce0997-236c-4e8c-baff-30c019a12f4e\",\"favorite\":null},{\"traits\":{\"size\":\"14\"},\"id\":\"79d4d09f-baef-4d96-a9a0-4dce4b51b1e6\",\"favorite\":null},{\"traits\":{\"size\":\"15\"},\"id\":\"6bf4f6ab-b992-4915-a7bd-0dd09a762a80\",\"favorite\":null}],\"title\":\"Nike Dunk Low Retro White Black Panda (2021)\",\"browseVerticals\":[\"sneakers\"],\"urlKey\":\"nike-dunk-low-retro-white-black-2021\",\"productCategory\":\"sneakers\",\"market\":{\"salesInformation\":{\"salesThisPeriod\":1556,\"salesLastPeriod\":1721,\"pricePremium\":0.29,\"lastSaleDate\":\"2023-06-29T18:02:02Z\",\"changeValue\":20,\"volatility\":0.064147,\"changePercentage\":0.172945,\"lastSale\":130},\"deadStock\":{\"sold\":241432,\"averagePrice\":155},\"state\":{\"numberOfCustodialAsks\":null},\"bidAskData\":{\"lastHighestBidTime\":\"2023-06-28T00:35:25Z\",\"highestBid\":125,\"lastLowestAskTime\":\"2023-06-16T18:41:36Z\",\"lowestAsk\":123},\"currencyCode\":\"EUR\",\"statistics\":{\"last90Days\":{\"averagePrice\":124}}},\"condition\":\"New\",\"productTraits\":[{\"name\":\"Retail Price\",\"value\":\"110\"},{\"name\":\"Release Date\",\"value\":\"2021-03-10\"}],\"name\":\"White Black Panda (2021)\",\"model\":\"Nike Dunk Low Retro\",\"id\":\"5e6a1e57-1c7d-435a-82bd-5666a13560fe\",\"brand\":\"Nike\",\"favorite\":null},{\"gender\":\"women\",\"description\":\"Travis Scott is back in 2023, following up his previous release of the Air Jordan 1 Retro Low Phantom in 2022, with another Air Jordan 1 Retro Low collaboration. The Jordan 1 Retro low OG SP Travis Scott Olive released exclusively in womens sizing. \\n<br>\\n<br>\\nThe Air Jordan 1 Low OG SP Travis Scott Olive is constructed with white leather and black nubuck uppers. Travis continued to use his signautre reverse style Nike Swoosh, in an olive green colorway. The limited edition sneakers have an aged midsole that sits on top of an olive green outsole. \\n<br>\\n<br>\\nThe women's-exclusive Air Jordan 1 Retro Low OG SP Travis Scott Olive released April 26, 2023, with a retail price of $150. \",\"listingType\":\"STANDARD\",\"media\":{\"smallImageUrl\":\"https://images.stockx.com/images/Air-Jordan-1-Retro-Low-OG-SP-Travis-Scott-Olive-W-Product.jpg?fit=fill&bg=FFFFFF&w=300&h=214&fm=webp&auto=compress&q=90&dpr=2&trim=color&updated_at=1679667531\",\"thumbUrl\":\"https://images.stockx.com/images/Air-Jordan-1-Retro-Low-OG-SP-Travis-Scott-Olive-W-Product.jpg?fit=fill&bg=FFFFFF&w=140&h=100&fm=webp&auto=compress&q=90&dpr=2&trim=color&updated_at=1679667531\"},\"variants\":[{\"traits\":{\"size\":\"5W\"},\"id\":\"64558d27-acb5-4fab-bcb2-239cb61430f0\",\"favorite\":null},{\"traits\":{\"size\":\"5.5W\"},\"id\":\"a26362c9-5ba4-4880-b30d-b0e1eb71c994\",\"favorite\":null},{\"traits\":{\"size\":\"6W\"},\"id\":\"373f6f8d-a570-450c-93f2-c7248509fd9e\",\"favorite\":null},{\"traits\":{\"size\":\"6.5W\"},\"id\":\"e13a2fa8-b527-4ea5-9b14-ef71feba2309\",\"favorite\":null},{\"traits\":{\"size\":\"7W\"},\"id\":\"d3251b88-0b5e-4da9-b5f6-25a8aac8d394\",\"favorite\":null},{\"traits\":{\"size\":\"7.5W\"},\"id\":\"6312f7d2-3e2f-4f50-9547-f830056713fe\",\"favorite\":null},{\"traits\":{\"size\":\"8W\"},\"id\":\"ece3dc91-e3f2-499e-8581-347e203699b1\",\"favorite\":null},{\"traits\":{\"size\":\"8.5W\"},\"id\":\"636876d8-363c-43d7-b3cb-2f67f10b3edc\",\"favorite\":null},{\"traits\":{\"size\":\"9W\"},\"id\":\"d081da45-8f99-49f2-9587-53821484358d\",\"favorite\":null},{\"traits\":{\"size\":\"9.5W\"},\"id\":\"e0d2f639-f449-4d58-bd9b-3859811518ec\",\"favorite\":null},{\"traits\":{\"size\":\"10W\"},\"id\":\"d9632ce2-9f51-4e9b-80b5-75b2fc9537d4\",\"favorite\":null},{\"traits\":{\"size\":\"10.5W\"},\"id\":\"06177609-cb43-4587-8336-559118d26ccb\",\"favorite\":null},{\"traits\":{\"size\":\"11W\"},\"id\":\"2f4abdba-ff53-4d25-9fd2-526e1b48e037\",\"favorite\":null},{\"traits\":{\"size\":\"11.5W\"},\"id\":\"63444c0a-30bb-4fc1-a9d4-a3f921d648ae\",\"favorite\":null},{\"traits\":{\"size\":\"12W\"},\"id\":\"95431a78-60da-4a5e-a175-2168de24fc58\",\"favorite\":null},{\"traits\":{\"size\":\"12.5W\"},\"id\":\"9886a67e-f707-436a-b93c-6af78fcf2e63\",\"favorite\":null},{\"traits\":{\"size\":\"13W\"},\"id\":\"8b54fa1a-8aa8-4ca0-87a7-ad411a03f9c6\",\"favorite\":null},{\"traits\":{\"size\":\"13.5W\"},\"id\":\"4543afc4-138b-4e61-aff5-c7bc8a48afdf\",\"favorite\":null},{\"traits\":{\"size\":\"14W\"},\"id\":\"d02ee688-3a07-4c83-899e-d02e917b504e\",\"favorite\":null},{\"traits\":{\"size\":\"14.5W\"},\"id\":\"0e34679a-3923-410c-a411-51a43774a1a5\",\"favorite\":null},{\"traits\":{\"size\":\"15W\"},\"id\":\"87b06c2a-e9ac-4538-bb4e-1c601705f7e7\",\"favorite\":null},{\"traits\":{\"size\":\"15.5W\"},\"id\":\"fd131713-b336-4afc-8523-08eec32833b2\",\"favorite\":null},{\"traits\":{\"size\":\"16W\"},\"id\":\"74d45174-73f9-40a7-82ed-073f37a4ac83\",\"favorite\":null},{\"traits\":{\"size\":\"16.5W\"},\"id\":\"205b38a1-b76c-4e7e-b9bf-595c7dc01811\",\"favorite\":null}],\"title\":\"Jordan 1 Retro Low OG SP Travis Scott Olive (Women's)\",\"browseVerticals\":[\"sneakers\"],\"urlKey\":\"air-jordan-1-retro-low-og-sp-travis-scott-olive-w\",\"productCategory\":\"sneakers\",\"market\":{\"salesInformation\":{\"salesThisPeriod\":368,\"salesLastPeriod\":204,\"pricePremium\":2.197,\"lastSaleDate\":\"2023-06-29T17:52:00Z\",\"changeValue\":-89,\"volatility\":0.2389,\"changePercentage\":-0.168958,\"lastSale\":438},\"deadStock\":{\"sold\":12063,\"averagePrice\":627},\"state\":{\"numberOfCustodialAsks\":null},\"bidAskData\":{\"lastHighestBidTime\":\"2023-06-29T17:54:51Z\",\"highestBid\":1179,\"lastLowestAskTime\":\"2023-06-28T10:50:52Z\",\"lowestAsk\":369},\"currencyCode\":\"EUR\",\"statistics\":{\"last90Days\":{\"averagePrice\":625}}},\"condition\":\"New\",\"productTraits\":[{\"name\":\"Retail Price\",\"value\":\"150\"},{\"name\":\"Release Date\",\"value\":\"2023-04-26\"}],\"name\":\"Travis Scott Olive (Women's)\",\"model\":\"Jordan 1 Retro Low OG SP\",\"id\":\"b05a5e21-62f6-4001-88bc-4cbcb91bfabe\",\"brand\":\"Jordan\",\"favorite\":null}]");
		
		JSONArray finalArray = new JSONArray();
		JSONObject finalJSON = new JSONObject();
		
		for(int i = 0; i < result.length(); i++) {
			ScraperJSONBuilder jsonBuilder = new ScraperJSONBuilder(result.getJSONObject(i));		
			
			System.out.println("Looking for keys in: " + result.getJSONObject(i));
			
			jsonBuilder.putAs("id", "id");
			jsonBuilder.putAs("slug", "slug");
			jsonBuilder.putAs("sku", "sku");
			jsonBuilder.putAs("status", "status");
			
			jsonBuilder.putAs("productTraits[0].value", "retail");
			jsonBuilder.putAs("market.bidAskData.highestBid", "highestBid");
			jsonBuilder.putAs("market.bidAskData.lowestAsk", "lowestAsk");
			jsonBuilder.putAs("market.salesInformation.lastSale", "lastSale");
			jsonBuilder.putAs("market.salesInformation.salesThisPeriod", "salesLast72");
			jsonBuilder.putAs("market.statistics.last90Days.averagePrice", "averagePrice");
			
			finalArray.put(jsonBuilder.build());
		}
		
//		return finalJSON.put("data", finalArray);
		return finalArray;
	}
	
	public void uploadScrapingResults(JSONArray result) {
		JSONArray json = prepareUploadJSON(result);
		System.out.println("uploading json:" + json);
		
		// send to /api/scraper/receive as array
		try {
			System.out.println(japi.send(new UploadScrapingResultsRequest(json.toString())));
		} catch (NullPointerException | JSONException | IllegalArgumentException | IllegalAccessException
				| Life360Exception e) {
			e.printStackTrace();
		}
		
	}

	public void uploadFullSneakerResearch(JSONObject outputSneaker) {
		System.out.println(new JSONArray().put(outputSneaker).toString());
		try {
			System.out.println(japi.send(new UploadScrapingResultsRequest(new JSONArray().put(outputSneaker).toString())));
		} catch (NullPointerException | JSONException | IllegalArgumentException | IllegalAccessException
				| Life360Exception e) {
			e.printStackTrace();
		}
	}

}
