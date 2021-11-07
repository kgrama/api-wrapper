package com.github.kgrama.apiwrapperdemo.service;

import reactor.core.publisher.Flux;

@SuppressWarnings({"rawtypes"})
public interface MakeExternalCalls {
	String ACCEPT_DATATYPE = "application/prs.openbanking.opendata.v2.2+json";
	
	Flux<?> forwardRequestForComplexDataTypes(String url, Class responseType);
	
}
