package com.lender.exception.plaid;

/**
 * Thrown when the account the user selected with Plaid cannot retrieve certain data
 * due to an issue with Plaid.
 * <br>
 * <b>Result: 503</b>
 * @author Sam
 *
 */
public class BadAccountException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
