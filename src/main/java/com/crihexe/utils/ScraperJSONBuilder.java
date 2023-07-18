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
	private JSONArray simpleTargetArr;
	
	private boolean simple;
	
	public ScraperJSONBuilder(String json) throws JSONException {
		this(new JSONObject(json));
	}
	
	public ScraperJSONBuilder(JSONObject json) {
		this.sourceObj = json;
		targetObj = new JSONObject();
	}
	
	public ScraperJSONBuilder setSimpleMode(boolean simple) {
		this.simple = simple;
		simpleTargetArr = new JSONArray();
		return this;
	}
	
	public JSONObject build() {
		System.out.println("SOURCE:"  + sourceObj);
		return targetObj;
	}
	
	public JSONArray buildSimple() {
		return simpleTargetArr;
	}
	
	public void putKey(String path, String as) {
		List<String> keyNames = Arrays.asList(path.split("\\."));
		List<String> asNames = Arrays.asList(as.split("\\."));
		
		recursiveObjPutKeyAs(keyNames, 0, asNames, targetObj, sourceObj);
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
	
	public void putAs(String path, String as) {
		List<String> keyNames = Arrays.asList(path.split("\\."));
		
		Object result = newObjGetKey(keyNames, 0, sourceObj);
		targetObj.put(as, result);
		
		
	}
	
	/*private JSONObject recursiveObjPutKeyAs(List<String> keyNames, int depth, List<String> asNames, JSONObject output, JSONObject input) {
		if(depth >= keyNames.size()) {// TODO ricontrollare TODO TODO TODO TODO TODO soprattutto nel caso degli array un gran bel bordello TODO TODO TODO TODO
			return input;	// oppure "new JSONObject();" oppure "output;"
		}
		
		String key = getKey(keyNames, depth);
		
		Object inputNext = input.get(key);
		Object next = inputNext;
		
		if(inputNext instanceof JSONArray)
			next = recursiveArrPutKeyAs(keyNames, depth, asNames, output, (JSONArray) inputNext);
		else if(inputNext instanceof JSONObject)
			next = recursiveObjPutKeyAs(keyNames, depth+1, asNames, output, (JSONObject) inputNext);
		
		recursiveObjGetAs(asNames, 0, output).put(asNames.get(asNames.size()-1), next);
		
		return output;
	}*/
	
	private JSONObject recursiveObjPutKeyAs(List<String> keyNames, int depth, List<String> asNames, JSONObject output, JSONObject input) {
		if(depth >= keyNames.size()) {// TODO ricontrollare TODO TODO TODO TODO TODO soprattutto nel caso degli array un gran bel bordello TODO TODO TODO TODO
//			if(simple) simpleTargetArr.put(new JSONObject().put(getKey(keyNames, depth-1), input));
			recursiveObjGetAs(asNames, 0, output).put(asNames.get(asNames.size()-1), input.get(getKey(keyNames, depth-1)));
			return input;	// oppure "new JSONObject();" oppure "output;"
		}
		
		String key = getKey(keyNames, depth);
		
		Object inputNext = input.get(key);
		Object next = inputNext;
		
		if(inputNext instanceof JSONArray)
			next = recursiveArrPutKeyAs(keyNames, depth, asNames, new JSONObject(), (JSONArray) inputNext);
		else if(inputNext instanceof JSONObject)
			next = recursiveObjPutKeyAs(keyNames, depth+1, asNames, new JSONObject(), (JSONObject) inputNext);
		
		recursiveObjGetAs(asNames, 0, output).put(asNames.get(asNames.size()-1), next);
		
		return output;
	}
	
	private JSONObject recursiveObjGetAs(List<String> asNames, int depthAs, JSONObject output) {
		if(depthAs >= asNames.size()-1) {
			return output;
		}
		
		String key = getKey(asNames, depthAs);
		
		return recursiveObjGetAs(asNames, depthAs+1, output.has(key) ? output : output.put(key, new JSONObject()));
	}
	
	public Object newObjGetKey(List<String> keyNames, int depth, Object currObj) {
		if(depth == keyNames.size()) return currObj;
//		System.out.println("NEW: depth" + depth);
//		System.out.println("NEW: " + currObj);
		if(currObj instanceof JSONObject) {
			JSONObject currJSONObj = (JSONObject) currObj;
			
			String key = getKey(keyNames, depth);
			
			Object next = currJSONObj.get(key);
			
			return newObjGetKey(keyNames, depth+1, next);
		} else if(currObj instanceof JSONArray) {
			JSONArray currJSONArr = (JSONArray) currObj;
			
			String key = getKey(keyNames, depth);
			
			Pair<Integer, Integer> bounds = getJSONArrayBounds(keyNames.get(depth-1), currJSONArr.length());
			
			if(bounds.second == 1) return newObjGetKey(keyNames, depth, currJSONArr.get(bounds.first));
			
			JSONArray nexts = new JSONArray();
			for(int i = bounds.first, len = bounds.second+bounds.first; i < len; i++) {
				Object next = currJSONArr.get(i);
				nexts.put(newObjGetKey(keyNames, depth, next));
			}
			
			return nexts;
		} else return currObj;
	}
	
	private JSONObject recursiveObjPutKey(List<String> keyNames, int depth, JSONObject output, JSONObject input) {
		if(depth >= keyNames.size()) {// TODO ricontrollare TODO TODO TODO TODO TODO soprattutto nel caso degli array un gran bel bordello TODO TODO TODO TODO
//			if(simple) simpleTargetArr.put(new JSONObject().put(getKey(keyNames, depth-1), input));
			if(simple) simpleTargetArr.put(input);
			return input;	// oppure "new JSONObject();" oppure "output;"
		}
		
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
	
	private JSONArray recursiveArrPutKeyAs(List<String> keyNames, int depth, List<String> asNames, JSONObject output, JSONArray input) {
		String key = getKey(keyNames, depth);
		
		// first = offset   second = length
		Pair<Integer, Integer> bounds = getJSONArrayBounds(keyNames.get(depth), input.length());
		
//		System.out.println("from " + keyNames.get(depth) + " -> " + bounds);
		
		for(int i = bounds.first, len = bounds.second+bounds.first; i < len; i++) {
			Object inputNext = input.get(i);
			Object next = inputNext;
			if(inputNext instanceof JSONObject) {
				JSONObject inputNextObj = (JSONObject) inputNext;
				int used = used(inputNextObj);
				System.out.println("jsonobj used: " + used);
				if(used == -1) inputNextObj.put("hypemonitorscraper_used_index", output.length());
				System.out.println("set usedindex: " + inputNextObj.getInt("hypemonitorscraper_used_index"));
				next = recursiveObjPutKeyAs(keyNames, depth+1, asNames, output, inputNextObj);
			}
			
		}
		
		return new JSONArray();
	}
	
	private JSONArray recursiveArrPutKey(List<String> keyNames, int depth, JSONArray output, JSONArray input) {
		String key = getKey(keyNames, depth);
		
		// first = offset   second = length
		Pair<Integer, Integer> bounds = getJSONArrayBounds(keyNames.get(depth), input.length());
		
//		System.out.println("from " + keyNames.get(depth) + " -> " + bounds);
		
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
