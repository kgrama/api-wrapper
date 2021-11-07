package com.github.kgrama.apiwrapperdemo.api.http;

import java.util.Base64;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.kgrama.apiwrapperdemo.api.LookupExternalData;
import com.github.kgrama.apiwrapperdemo.model.LookupResponse;
import com.github.kgrama.apiwrapperdemo.service.IdentifyRequestedResource;
import com.github.kgrama.apiwrapperdemo.service.exceptions.ExternalServiceError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class RestExternalLookup implements LookupExternalData {
	
	private final Base64.Decoder decoder = Base64.getDecoder();
	
	@Autowired
	private IdentifyRequestedResource requestExternalResource;
	
	@Override
	@Operation(summary = "Get an openabnking resource by its id")
	
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the resource", 
	    content = { @Content(mediaType = "application/json", 
	      schema = @Schema(implementation = LookupResponse.class)) }),
	  @ApiResponse(responseCode = "403", description = "Invalid id/url supplied", 
	    content = @Content), 
	  @ApiResponse(responseCode = "404", description = "Resource not found", 
	    content = @Content) })
	@GetMapping(V1_PATH + "{base64Url}/{identifier}")
	public @ResponseBody ResponseEntity<LookupResponse> lookupExternalURLWithIdentifier(
			@Parameter(description = "External API encoded as URL safe base64 string")  @PathVariable String base64Url, 
			@Parameter(description = "Identifier for the external resource") @PathVariable  String identifier) {
		
		var listOfExceptions = new LinkedList<Throwable>();
		var url = new String(decoder.decode(base64Url));
		log.info("Request for resource {} at url {}", identifier, url);
		var matchingResponses = requestExternalResource.findRequestedResource(identifier, url, listOfExceptions);
		
		if (!listOfExceptions.isEmpty()) {
			if (ExternalServiceError.class.isInstance(listOfExceptions.get(0))) {
				return new ResponseEntity<>(ExternalServiceError.class.cast(listOfExceptions.get(0)).getErrorStatus());
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (matchingResponses.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<LookupResponse>(LookupResponse.builder()
				.identifier(identifier)
				.externalObject(matchingResponses.get(0)).build(),
				HttpStatus.OK); 
	}

}
