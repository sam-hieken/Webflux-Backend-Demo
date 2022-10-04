package com.lender.data.pojo.dwolla;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DwollaCustomer {
	private String 	firstName, lastName, email, type, address1, address2, city, state,
					postalCode, dateOfBirth, ssn;
	
	
}
