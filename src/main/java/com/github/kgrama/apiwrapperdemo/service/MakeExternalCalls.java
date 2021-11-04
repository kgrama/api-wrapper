package com.github.kgrama.apiwrapperdemo.service;

import reactor.core.publisher.Mono;

public interface MakeExternalCalls {
	
	@SuppressWarnings({"rawtypes"})
	Mono<?> forwardRequestForSmallDataset(String url , Class responseType);
	
	
}
