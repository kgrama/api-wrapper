package com.github.kgrama.apiwrapperdemo.service.impl;

import java.net.MalformedURLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kgrama.apiwrapperdemo.service.MakeExternalCalls;
import com.github.kgrama.apiwrapperdemo.service.exceptions.ExternalServiceError;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DemoExternalCalls implements MakeExternalCalls {
	
	
	
	@Override
	public Mono<?> forwardRequestForSmallDataset(String baseUrl, Class responseType) {
		return buildClientAndMakeExternalCall(baseUrl)
				.onStatus(HttpStatus::isError, response -> {
					return Mono.error(new ExternalServiceError(response.statusCode()));
				})
				.bodyToMono(responseType);
	}

	private ResponseSpec buildClientAndMakeExternalCall(String baseUrl) {
		try {
			var webClientForRequest = WebClient.builder()
				.codecs(configurer -> {
			        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.APPLICATION_JSON));
			        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.APPLICATION_JSON));
			     }).baseUrl(baseUrl).build().get();
			return webClientForRequest.retrieve();
		} catch (Exception e) {
			log.error("Core function error {}" , e);
			if (e instanceof MalformedURLException ||e instanceof  IllegalArgumentException) {
				throw new ExternalServiceError(HttpStatus.BAD_REQUEST);
			}
			throw new ExternalServiceError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Flux<?> forwardRequestForComplexDataTypes(String url, Class responseType) {
		return buildClientAndMakeExternalCall(url).bodyToFlux(responseType);
	}
	
	
	
}
