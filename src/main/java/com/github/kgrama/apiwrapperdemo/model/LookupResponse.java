package com.github.kgrama.apiwrapperdemo.model;

import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupResponse {
	
	private String identifier;
	private JSONObject externalObject;
}
