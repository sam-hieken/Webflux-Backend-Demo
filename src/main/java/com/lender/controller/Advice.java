package com.lender.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.lender.data.exception.CustomerNotFoundException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class Advice {
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
	public Mono<Map<String, Object>> handleMultipartException(MaxUploadSizeExceededException e) {
	    return Mono.just(Map.of("Error", "Maximum upload size exceeded."));
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Mono<Map<String, Object>> handleNoCustomerException(CustomerNotFoundException e) {
		
		return Mono.just(Map.of("Error", "Customer does not exist."));
	}
}
