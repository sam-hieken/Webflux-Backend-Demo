package com.lender.data.exception;

/**
 * Thrown when a UNIQUE constraint is violated performing an SQL insert
 * <br>
 * <b>Result: 409</b>
 * @author Sam
 *
 */
public class DuplicateKeyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}