package com.lender.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lender.data.exception.CustomerRejectedException;
import com.lender.data.exception.DuplicateKeyException;
import com.lender.data.pojo.CustomerRequest;
import com.lender.data.pojo.User;
import com.lender.data.repository.pojo.Customer;
import com.lender.service.CustomerService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	private CustomerService 	customerService;
	
	@GetMapping
	public Mono<Customer> getCustomer(JwtAuthenticationToken token) {
		final User user = User.getUserFromToken(token);

		return customerService.getCustomer(user.getUuid());
	}
	
	@PostMapping
	public Mono<Customer> newCustomer(JwtAuthenticationToken token, @RequestBody CustomerRequest customer) {
		final User user = User.getUserFromToken(token);

		return customerService.saveNewCustomer(customer, user);
	}
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public Mono<Map<String, Object>> handleDuplicateKeyException(DuplicateKeyException e) {
	    return Mono.just(Map.of("Error", "Customer already exists."));
	}
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public Mono<Map<String, Object>> handleRejectedCustomer(CustomerRejectedException e) {
	    return Mono.just(Map.of("Error", e.getMessage()));
	}
}