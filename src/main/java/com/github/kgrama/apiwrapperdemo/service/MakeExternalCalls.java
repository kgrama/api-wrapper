package com.github.kgrama.apiwrapperdemo.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({"rawtypes"})
public interface MakeExternalCalls {
	String ACCEPT_DATATYPE = "application/prs.openbanking.opendata.v2.2+json";
	
	Flux<?> forwardRequestForComplexDataTypes(String url, Class responseType);
	
	
	Mono<?> forwardRequestForSmallDataset(String url , Class responseType);
	
	
}
