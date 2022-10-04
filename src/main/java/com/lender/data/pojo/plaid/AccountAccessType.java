package com.lender.data.pojo.plaid;

public enum AccountAccessType {
	ACCOUNT, ACCOUNT_AND_NUMBERS, PROCESSOR;
	
	public static AccountAccessType includeNumbers(boolean includeNumbers) {
		return includeNumbers ? ACCOUNT_AND_NUMBERS : ACCOUNT;
	}
}
