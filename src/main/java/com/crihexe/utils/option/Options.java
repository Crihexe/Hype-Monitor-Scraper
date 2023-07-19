package com.crihexe.utils.option;

import java.io.File;
import java.lang.reflect.Field;

public class Options {
	
	public static final File optionsFile = new File("./options.json");
	
	public static Boolean headless = false;
	
	public static String language = "en-GB";
	
	public static Boolean autoDetectGeckoDriver = true;
	public static String geckoDriverPath = "";
	
	public static Boolean usingCustomBinary = false;
	public static String firefoxBinary = "";
	
	public static String hypeMonitorUrl = "http://dev.crihexe.com:8080";
	public static String hypeMonitorPathHealth = "/api/dev/health";
	public static String hypeMonitorPathScrapingRequests = "/api/scraping/request";
	public static String hypeMonitorPathScrapingReceive = "/api/scraping/receive";
	
	// proxy
	public static Boolean usingProxy = false;
	public static Integer proxyType = 1;
	public static String proxyIp = "127.0.0.1";
	public static Integer proxyPort = 80;
	
	// captcha
	public static Integer captchaMaxRetry = 4;
	public static Integer captchaDetectDelayMS = 3500;
	public static String captchaReportFolder = "reports";
	// captcha PRESS AND HOLD button
	public static String captchaPAHCssSelector = "#px-captcha";
	public static Integer captchaPAHButtonWidth = 253;
	public static Integer captchaPAHButtonHeight = 50;
	public static Integer captchaPAHCompletedColorInt = -14567958;	// new Color(33, 181, 234).getRGB()
	public static Integer captchaPAHRefreshRateMS = 500;
	public static Integer captchaPAHReleaseDelayMS = 850;
	public static Integer captchaPAHRetryDelayMS = 6000;
	// captcha CLOUDFLARE CHALLANGE
	public static String captchaCFCContainerIDSelector = "challenge-stage";
	public static String captchaCFCIDSelector = "hcaptcha-box";
	// captcha report
	public static Integer captchaReportIndentFactor = 2;
	
	// sneaker json keys
	public static String sneakerKey_id = "id";
	public static String sneakerKey_slug = "slug";
	public static String sneakerKey_status = "status";
	public static String sneakerKey_releaseDate = "releaseDate";
	public static String sneakerKey_name = "name";
	public static String sneakerKey_color = "color";
	public static String sneakerKey_colorway = "colorway";
	public static String sneakerKey_retail = "retail";
	public static String sneakerKey_brand = "brand";
	public static String sneakerKey_image = "image";
	public static String sneakerKey_sku = "sku";
	public static String sneakerKey_primaryModel = "primaryModel";
	public static String sneakerKey_secondaryModel = "secondaryModel";
	public static String sneakerKey_gender = "gender";
	public static String sneakerKey_description = "description";
	public static String sneakerKey_highestBid = "highestBid";
	public static String sneakerKey_lowestAsk = "lowestAsk";
	public static String sneakerKey_lastSale = "lastSale";
	public static String sneakerKey_salesLast72 = "salesLast72";
	public static String sneakerKey_averagePrice = "averagePrice";
	public static String sneakerKey_stockxURL = "stockxURL";
	
	// stockx json keys
	public static String stockxSlugKey = "urlKey";
	// stockx json keys marketupdate
	public static String stockxMarketUpdate_retail = "productTraits[0].value";
	public static String stockxMarketUpdate_highestBid = "market.bidAskData.highestBid";
	public static String stockxMarketUpdate_lowestAsk = "market.bidAskData.lowestAsk";
	public static String stockxMarketUpdate_lastSale = "market.salesInformation.lastSale";
	public static String stockxMarketUpdate_salesLast72 = "market.salesInformation.salesThisPeriod";
	public static String stockxMarketUpdate_averagePrice = "market.statistics.last90Days.averagePrice";
	// stockx json keys fullsneaker research
	public static String stockxFullSneakerResearch_releaseDate = "traits[3].value";
	public static String stockxFullSneakerResearch_name = "title";
	public static String stockxFullSneakerResearch_colorway = "traits[1].value";
	public static String stockxFullSneakerResearch_retail = "traits[2].value";
	public static String stockxFullSneakerResearch_brand = "brand";
	public static String stockxFullSneakerResearch_primaryModel = "primaryTitle";
	public static String stockxFullSneakerResearch_secondaryModel = "secondaryTitle";
	public static String stockxFullSneakerResearch_gender = "gender";
	public static String stockxFullSneakerResearch_description = "description";
	public static String stockxFullSneakerResearch_highestBid = "market.bidAskData.highestBid";
	public static String stockxFullSneakerResearch_lowestAsk = "market.bidAskData.lowestAsk";
	public static String stockxFullSneakerResearch_lastSale = "market.salesInformation.lastSale";
	public static String stockxFullSneakerResearch_salesLast72 = "market.salesInformation.salesLast72Hours";
	public static String stockxFullSneakerResearch_media = "media";
	// stockx json paths
	public static String stockxMarketUpdateProductPath = "props.pageProps.req.appContext.states.query.value.queries[99999].state.data.browse.results.edges[*].node";
	public static String stockxFullSneakerResearchProductPath = "props.pageProps.req.appContext.states.query.value.queries[99999].state.data.product";
	
	// upload media
	public static String uploadMediaImagePath = "/";
	public static String uploadMedia360Path = "/360/";
	public static String uploadMediaGalleryPath = "/gallery/";
	public static String uploadMediaNamingFormat = "%02d";
	public static String uploadMediaImageName = "image";
	
	public static void setOption(String optionName, Object value) throws Exception {
		Field[] fields = Options.class.getFields();
		for(Field f : fields)
			if(optionName.equals(f.getName())) {
				System.out.println("Loading option: " + optionName + ":" + value);
				if(f.getType().isAssignableFrom(value.getClass()))
					f.set(null, value);
				else
					System.out.println("Invalid value for this option!");
			}
				
	}
	

}
