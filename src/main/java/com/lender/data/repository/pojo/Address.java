package com.lender.data.repository.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(schema = "loans", name = "customer_address")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long 	id;
	
	@Column(name = "street_number")
	private Short	streetNumber;
	
	@Column(name = "street_address")
	private String	addressLine1;
	
	@Column(name = "street_address2")
	private String	addressLine2;
	
	@Column(name = "city")
	private String	city;
	
	@Column(name = "state")
	private String	state;
	
	@Column(name = "zip_code")
	private Integer	zipCode;
}
