package com.github.kgrama.apiwrapperdemo.service;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.assertj.core.util.Arrays;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kgrama.apiwrapperdemo.service.exceptions.InvalidWrappedAPIResponse;

import lombok.extern.log4j.Log4j2;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;


@Log4j2
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExternalGETCallTest {
	
	@Autowired
	private MockWebServer mockBackend;
	
	@Autowired
	private MakeExternalCalls makeExternalCalls;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	public void beanExists() {
		assertNotNull(makeExternalCalls, "The make external calls service shoud exist");
	}
	
	@Test
	public void httpOKCallsDoNotThrowException () throws JsonProcessingException, InterruptedException {
		log.debug("Verify that standard calls with ok responses work");
		var mapResponse = new HashMap<String, Object>();
		var responseContentType = MediaType.APPLICATION_JSON_VALUE;
		var testPath = "/some-path/";
		initMockResponseData(mapResponse);
		mockBackend.enqueue(initHttpOKMockResponse(mapResponse, responseContentType));
		Mono<?> response = makeExternalCalls.forwardRequestForSmallDataset(String.format("http://localhost:%s%s", mockBackend.getPort(), testPath), String.class);
		assertEquals(mapper.writeValueAsString(mapResponse), response.block());
		validateOutboundRequest(testPath);
	}
	
	@Test()
	public void httpErr400ResponseType () throws JsonProcessingException, InterruptedException {
		log.debug("Verify that status 400 responses are handled");
		var mapResponse = new HashMap<String, Object>();
		var responseContentType = MediaType.APPLICATION_XML_VALUE;
		var testPath = "/some-path/";
		initMockResponseData(mapResponse);
		mockBackend.enqueue(initHttpOKMockResponse(mapResponse, responseContentType).setBody("asdfdasf asdfdasf").setResponseCode(400));
		Mono<?> response = makeExternalCalls.forwardRequestForSmallDataset(String.format("http://localhost:%s%s", mockBackend.getPort(), testPath), String.class);
		assertThrows(InvalidWrappedAPIResponse.class, ()-> response.block());
		validateOutboundRequest(testPath);
	}
	
	@Test()
	public void httpErr500ResponseType () throws JsonProcessingException, InterruptedException {
		log.debug("Verify that status 500 responses are handled");
		var mapResponse = new HashMap<String, Object>();
		var responseContentType = MediaType.APPLICATION_XML_VALUE;
		var testPath = "/some-path/";
		initMockResponseData(mapResponse);
		mockBackend.enqueue(initHttpOKMockResponse(mapResponse, responseContentType).setBody("").setResponseCode(500));
		Mono<?> response = makeExternalCalls.forwardRequestForSmallDataset(String.format("http://localhost:%s%s", mockBackend.getPort(), testPath), String.class);
		assertThrows(InvalidWrappedAPIResponse.class, ()-> response.block());
		validateOutboundRequest(testPath);
	}

	private void validateOutboundRequest(String testPath) throws InterruptedException {
		await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
			RecordedRequest recordedRequest = mockBackend.takeRequest();
			assertEquals("GET", recordedRequest.getMethod());
			assertEquals(testPath, recordedRequest.getPath());
		});
		
	}

	private MockResponse initHttpOKMockResponse(HashMap<String, Object> mapResponse, String responseContent)
			throws JsonProcessingException {
		return new MockResponse().setBody(mapper.writeValueAsString(mapResponse)).addHeader("Content-Type", responseContent).setResponseCode(200);
	}

	private void initMockResponseData(HashMap<String, Object> mapResponse) {
		String[] stringArray = {"one", "two", "three"};
		mapResponse.put("String", "String");
		mapResponse.put("Array", stringArray);
		mapResponse.put("List", Arrays.asList(stringArray));
	}
}
