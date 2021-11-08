package com.github.kgrama.apiwrapperdemo.reporting;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;

@Component
public class ApplicationErrorTracker {
	
	@Getter
	private final Counter unhandledExceptionCounter;
	
	@Getter
	private final Counter parseExceptionCounter;

	public ApplicationErrorTracker(MeterRegistry meterRegistry) {
		unhandledExceptionCounter = meterRegistry.counter("unhandled_exceptions");
		parseExceptionCounter = meterRegistry.counter("resource_parse_exceptions");
	}
}
