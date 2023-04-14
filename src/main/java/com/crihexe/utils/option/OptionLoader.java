package com.crihexe.utils.option;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OptionLoader {
	
	public static boolean checkOptionsFile() {
		return Options.optionsFile.exists();
	}
	
	public static void load() throws IOException, JSONException {
		JSONObject obj = new JSONObject(FileUtils.readFileToString(Options.optionsFile, Charset.defaultCharset()));
		recursiveTraverse("", obj);
	}
	
	private static void recursiveTraverse(String previousKey, JSONObject currentObject) {
	    //iterate each key
	    for (String currentKey : currentObject.keySet()) {
	      //build the next key
	      String nextKey = previousKey == null || previousKey.isEmpty() ? currentKey : previousKey + "-" + currentKey;
	      Object value = currentObject.get(currentKey);
	      if (value instanceof JSONObject) {
	        //if current value is object, call recursively with next key and value
	        recursiveTraverse(nextKey, (JSONObject) value);
	      } else if (value instanceof JSONArray) {
	        //if current value is array, iterate it
	        JSONArray array = (JSONArray) value;
	        for (int i = 0; i < array.length(); i++) {
	          Object object = array.get(i);
	          JSONObject jsonObject = (JSONObject) object;
	          //assuming current array member is object, call recursively with next key + index and current member
	          recursiveTraverse(nextKey + "-" + i, jsonObject);
	          //you might need to handle special case of current member being array
	        }
	      } else {
	        //value is neither object, nor array, so we print and this ends the recursion
	        System.out.println("Trying to set {\"" + nextKey + "\": \"" + value + "\"}");
	        try {
				Options.setOption(nextKey, value);
			} catch (Exception e) {
				System.err.println("Error on " + nextKey + " key. Invalid \"" + value + "\" value: " + e.getMessage());
			}
	      }
	    }
	  }

}
