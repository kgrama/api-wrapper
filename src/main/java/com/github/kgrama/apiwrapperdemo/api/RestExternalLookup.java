package com.github.kgrama.apiwrapperdemo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.kgrama.apiwrapperdemo.model.LookupRequest;
import com.github.kgrama.apiwrapperdemo.model.LookupResponse;

@RestController
public class RestExternalLookup implements LookupExternalData {

	@Override
	@GetMapping("/")
	public @ResponseBody LookupResponse lookupExternalURLWithIdentifier(@RequestBody LookupRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
