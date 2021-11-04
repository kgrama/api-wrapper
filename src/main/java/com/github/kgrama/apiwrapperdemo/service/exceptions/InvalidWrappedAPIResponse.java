package com.github.kgrama.apiwrapperdemo.service.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class InvalidWrappedAPIResponse extends RuntimeException {

	private static final long serialVersionUID = 8732737085617871009L;
	
	@Getter
	private final HttpStatus errorStatus;
	
	public InvalidWrappedAPIResponse() {
		super();
		this.errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}
	
	public InvalidWrappedAPIResponse(HttpStatus err) {
		super();
		this.errorStatus = err;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	
	
}
