package com.lender.exception.plaid;

/**
 * Thrown when a token from Plaid is invalid/missing.
 * <br>
 * <b>Result: 404 (MISSING) 400 (INVALID)</b>
 * @author Sam
 *
 */
public class BadTokenException extends RuntimeException {
	
	public static final String 	TOKEN_INVALID = "__INVALID",
								TOKEN_MISSING = "__MISSING";
	
	private static final long serialVersionUID = 1L;
	
	public BadTokenException(String message) { super(message); }
}
