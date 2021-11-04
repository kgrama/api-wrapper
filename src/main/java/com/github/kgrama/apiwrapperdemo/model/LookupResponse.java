package com.github.kgrama.apiwrapperdemo.model;

import org.json.JSONObject;

import lombok.Data;

@Data
public class LookupResponse {
	
	private String identifier;
	private JSONObject externalObject;
}
