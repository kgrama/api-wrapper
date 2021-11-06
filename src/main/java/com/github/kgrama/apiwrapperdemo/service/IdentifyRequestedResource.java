package com.github.kgrama.apiwrapperdemo.service;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

public interface IdentifyRequestedResource {
	String HARD_CODED_OBJ_LOOKUP_KEY = "Identification";
	
	String KEY_FOR_RESOURCES = "data";
	
	String KEY_FOR_SAMPLE = "ATM";
	
	String KEY_FOR_BRAND = "Brand";
	
	String KEY_FOR_BRANDNAME = "BrandName";
	
	List<JSONObject> findRequestedResource(String identifier, String url, LinkedList<Throwable> exceptionList); 
}