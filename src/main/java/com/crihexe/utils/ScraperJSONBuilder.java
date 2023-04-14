package com.crihexe.utils;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScraperJSONBuilder {
	
	/*
	 * TODO TODO TODO TODO
	 * 
	 * fare in modo che il richiedente possa scegliere da quale percorso prendere il valore e metterlo in una certa posizione nel json di output
	 * tipo "Product.name as data.nome"
	 * 
	 * e poi fare in modo che una richiesta possa andare a scrapare due endpoint diversi per formare un solo ed unico json di output, e per
	 * ogni endpoint vengono specificato quali chiavi prendere
	 * 
	 * TODO TODO TODO TODO
	 * 
	 */
	
	private static long lastObjID = Long.MIN_VALUE;
	
	private JSONObject sourceObj;
	private JSONObject targetObj;
	
	public ScraperJSONBuilder(String json) throws JSONException {
		this(new JSONObject(json));
	}
	
	public ScraperJSONBuilder(JSONObject json) {
		this.sourceObj = json;
		targetObj = new JSONObject();
	}
	
	public JSONObject build() {
		System.out.println("SOURCE:"  + sourceObj);
		return targetObj;
	}
	
	public void putKey(String path) {
		List<String> keyNames = Arrays.asList(path.split("\\."));
		
		recursiveObjPutKey(keyNames, 0, targetObj, sourceObj);
	}
	
	public String getKey(List<String> keyNames, int depth) {
		return getKey(keyNames.get(depth));
	}
	
	public String getKey(String keyName) {
		if(keyName.contains("[") || keyName.contains("]")) 
			return keyName.substring(0, keyName.indexOf("["));
		return keyName;
	}
	
	private JSONObject recursiveObjPutKey(List<String> keyNames, int depth, JSONObject output, JSONObject input) {
		if(depth >= keyNames.size()) // TODO ricontrollare TODO TODO TODO TODO TODO soprattutto nel caso degli array un gran bel bordello TODO TODO TODO TODO
			return input;	// oppure "new JSONObject();" oppure "output;"
		
		String key = getKey(keyNames, depth);
		
		Object inputNext = input.get(key);
		Object next = inputNext;
		
		if(inputNext instanceof JSONArray)
			next = recursiveArrPutKey(keyNames, depth, output.has(key) ? output.getJSONArray(key) : new JSONArray(), (JSONArray) inputNext);
		else if(inputNext instanceof JSONObject)
			next = recursiveObjPutKey(keyNames, depth+1, output.has(key) ? output.getJSONObject(key) : new JSONObject(), (JSONObject) inputNext);
		
		output.put(key, next);
		
		return output;
	}
	
	private Pair<Integer, Integer> getJSONArrayBounds(String keyName, int maxLength) {
		Pair<Integer, Integer> bounds = new Pair<Integer, Integer>(0, maxLength);
		
		if(!keyName.contains("[") || !keyName.contains("]"))	// se non specificato, ritorna tutto l'array
			return bounds;
		
		String boundsContent = keyName.substring(keyName.indexOf("[")+1, keyName.indexOf("]"));
		if(boundsContent.equals("*"))
			return bounds;
		
		if(boundsContent.contains(":")) {
			String range[] = boundsContent.split(":");
			
			String start = range[0];
			String end = range[1];
			
			if(!start.equals("*")) // else default first bound
				try {
					bounds.first = Integer.parseInt(start);
				} catch(Exception e) {
					e.printStackTrace();
				}
			
			if(!end.equals("*")) // else default second bound
				try {
					bounds.second = Integer.parseInt(end) - bounds.first;
				} catch(Exception e) {
					e.printStackTrace();
				}
			
			if(bounds.first + bounds.second > maxLength) bounds.second = maxLength - bounds.first;
			if(bounds.second < bounds.first) bounds.second = bounds.first;
			
			return bounds;
		}
		
		// single number case
		try {
			bounds.first = Integer.parseInt(boundsContent);	// offset
			if(bounds.first >= maxLength) bounds.first = maxLength-1;
			
			bounds.second = 1;	// length. not set if any exception occurs
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return bounds;
	}
	
	private JSONArray recursiveArrPutKey(List<String> keyNames, int depth, JSONArray output, JSONArray input) {
		String key = getKey(keyNames, depth);
		
		// first = offset   second = length
		Pair<Integer, Integer> bounds = getJSONArrayBounds(keyNames.get(depth), input.length());
		
		System.out.println("from " + keyNames.get(depth) + " -> " + bounds);
		
		for(int i = bounds.first, len = bounds.second+bounds.first; i < len; i++) {
			Object inputNext = input.get(i);
			Object next = inputNext;
			if(inputNext instanceof JSONObject) {
				JSONObject inputNextObj = (JSONObject) inputNext;
				int used = used(inputNextObj);
				System.out.println("jsonobj used: " + used);
				if(used == -1) inputNextObj.put("hypemonitorscraper_used_index", output.length());
				System.out.println("set usedindex: " + inputNextObj.getInt("hypemonitorscraper_used_index"));
				next = recursiveObjPutKey(keyNames, depth+1, used == -1 ? new JSONObject() : output.getJSONObject(used), inputNextObj);
				if(used != -1) output.put(used, next);
				else output.put(next);
			} else if(inputNext instanceof JSONArray) {
				// TODO per ora ritorna l'intero sotto-array
				output.put(next);
			} else output.put(next);
			
		}
		
		return output;
	}
	
	private int used(JSONObject obj) {
		if(obj.has("hypemonitorscraper_used_index")) return obj.getInt("hypemonitorscraper_used_index");
		return -1;
	}

}
