package com.lender.data.exception;

import java.util.UUID;

/**
 * Thrown when customer is not found in database. 
 * <br>
 * <b>Result: 404</b>
 * @author Sam
 *
 */
public class CustomerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CustomerNotFoundException(UUID uuid) { super(uuid.toString()); }
}
