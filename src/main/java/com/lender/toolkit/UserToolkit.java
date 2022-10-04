package com.lender.toolkit;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class UserToolkit {
	public static String pullAttributeAsString(JwtAuthenticationToken token, 
			String attribute) {

		return (String) token.getTokenAttributes().get(attribute);
	}

	public static boolean pullAttributeAsBoolean(JwtAuthenticationToken token,
			String attribute) {
		
		return (boolean) token.getTokenAttributes().get(attribute);
	}
}
