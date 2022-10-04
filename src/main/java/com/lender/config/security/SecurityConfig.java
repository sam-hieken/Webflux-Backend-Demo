package com.lender.config.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import com.lender.config.KeycloakRoleConverter;

import reactor.core.publisher.Mono;

/*
 *	XXX Set SameSite=Strict on Keycloak cookies
 */

@Configuration
public class SecurityConfig {
	/**
	 * True to run in Spring Security debug mode, never set to true in production
	 */
	public static final boolean		DEBUG = true;
	
	private static final List<String> 	
			ALLOWED_ORIGINS = Arrays.asList(
				"http://localhost", "http://172.22.112.134"
			);
	
	private static final String[] 
			ENDPOINT_AUTHENTICATED = {
				"/loans/**", "/customer/**", "/plaid/**", "/dwolla/**"
			},
			ENDPOINT_PERMIT_ALL = {
				"/home/**", "/test/**"
			};
	
	@Bean
	public Converter<Jwt, Mono<AbstractAuthenticationToken>>  jwtConverter() {
		final var converter = new JwtAuthenticationConverter();

		converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
		return new ReactiveJwtAuthenticationConverterAdapter(converter);
	}
	
	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {	
		http
			.cors().configurationSource(corsConfig()).and()
			.csrf().disable()
			.authorizeExchange()
				.pathMatchers(ENDPOINT_PERMIT_ALL).permitAll()
				.pathMatchers(ENDPOINT_AUTHENTICATED).authenticated()
			.and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtConverter());
		
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfig() {
		return (exchange) -> {
			CorsConfiguration config = new CorsConfiguration();

			config.setAllowedOrigins(ALLOWED_ORIGINS);
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Collections.singletonList("Authorization"));
            config.setMaxAge(3600L);
            return config;
		};
    }
}
