package com.lender.toolkit;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lender.exception.BadResponseException;

import reactor.core.publisher.Mono;

@Component
public class PlaidToolkit {
	public static final String[]	COUNTRY_CODES = {"US"},
									PRODUCTS = {"auth"};

	public static final String		CLIENT_NAME = "Lender";

	private static final String 	BASE_URL = "https://sandbox.plaid.com/",
									CLIENT_ID = "[removed]",
									SECRET = "[removed]";

	public <T> WebClient newPlaidClient() {
		return WebClient.builder()
				.baseUrl(BASE_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader("PLAID-CLIENT-ID", CLIENT_ID)
				.defaultHeader("PLAID-SECRET", SECRET)
//				.filter(ExchangeFilterFunction.ofRequestProcessor(e -> Mono.just(e)))
				.build();
	}

	public <R, T> Mono<R> get(String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return get(newPlaidClient(), endpoint, requestBody, requestType, responseType);
	}

	public <R, T> Mono<R> get(WebClient client, String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return request(client, HttpMethod.GET, endpoint, requestBody, requestType, responseType);
	}

	public <R, T> Mono<R> post(String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return post(newPlaidClient(), endpoint, requestBody, requestType, responseType);
	}

	public <R, T> Mono<R> post(WebClient client, String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return request(client, HttpMethod.POST, endpoint, requestBody, requestType, responseType);
	}

	private <R, T> Mono<R> request(WebClient client, HttpMethod method, String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return client.method(method)
				.uri(endpoint)
				.body(requestBody, requestType)
				.exchangeToMono(resp -> {
					if (resp.statusCode().isError()) {
						return resp.bodyToMono(Map.class).flatMap(map -> {
							String errCode = (String) map.get("error_code");
							try {
								System.err.println(new ObjectMapper().writeValueAsString(map));
							} catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (errCode == null) errCode = "NONE";
							
							return Mono.error(new BadResponseException(
									endpoint + "::" + resp.statusCode().value() + "::" + errCode
							));
						});
					}
					
					return resp.bodyToMono(responseType);
				});
	}
}