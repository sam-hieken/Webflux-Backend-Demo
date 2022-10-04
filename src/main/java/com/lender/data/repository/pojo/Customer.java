package com.lender.data.repository.pojo;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.lender.data.pojo.CustomerRequest;
import com.lender.data.pojo.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(schema = "loans", name = "customer")
public class Customer {
	
	/**
	 * Safely create a Customer from a request
	 * immediately ready to be inserted into the database.
	 * @param user
	 * @param customerReq
	 * @return
	 */
	public static Customer safelyInitializeCustomer(User user, CustomerRequest customerReq) {
		final Address address = parseAddress(customerReq);
		
		return new Customer(user, customerReq, address);
	}
	
	private static Address parseAddress(CustomerRequest customerReq) {
		final short streetNumber = Short.parseShort(customerReq.getAddressLine1().split(" ")[0]);
		final String addressLine1 = customerReq.getAddressLine1().replaceFirst("" + streetNumber, "").trim();

		return new Address(null, streetNumber, addressLine1, customerReq.getAddressLine2(),
				customerReq.getCity(), customerReq.getState(), customerReq.getZipCode());
	}
	
	@Id
	@Column(name = "user_id", updatable = false)
	private UUID 	userID;
	
//	@OneToMany(	mappedBy = "userID", fetch = FetchType.LAZY, 
//				cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE})
//	private List<LoanGrant> loanGrants;
	
	@Column(name = "owed")
	private Double 	owed = 0.0;
	
	@Column(name = "ssn_last4")
	private short	last4SSN;
	
	@Column(name = "verified_status")
	private short 	verifiedStatus = 0;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address")
	private Address	address;
	
	@Column(name = "phone_number")
	private String	phoneNumber;
	
	@Column(name = "birthday")
	private java.sql.Date	
					birthday;
	
	@Column(name = "last_payment")
	private Date 	lastPayment = null;
	
	@Column(name = "last_grant")
	private Date 	lastGrant = null;
	
	@Column(name = "bank_token")
	private String 	accessToken = null;
	
	private Customer(User user, CustomerRequest customerReq, Address address) {
		this.address = address;
		this.phoneNumber = customerReq.getPhoneNumber();
		this.birthday = customerReq.getBirthday();
		this.last4SSN = customerReq.getLast4SSN();
		this.userID = user.getUuid();
	}
	
	public String getAddressLine1() {
		if (address == null) return null;
		
		return address.getStreetNumber() + " " + address.getAddressLine1();
	}
	
	public String getAddressLine2() {
		if (address == null) return null;
		
		return address.getAddressLine2();
	}
}
