package com.lender.data.pojo.plaid;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkTokenCreateRequest {
	public static User newUser(String user) {
		return new User(user);
	}
	
	@Data
	@AllArgsConstructor
	public static class User {
		private String	client_user_id;
	}
	
	private String 		client_name,
						language;
//						redirect_uri;
	
	private String[] 	country_codes,
						products;
	
	private User		user;
	
	
}