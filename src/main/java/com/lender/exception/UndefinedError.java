package com.lender.exception;

/**
 * Completely undefined behavior occurred in the code; requires immediate attention.
 * <br>
 * <b>Result: 500</b>
 * @author Sam
 *
 */
public class UndefinedError extends Error {
	private static final long serialVersionUID = 1L;

	public UndefinedError(String message) { super(message); }
}
