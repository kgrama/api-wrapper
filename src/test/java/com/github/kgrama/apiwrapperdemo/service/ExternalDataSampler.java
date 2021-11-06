package com.github.kgrama.apiwrapperdemo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@SpringBootTest
public class ExternalDataSampler {
	@Autowired
	private MakeExternalCalls makeExternalCalls;
	
	@Test
	public void fetchSampleData () throws JsonProcessingException, InterruptedException {
		log.debug("Fetch authorativesampledata");
		var testPath = "https://developer.lloydsbank.com/opendata-v2.2#get-atms-2.2";
		Mono<?> response = makeExternalCalls.forwardRequestForSmallDataset(testPath, String.class);
		log.debug("{}", response.block());
	}
}