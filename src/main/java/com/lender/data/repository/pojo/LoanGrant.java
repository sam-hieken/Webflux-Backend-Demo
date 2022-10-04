package com.lender.data.repository.pojo;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.lender.data.repository.converter.LoanGrantTypeConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(schema = "loans", name = "grants")
public class LoanGrant {
	@Id
	@Column(name = "id")
	private UUID 	loanID = UUID.randomUUID();
	
	@Column(name = "granted_user_id")
	private UUID 	userID;
	
	@Column(name = "broker_uuid")
	private UUID	brokerID;
	
	@Column(name = "granted_amount")
	private double 	amountGranted;
	
	@Column(name = "granted_balance_new")
	private double 	newBankBalance;
	
	@Column(name = "granted_time")
	private Date 	date = new Date(System.currentTimeMillis());
	
	@Column(name = "granted_bank_token")
	private String 	plaidToken;
	
	@Column(name = "granted_credit_score")
	private short 	creditScore;
	
	@Column(name = "grant_type")
	@Convert(converter = LoanGrantTypeConverter.class)
	private LoanGrantType grantType = LoanGrantType.AUTOMATIC;

	
//	public LoanGrant(UUID userID, UUID brokerID, double amountGranted, double newBankBalance, 
//			Date date, String plaidToken, short creditScore, LoanGrantType grantType) {
//		
//		this.userID = userID;
//		this.brokerID = brokerID;
//		this.amountGranted = amountGranted;
//		this.newBankBalance = newBankBalance;
//		this.date = date;
//		this.plaidToken = plaidToken;
//		this.creditScore = creditScore;
//		this.grantType = grantType;
//		
//	}
	
	// XXX for testing only
	public LoanGrant(Customer customer) {
		this.userID = customer.getUserID();
		this.brokerID = UUID.randomUUID();
		this.amountGranted = (System.currentTimeMillis() % 10000) + 0.5;
		this.newBankBalance = 69.420;
		this.plaidToken = customer.getAccessToken();
		this.creditScore = 420;
	}
}