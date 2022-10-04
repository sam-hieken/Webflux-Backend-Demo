package com.lender.data.pojo;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.lender.toolkit.UserToolkit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
	public static final User getUserFromToken(JwtAuthenticationToken token) {
		final UUID uuid = UUID.fromString(token.getName());
		
		final String email = UserToolkit.pullAttributeAsString(token, "email");
		final String username = UserToolkit.pullAttributeAsString(token, "preferred_username");
		
		final String firstName = UserToolkit.pullAttributeAsString(token, "given_name");
		final String lastName = UserToolkit.pullAttributeAsString(token, "family_name");
		
		final boolean emailVerified = UserToolkit.pullAttributeAsBoolean(token, "email_verified");
		
		return new User(uuid, token.getAuthorities(), email, username, firstName, lastName, emailVerified);
	}

	private final UUID 			uuid;
	
	private final Collection<GrantedAuthority> 
								authorities;
	
	private final String 		email;
	private final String 		username;
	
	private final String 		firstName;
	private final String 		lastName;
	
	private boolean 			emailVerified;

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}
}
