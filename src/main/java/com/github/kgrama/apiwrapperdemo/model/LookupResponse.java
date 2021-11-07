package com.github.kgrama.apiwrapperdemo.model;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.kgrama.apiwrapperdemo.config.JSONObjectCustomSerialise;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupResponse {
	
	private String identifier;
	
	@JsonSerialize(using = JSONObjectCustomSerialise.class)
	private JSONObject externalObject;
}
