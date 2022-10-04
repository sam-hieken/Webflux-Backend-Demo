package com.lender.data.exception;

/**
 * Thrown when request to create customer fails from bad info provided. 
 * <br>
 * <b>Result: 400</b>
 * @author Sam
 *
 */
public class CustomerRejectedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CustomerRejectedException(String msg) { super("Customer rejected: " + msg); }
}
