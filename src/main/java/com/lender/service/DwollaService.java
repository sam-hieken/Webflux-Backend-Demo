package com.lender.service;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lender.data.pojo.User;
import com.lender.data.pojo.dwolla.DwollaCustomer;
import com.lender.data.repository.pojo.Customer;
import com.lender.exception.BadResponseException;
import com.lender.exception.dwolla.DuplicateCustomerException;
import com.lender.toolkit.DwollaToolkit;

import reactor.core.publisher.Mono;

@Service
public class DwollaService {
	@Autowired
	private DwollaToolkit toolkit;

	public Mono<Void> createNewCustomer(User user, Customer customer) {
		final String birthday = new SimpleDateFormat("yyyy-MM-dd").format(customer.getBirthday());

		final DwollaCustomer req = new DwollaCustomer(user.getFirstName(), user.getLastName(),
				user.getEmail(), "personal", customer.getAddressLine1(), customer.getAddressLine2(),
				customer.getAddress().getCity(), customer.getAddress().getState(), 
				customer.getAddress().getZipCode().toString(), birthday, customer.getLast4SSN() + "");
		
		return toolkit.post("/customers", Mono.just(req), DwollaCustomer.class, Void.class)
				.doOnError(e -> {
					if (e instanceof BadResponseException)
						throw new DuplicateCustomerException(e.getMessage());
				});
	}
}
