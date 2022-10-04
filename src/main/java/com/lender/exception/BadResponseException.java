package com.lender.exception;

/**
 * Thrown when a third party service returns a bad response (400+)
 * @author Sam
 *
 */
public class BadResponseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public BadResponseException(String msg) { super(msg); }
}
