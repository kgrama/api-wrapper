package com.github.kgrama.apiwrapperdemo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupResponse {
	
	private String identifier;
	private String externalObject;
}
