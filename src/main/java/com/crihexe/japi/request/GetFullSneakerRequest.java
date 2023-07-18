package com.crihexe.japi.request;

import com.crihexe.japi.annotations.Endpoint;
import com.crihexe.japi.annotations.Method;
import com.crihexe.japi.annotations.Method.Methods;
import com.crihexe.japi.annotations.QueryParam;

@Method(method = Methods.GET)
@Endpoint("/api/scraper/sneaker")
public class GetFullSneakerRequest extends Request {
	
	@QueryParam
	public Long id;
	
	public GetFullSneakerRequest(Long id) {
		this.id = id;
	}
	
}
