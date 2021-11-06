package com.github.kgrama.apiwrapperdemo.api;

import org.springframework.http.ResponseEntity;

import com.github.kgrama.apiwrapperdemo.model.LookupResponse;

public interface LookupExternalData {
	
	String V1_PATH = "v1/";
	public ResponseEntity<LookupResponse> lookupExternalURLWithIdentifier(String base64EncodeURL, String resourceIdentifier);
}
