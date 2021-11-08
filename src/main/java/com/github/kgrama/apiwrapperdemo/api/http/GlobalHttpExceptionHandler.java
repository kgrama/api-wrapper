package com.github.kgrama.apiwrapperdemo.api.http;

import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.github.kgrama.apiwrapperdemo.reporting.ApplicationErrorTracker;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class GlobalHttpExceptionHandler {
	
	@Autowired 
	private ApplicationErrorTracker reporting;
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {IllegalArgumentException.class})
	public ResponseEntity<ErrorMessage> illegalArgumentFromUser(IllegalArgumentException err, WebRequest request) {
		log.info("Illegal argument exception");
		return new ResponseEntity<ErrorMessage>(new ErrorMessage("Bad request"), HttpStatus.BAD_REQUEST );
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<ErrorMessage> somethingwentWrong(Exception err, WebRequest request) {
		log.error("Unhandled exception {}", err);
		reporting.getUnhandledExceptionCounter().increment();
		return new ResponseEntity<ErrorMessage>(new ErrorMessage("ServerFail!"), HttpStatus.BAD_REQUEST );
	}
}
