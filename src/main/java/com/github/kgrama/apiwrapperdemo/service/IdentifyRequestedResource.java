package com.github.kgrama.apiwrapperdemo.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = {"externals"})
public interface IdentifyRequestedResource {
	String HARD_CODED_OBJ_LOOKUP_KEY = "Identification";
	
	String KEY_FOR_RESOURCES = "data";
	
	String KEY_FOR_SAMPLE = "ATM";
	
	String KEY_FOR_BRAND = "Brand";
	
	String KEY_FOR_BRANDNAME = "BrandName";
	
	@Cacheable( keyGenerator = "urlResourceKeyGen" ,unless = "#result == null || !{#exceptionList.isEmpty()} || #result.isEmpty()")
	List<JSONObject> findRequestedResource(String identifier, String url, List<Throwable> exceptionList); 
}