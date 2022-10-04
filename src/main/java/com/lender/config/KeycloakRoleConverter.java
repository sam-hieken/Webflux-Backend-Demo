package com.lender.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Map<String, Object> realm = jwt.getClaim("realm_access");
		
		@SuppressWarnings("unchecked")
		Collection<GrantedAuthority> ret = ((List<String>) realm.get("roles"))
				.stream().map(role -> "ROLE_" + role)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toCollection(HashSet::new));
		
		addScopes(jwt, ret);

		return ret;
	}
	
	public void addScopes(Jwt token, Collection<GrantedAuthority> authorities) {
		String[] scopes = token.getClaim("scope").toString().split(" ");
		
		for (String scope : scopes)
			authorities.add(() -> scope);
	}
}
