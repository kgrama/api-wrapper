package com.github.kgrama.apiwrapperdemo.service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.kgrama.apiwrapperdemo.reporting.ApplicationErrorTracker;
import com.github.kgrama.apiwrapperdemo.service.exceptions.ProcessingError;
import com.github.kgrama.apiwrapperdemo.support.MultipartDataTestParent;

import lombok.extern.log4j.Log4j2;

@Log4j2
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class IdentifyRequestedResourceTest extends MultipartDataTestParent {

	private String urlString = "";
	private String  testPath = "/some-path/";

	private String[] validATMIdentifiers = {"LFFFBC11", "LFFADC11", "LFFFBC112"};
	private String[] invalidATMIdentifiers = {"30935500", "30847300"};
	
	private double previous;
	@Value("${processing.wait.time.max:40}") 
	private long maxWaitTime;
	
	@Autowired 
	private ApplicationErrorTracker reporting;
		
	@BeforeEach
	public void initUrlString() {
		urlString = String.format("http://localhost:%s%s", mockBackend.getPort(), testPath);
		previous = reporting.getParseExceptionCounter().count();
	}

	@Autowired
	private IdentifyRequestedResource identifyExternalResource;

	@Test
	public void beanExists() {
		log.debug("Verify test context setup");
		assertNotNull(identifyExternalResource, "The make external calls service shoud exist");
	}

	@Test
	public void verifySlowResponsesResultInError() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that slow responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(maxWaitTime+1, TimeUnit.SECONDS));
		var  exceptionList = new LinkedList<Throwable>();
		verifyInvalidResultsNotCached(exceptionList);
		log.debug("Test that errors are not cached");
		verifyInvalidResultsNotCached(exceptionList);
	}

	@Test
	public void verifyErrorResponsesResultInError() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that status !2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS).setResponseCode(400));
		var  exceptionList = new LinkedList<Throwable>();
		verifyInvalidResultsNotCached(exceptionList);
		expectedERRCount(1);
		log.debug("Test that errors are not cached");
		verifyInvalidResultsNotCached(exceptionList);
	}
	
	@Test
	public void verifyCorrectResourceIdentification() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that status 2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var  exceptionList = new LinkedList<Throwable>();
		var jsonResponse = identifyExternalResource.findRequestedResource(validATMIdentifiers[0], urlString,exceptionList);
		assertTrue(exceptionList.isEmpty());
		assertNotNull(jsonResponse);
		assertFalse(jsonResponse.isEmpty());
		expectedERRCount(0);
		await().atMost(maxWaitTime+5, TimeUnit.SECONDS).untilAsserted(() -> {
			var jsonResponseCached = identifyExternalResource.findRequestedResource(validATMIdentifiers[0], urlString,exceptionList);
			assertNotNull(jsonResponseCached);
		});
	}
	
	@Test
	public void verifyCorrectResourceIdentificationMultiBrand() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that status 2xx responses are handled");
		mockBackend.enqueue(initHttpOKMultiBrandMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var  exceptionList = new LinkedList<Throwable>();
		var jsonResponse = identifyExternalResource.findRequestedResource(validATMIdentifiers[2], urlString,exceptionList);
		assertTrue(exceptionList.isEmpty());
		assertNotNull(jsonResponse);
		assertFalse(jsonResponse.isEmpty());
		expectedERRCount(0);
		await().atMost(maxWaitTime+5, TimeUnit.SECONDS).untilAsserted(() -> {
			var jsonResponseCached = identifyExternalResource.findRequestedResource(validATMIdentifiers[2], urlString,exceptionList);
			assertNotNull(jsonResponseCached);
		});
	}
	
	@Test
	public void verifyCorrectResourceIdentificationButNoResource() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that status 2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var  exceptionList = new LinkedList<Throwable>();
		var jsonResponse = identifyExternalResource.findRequestedResource(invalidATMIdentifiers[0], urlString,exceptionList);
		assertNotNull(jsonResponse);
		assertTrue(jsonResponse.isEmpty());
		expectedERRCount(0);
		log.debug("Test that errors are not cached");
		verifyInvalidResultsNotCached(exceptionList);
	}
	
	
	
	private void verifyInvalidResultsNotCached(LinkedList<Throwable> exceptionList) {
		await().atMost(maxWaitTime+5, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThrows(ProcessingError.class, () -> 
				identifyExternalResource.findRequestedResource(validATMIdentifiers[0], urlString,exceptionList));
		});
	}
	
	private void expectedERRCount(double delta) {
		var after = reporting.getParseExceptionCounter().count();
		assertEquals(previous, after, 0);
	}
}
