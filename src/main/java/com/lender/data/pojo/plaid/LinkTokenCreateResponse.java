package com.lender.data.pojo.plaid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkTokenCreateResponse {
	
	private String 		link_token,
						expiration,
						request_id;
	
	
}