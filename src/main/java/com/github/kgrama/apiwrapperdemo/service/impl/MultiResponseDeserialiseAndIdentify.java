package com.github.kgrama.apiwrapperdemo.service.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.github.kgrama.apiwrapperdemo.service.IdentifyRequestedResource;
import com.github.kgrama.apiwrapperdemo.service.MakeExternalCalls;
import com.github.kgrama.apiwrapperdemo.service.exceptions.ExternalServiceError;
import com.github.kgrama.apiwrapperdemo.service.exceptions.ProcessingError;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Service
public class MultiResponseDeserialiseAndIdentify implements IdentifyRequestedResource {
	@Value("${processing.wait.time.max:40}") 
	private long maxWaitTime;

	@Autowired
	private MakeExternalCalls externalCalls;

	@Override
	public List<JSONObject> findRequestedResource(String identifier, String url,List<Throwable> exceptionList) {
		log.debug("Processing for identifier {} from url {}", identifier, url);
		var requestSemaphore = new Semaphore(1);
		var responseData = new LinkedList<DataBuffer>();
		makeExternalCall(responseData, identifier, url, requestSemaphore, exceptionList);
		if (exceptionList.isEmpty()) {
			var dataAsStream = consolidateResponseData(responseData, requestSemaphore, exceptionList);
			log.debug("Finished consolidating data, looking for resource");
			try {
				return findJsonObject(dataAsStream, identifier);
			} catch (Exception e) {
				return Collections.emptyList();
			}
		}
		if (!exceptionList.isEmpty()) {
			throw new ProcessingError(exceptionList);
		}
		return null;
	}

	private List<JSONObject> findJsonObject(InputStream dataAsStream, String identifier) {
		var jsonResponse = new JSONObject(new JSONTokener(dataAsStream));
		var keys = jsonResponse.keySet();
		log.debug("Keys in the response {}", keys);
		if (!keys.contains(IdentifyRequestedResource.KEY_FOR_RESOURCES)) {
			log.debug("Missing response data key");
			throw new ExternalServiceError(HttpStatus.INTERNAL_SERVER_ERROR, "Response from service didnt have data");
		}
		return searchForResourceInData(jsonResponse.getJSONArray(KEY_FOR_RESOURCES), identifier);
	}

	private List<JSONObject> searchForResourceInData(JSONArray jsonArray, String identifier) {
		log.trace("Json {}", jsonArray);
		if (jsonArray.length() == 0) {
			throw new ExternalServiceError(HttpStatus.INTERNAL_SERVER_ERROR, "Response from service didnt have data");
		}
		try {
			if (JSONObject.class.cast(jsonArray.get(0)).has(KEY_FOR_BRAND)) {
				return searchForResourceInData((JSONArray) JSONObject.class.cast(jsonArray.get(0)).get(KEY_FOR_BRAND), identifier);
			}
		} catch (JSONException e) {
			log.trace("{}", e);
		}
		try {
			var jsonResourceArray = jsonArray.query(JSONPointer.builder().append(0).append(KEY_FOR_SAMPLE).build());
			if (jsonResourceArray != null) {
				return identifyRequiredResource(JSONArray.class.cast(jsonResourceArray), identifier);
			}
		} catch (JSONException | ClassCastException e) {
			log.trace("{}", e);
		} 
		return null;
	}

	private List<JSONObject> identifyRequiredResource(JSONArray requestedResources, String identifier) {
		var matchingVals = new LinkedList<JSONObject>();
		requestedResources.forEach((obj)-> {
			log.trace("{}", obj);
			try {
				var objAsJson = (JSONObject) obj;
				if (objAsJson.has(HARD_CODED_OBJ_LOOKUP_KEY) && objAsJson.getString(HARD_CODED_OBJ_LOOKUP_KEY).equals(identifier)) {
					matchingVals.add(objAsJson);
				}
			} catch (Exception e) {
				log.error("Error parsing resource array {}", e);
			}
		});
		return matchingVals;
		
	}

	private InputStream consolidateResponseData(LinkedList<DataBuffer> responseData, Semaphore requestSemaphore, List<Throwable> exceptionList) {
		try {
			if (requestSemaphore.tryAcquire(maxWaitTime, TimeUnit.SECONDS)) {
				var consolidatedData = DataBufferUtils.join(Flux.fromStream(responseData.stream()));
				return consolidatedData.block().asInputStream(true);
			} else {
				log.error("Resources response exceeded wait time of {} {}", maxWaitTime, TimeUnit.SECONDS);
				exceptionList.add(new ExternalServiceError(HttpStatus.FAILED_DEPENDENCY));
			}
		} catch (InterruptedException e) {
			log.error("Processing semaphore failed wait");
			exceptionList.add(new ExternalServiceError(HttpStatus.INTERNAL_SERVER_ERROR));
		}
		return null;
	}

	private void makeExternalCall(List<DataBuffer> responseAccumulator, String identifier, String url, Semaphore semaphore, List<Throwable> exceptionList) {
		var serverResponses = externalCalls.forwardRequestForComplexDataTypes(url, DataBuffer.class);
		serverResponses
		.doOnComplete(() -> {} )
		.doOnSubscribe((subscription)-> {semaphore.tryAcquire();})
		.doFinally((signal) -> {semaphore.release();})
		.onErrorResume((err) -> {
			exceptionList.add(err);
			return Flux.empty();})
		.subscribe((val) -> {
			responseAccumulator.add(DataBuffer.class.cast(val));
		});
	}

}
