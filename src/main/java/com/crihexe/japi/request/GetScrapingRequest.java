package com.crihexe.japi.request;

import com.crihexe.japi.annotations.Endpoint;
import com.crihexe.japi.annotations.Method;
import com.crihexe.japi.annotations.Method.Methods;

@Method(method = Methods.GET)
@Endpoint("/api/scraper/request")
public class GetScrapingRequest extends Request {
	
}
