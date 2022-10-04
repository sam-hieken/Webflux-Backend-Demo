package com.lender.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lender.data.pojo.User;
import com.lender.exception.dwolla.DuplicateCustomerException;
import com.lender.service.CustomerService;
import com.lender.service.DwollaService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dwolla")
public class DwollaController {
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private DwollaService dwollaService;
	
	@PostMapping("/customer")
	public Mono<Map<String, Object>> initCustomer(JwtAuthenticationToken token) {
		final User user = User.getUserFromToken(token);
		
		return customerService.getCustomer(user.getUuid())
				.flatMap(customer -> 
					dwollaService.createNewCustomer(user, customer))
				.map(v -> 
					Map.of("Success", "Dwolla account successfully added."));
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public Mono<Map<String, Object>> handleNoCustomerException(DuplicateCustomerException e) {
		return Mono.just(Map.of("Error", "Dwolla customer already created.",
				"Message", e.getMessage()));
	}
}
