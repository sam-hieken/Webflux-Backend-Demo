package com.lender.toolkit;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lender.exception.BadResponseException;

import reactor.core.publisher.Mono;

@Component
public class DwollaToolkit {
	public static final String[]	COUNTRY_CODES = {"US"},
									PRODUCTS = {"auth"};

	public static final String		CLIENT_NAME = "Lender";

	private static final String 	BASE_URL = "https://api-sandbox.dwolla.com/",
									AUTH_BASIC = "[removed]";

	// TODO TESTME
	/**
	 * A new Dwolla WebClient. 
	 * 
	 * @param <T>
	 * @return A Mono, as this method makes an initial request to Dwolla for an access token, then appends it as a default header.
	 */
	public <T> Mono<WebClient> newDwollaClient() {
		WebClient client =  WebClient.builder()
				.baseUrl(BASE_URL)
				.build();
		
		// Request authorization token from Dwolla, then add it to this client's default headers.
		return client.post()
			.uri("/token")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.header("Authorization", "Basic " + AUTH_BASIC)
			.body(BodyInserters.fromFormData("grant_type", "client_credentials"))
			.retrieve()
			.bodyToMono(Map.class)
			.map(map -> (String) map.get("access_token"))
			.map(token -> 
				client.mutate()
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader("Authorization", "Bearer " + token)
					.defaultHeader("Accept", "application/vnd.dwolla.v1.hal+json")
					.build()
			);
	}

	public <R, T> Mono<R> get(String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return get(newDwollaClient(), endpoint, requestBody, requestType, responseType);
	}

	public <R, T> Mono<R> get(Mono<WebClient> client, String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return request(client, HttpMethod.GET, endpoint, requestBody, requestType, responseType);
	}

	public <R, T> Mono<R> post(String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return post(newDwollaClient(), endpoint, requestBody, requestType, responseType);
	}

	public <R, T> Mono<R> post(Mono<WebClient> client, String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return request(client, HttpMethod.POST, endpoint, requestBody, requestType, responseType);
	}

	private <R, T> Mono<R> request(Mono<WebClient> webClient, HttpMethod method, String endpoint, Mono<T> requestBody, Class<T> requestType, Class<R> responseType) {
		return webClient
				.flatMap(client -> {
					return client.method(method)
						.uri(endpoint)
						.body(requestBody, requestType)
						.exchangeToMono(resp -> {
							if (resp.statusCode().isError()) {
								return resp.bodyToMono(Map.class)
										.flatMap(map -> {
											try {
												System.err.println(new ObjectMapper().writeValueAsString(map));
											} catch (JsonProcessingException e) {
												e.printStackTrace();
											}
											
											return Mono.error(new BadResponseException(
												 map.get("message") + "::" + resp.statusCode().value()));
										});
							}
							
							return resp.bodyToMono(responseType);
						});
				});
	}
}