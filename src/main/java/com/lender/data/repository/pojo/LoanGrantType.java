package com.lender.data.repository.pojo;

/**
 * The process used to approve the loan. 
 * @author Sam
 *
 */
public enum LoanGrantType {
	/** The loan was approved automatically by an AI */
	AUTOMATIC, 
	
	/** The loan was approved manually by an admin */
	ADMIN_CREATED
}
