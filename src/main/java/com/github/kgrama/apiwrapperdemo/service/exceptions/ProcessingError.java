package com.github.kgrama.apiwrapperdemo.service.exceptions;

import java.util.List;

import lombok.Getter;

public class ProcessingError extends RuntimeException {

	private static final long serialVersionUID = 8732737085617871009L;
	
	@Getter
	private final List<Throwable> errorStatus;
	
	public ProcessingError(List<Throwable> err, String message) {
		super(message);
		this.errorStatus = err;
	}
	
	
	
	public ProcessingError(List<Throwable> err) {
		super();
		this.errorStatus = err;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	
	
}
