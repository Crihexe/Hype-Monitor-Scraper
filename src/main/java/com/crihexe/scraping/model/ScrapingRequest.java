package com.crihexe.scraping.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScrapingRequest {
	
	public String url;
	public String path;
	public String body;
	
	@JsonProperty("requesting_keys")
	public List<String> requestingKeys;
	
}
