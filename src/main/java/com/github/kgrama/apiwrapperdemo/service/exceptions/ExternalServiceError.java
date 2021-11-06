package com.github.kgrama.apiwrapperdemo.service.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class ExternalServiceError extends RuntimeException {

	private static final long serialVersionUID = 8732737085617871009L;
	
	@Getter
	private final HttpStatus errorStatus;
	
	public ExternalServiceError(HttpStatus err, String message) {
		super(message);
		this.errorStatus = err;
	}
	
	public ExternalServiceError() {
		super();
		this.errorStatus = HttpStatus.FAILED_DEPENDENCY;
	}
	
	public ExternalServiceError(HttpStatus err) {
		super();
		this.errorStatus = err;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	
	
}
