package com.lender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lender.data.pojo.User;
import com.lender.data.repository.pojo.LoanGrant;
import com.lender.service.CustomerService;
import com.lender.service.LoanService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/loans")
public class LoanController {
	@Autowired
	private LoanService 	loanService;
	
	@Autowired
	private CustomerService	customerService;
	
	@PostMapping
	public Mono<LoanGrant> createLoan(JwtAuthenticationToken token) {
		User user = User.getUserFromToken(token);
		
//		LoanGrant grant = new LoanGrant(customer);
		return customerService.getCustomer(user.getUuid())
							.flatMap(customer -> (
								loanService.saveNewLoanGrant(new LoanGrant(customer))));
	}
}