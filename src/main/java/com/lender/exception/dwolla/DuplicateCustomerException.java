package com.lender.exception.dwolla;

/**
 * Thrown when a customer was trying to be created on Dwolla but already exists.
 * <br>
 * <b>Result: 409</b>
 * @author Sam
 *
 */
public class DuplicateCustomerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateCustomerException(String msg) { super(msg); }
}
