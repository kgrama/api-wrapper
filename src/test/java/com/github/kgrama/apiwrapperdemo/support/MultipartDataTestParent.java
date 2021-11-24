package com.github.kgrama.apiwrapperdemo.support;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class MultipartDataTestParent {

	@Autowired
	protected MockWebServer mockBackend;

	protected void validateOutboundRequest(String testPath) throws InterruptedException {
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			RecordedRequest recordedRequest = mockBackend.takeRequest();
			assertEquals("GET", recordedRequest.getMethod());
			assertEquals(testPath, recordedRequest.getPath());
		});
		
	}

	protected MockResponse initHttpOKMockResponse() throws JsonProcessingException {
		return new MockResponse().setChunkedBody(TestSampleDataConstants.COMPLEX_RESPONSE, TestSampleDataConstants.COMPLEX_RESPONSE.length()/3 ).setResponseCode(200);
	}
	
	protected MockResponse initHttpOKMultiBrandMockResponse() throws JsonProcessingException {
		return new MockResponse().setChunkedBody(TestSampleDataConstants.COMPLEX_RESPONSE_MULTI_BRAND, TestSampleDataConstants.COMPLEX_RESPONSE_MULTI_BRAND.length()/6 ).setResponseCode(200);
	}
}