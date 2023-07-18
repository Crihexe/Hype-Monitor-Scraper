package com.crihexe.japi.request;

import com.crihexe.japi.annotations.BodyParam;
import com.crihexe.japi.annotations.Endpoint;
import com.crihexe.japi.annotations.Method;
import com.crihexe.japi.annotations.Method.Methods;

@Method(method = Methods.PUT)
@Endpoint("/api/scraper/receive")
public class UploadScrapingResultsRequest extends Request {
	
	@BodyParam(keepValue = true)
	public String sneakers;
	
	public UploadScrapingResultsRequest(String sneakers) {
		this.sneakers = sneakers;
	}
	
}
