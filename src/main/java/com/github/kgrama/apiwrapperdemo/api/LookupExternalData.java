package com.github.kgrama.apiwrapperdemo.api;

import com.github.kgrama.apiwrapperdemo.model.LookupRequest;
import com.github.kgrama.apiwrapperdemo.model.LookupResponse;

public interface LookupExternalData {
	public LookupResponse lookupExternalURLWithIdentifier(LookupRequest request);
}
