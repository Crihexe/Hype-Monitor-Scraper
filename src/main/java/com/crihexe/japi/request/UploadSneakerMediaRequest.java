package com.crihexe.japi.request;

import com.crihexe.japi.annotations.BodyParam;
import com.crihexe.japi.annotations.Endpoint;
import com.crihexe.japi.annotations.Header;
import com.crihexe.japi.annotations.Method;
import com.crihexe.japi.annotations.Method.Methods;

@Method(method = Methods.POST)
@Endpoint("/api/scraper/media")
public class UploadSneakerMediaRequest extends Request {
	
	@Header
	public String path;
	
	@Header
	public String slug;
	
	@Header
	public String name;
	
	@BodyParam(keepValue = true)
	public byte[] data;

	public UploadSneakerMediaRequest(String path, String slug, String name, byte[] data) {
		this.path = path;
		this.slug = slug;
		this.name = name;
		this.data = data;
	}
	
}
