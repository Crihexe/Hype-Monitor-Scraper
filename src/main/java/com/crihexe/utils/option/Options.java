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
