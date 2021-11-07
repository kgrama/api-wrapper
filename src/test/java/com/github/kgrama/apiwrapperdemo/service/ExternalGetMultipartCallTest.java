package com.github.kgrama.apiwrapperdemo.service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.kgrama.apiwrapperdemo.support.MultipartDataTestParent;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ExternalGetMultipartCallTest extends MultipartDataTestParent {
	
	@Autowired
	private MakeExternalCalls makeExternalCalls;
	
	@Value("${processing.wait.time.max:40}") 
	private long maxWaitTime;

	private String urlString = "";
	private String  testPath = "/some-path/";
	
	@BeforeEach
	public void initUrlString() {
		urlString = String.format("http://localhost:%s%s", mockBackend.getPort(), testPath);
	}
	
	@Test
	public void beanExists() {
		log.debug("Verify test context setup");
		assertNotNull(makeExternalCalls, "The make external calls service shoud exist");
		assertNotNull(mockBackend, "The mockbackend should exist");
	}
	
	@Test
	public void verifyMultipartResponseReception() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that status 200 responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var semaphore = new Semaphore(1);
		var serverResponses = makeExternalCalls.forwardRequestForComplexDataTypes(urlString, DataBuffer.class);
		var responseAccumulator = new LinkedList<DataBuffer>();
		assertDoesNotThrow( () -> 
			serverResponses
				.doOnComplete(() -> {} ))
				.doOnSubscribe((subscription)-> {semaphore.tryAcquire();})
				.doFinally((signal) -> {semaphore.release();})
				.onErrorResume((err) ->  Flux.error(new RuntimeException("blah")))
				.subscribe((val) -> {
					responseAccumulator.add(DataBuffer.class.cast(val));
					});
		assertFalse(semaphore.availablePermits() > 0);
		await().atMost(maxWaitTime + 5 , TimeUnit.SECONDS).untilAsserted(() -> {
			assertTrue(semaphore.availablePermits() > 0);
		});
		
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			assertEquals(5,responseAccumulator.size() , "Five chunks expected");
		});
		validateOutboundRequest(testPath);
	}
	
	@Test
	public void verifyMultipartErrorResponseReception() throws InterruptedException, JsonProcessingException {
		log.debug("Verify that status 400 responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBody("dsf asd fdas fds ").setBodyDelay(2, TimeUnit.SECONDS).setResponseCode(400));
		var semaphore = new Semaphore(1);
		var serverResponses = makeExternalCalls.forwardRequestForComplexDataTypes(urlString, DataBuffer.class);
		var responseAccumulator = new LinkedList<DataBuffer>();
		assertDoesNotThrow( () -> 
			serverResponses
				.doOnComplete(() -> {} ))
				.doOnSubscribe((subscription)-> {semaphore.tryAcquire();})
				.doFinally((signal) -> {semaphore.release();})
				.onErrorResume((err) ->  Flux.empty())
				.subscribe((val) -> {
						fail("No valid data transmitted");
					});
		assertFalse(semaphore.availablePermits() > 0);
		await().atMost(maxWaitTime + 5, TimeUnit.SECONDS).untilAsserted(() -> {
			assertTrue(semaphore.availablePermits() > 0);
		});
		
		await().atMost(maxWaitTime + 5, TimeUnit.SECONDS).untilAsserted(() -> {
			assertEquals(0,responseAccumulator.size() , "No chunks expected");
		});
		validateOutboundRequest(testPath);
	}
}
