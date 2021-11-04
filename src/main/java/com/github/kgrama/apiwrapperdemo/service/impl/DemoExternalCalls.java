package com.github.kgrama.apiwrapperdemo.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kgrama.apiwrapperdemo.service.MakeExternalCalls;
import com.github.kgrama.apiwrapperdemo.service.exceptions.InvalidWrappedAPIResponse;

import reactor.core.publisher.Mono;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DemoExternalCalls implements MakeExternalCalls {
	
	

	@Override
	public Mono<?> forwardRequestForSmallDataset(String baseUrl, Class responseType) {
		var webClientForRequest = WebClient.builder()
				.codecs(configurer -> {
			        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.APPLICATION_JSON));
			        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.APPLICATION_JSON));
			     }).baseUrl(baseUrl).build().get().accept(MediaType.APPLICATION_JSON);
		return webClientForRequest.retrieve()
				.onStatus(HttpStatus::isError, response -> {
					return Mono.error(new InvalidWrappedAPIResponse(response.statusCode()));
				})
				.bodyToMono(responseType);
	}
	
	
	
}
