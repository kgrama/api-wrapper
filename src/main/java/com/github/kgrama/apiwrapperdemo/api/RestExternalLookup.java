package com.github.kgrama.apiwrapperdemo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.kgrama.apiwrapperdemo.model.LookupRequest;
import com.github.kgrama.apiwrapperdemo.model.LookupResponse;
import com.github.kgrama.apiwrapperdemo.service.IdentifyRequestedResource;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class RestExternalLookup implements LookupExternalData {
	
	@Autowired
	private IdentifyRequestedResource requestExternalResource;
	
	@Override
	@GetMapping("/v1/")
	public @ResponseBody LookupResponse lookupExternalURLWithIdentifier(@RequestBody LookupRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
