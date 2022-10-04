package com.lender.data.pojo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * A pojo for the request body when creating a customer
 * @author Sam
 *
 */
public class CustomerRequest {
	private Short	last4SSN;
	
	private String	addressLine1,
					addressLine2 = "";
	
	private String	city,
					state;
	
	private int		zipCode;
	
	private String	phoneNumber;
	
	private Date	birthday;
}
