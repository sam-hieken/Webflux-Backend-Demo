package com.lender.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lender.data.pojo.User;
import com.lender.data.pojo.plaid.AccountAccessType;
import com.lender.exception.plaid.BadAccountException;
import com.lender.exception.plaid.BadTokenException;
import com.lender.service.PlaidService;

import reactor.core.publisher.Mono;

@RequestMapping("/plaid")
@RestController
public class PlaidController {
	@Autowired
	private PlaidService 	plaidService;

	// https://sandbox.plaid.com/link/token/create
	@GetMapping("/link-token")
	public Mono<Map<String, Object>> getLinkToken(JwtAuthenticationToken token) {
		final Mono<String> linkToken = plaidService.createLinkToken(User.getUserFromToken(token));

		return linkToken.map(str -> 
			Map.of("linkToken", str)
		);
	}
	
	@GetMapping("/processor-token")
	public Mono<Map<String, Object>> createProcessorToken(JwtAuthenticationToken token) {
		final Mono<String> linkToken = plaidService.createProcessorToken(User.getUserFromToken(token));

		return linkToken.map(str -> 
			Map.of("processorToken", str)
		);
	}

	@GetMapping("/acct")
	public Mono<Map<String, Object>> getBankInfo(JwtAuthenticationToken token,
			@RequestParam(value = "includeNumbers", defaultValue = "false") boolean includeNumbers) {
		final User user = User.getUserFromToken(token);

		return plaidService.getBankInfo(user, AccountAccessType.includeNumbers(includeNumbers));
	}

	@PostMapping("/login")
	public Mono<Void> getAccessToken(JwtAuthenticationToken token,
			@RequestHeader("Public-Token") String publicToken) {

		final User user = User.getUserFromToken(token);

		final Mono<String> accessToken = plaidService.createAccessToken(publicToken);

		return accessToken.flatMap(str -> 
			plaidService.updatePlaidToken(user, str)
		);
	}

	@DeleteMapping("/acct")
	public Mono<Void> deleteAccountInfo(JwtAuthenticationToken token) {

		final User user = User.getUserFromToken(token);

		return plaidService.deleteBankInfo(user);
	}
	
	

	@ExceptionHandler
	public Mono<Map<String, Object>> handleTokenException(BadTokenException e, ServerHttpResponse resp) {
		switch (e.getMessage()) {

			case BadTokenException.TOKEN_INVALID:
				resp.setStatusCode(HttpStatus.BAD_REQUEST);	// 400
				return Mono.just(Map.of("Error", "Bad public token."));
	
			case BadTokenException.TOKEN_MISSING:
				resp.setStatusCode(HttpStatus.NOT_FOUND);	// 404
				return Mono.just(Map.of("Error", "Plaid token missing."));

		}

		resp.setStatusCode(HttpStatus.NOT_IMPLEMENTED);	// 501
		return Mono.just(Map.of("Error", "Unknown token error."));
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)		// 503
	public Mono<Map<String, Object>> handleAccountException(BadAccountException e) {
		return Mono.just(Map.of("Error", "Plaid did not supply numbers for this account."));
	}
}

