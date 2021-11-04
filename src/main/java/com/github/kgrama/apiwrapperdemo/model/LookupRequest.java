package com.github.kgrama.apiwrapperdemo.model;

import lombok.Data;

@Data
public class LookupRequest {
	private String url;
	private String identifier;
}
