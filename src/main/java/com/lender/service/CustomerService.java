package com.lender.service;

import static com.lender.toolkit.RepositoryToolkit.STS_CONFLICT;
import static com.lender.toolkit.RepositoryToolkit.STS_OK;
import static com.lender.toolkit.RepositoryToolkit.STS_UNKNOWN_ERR;

import java.util.Calendar;
import java.util.UUID;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lender.data.exception.CustomerNotFoundException;
import com.lender.data.exception.CustomerRejectedException;
import com.lender.data.exception.DuplicateKeyException;
import com.lender.data.pojo.CustomerRequest;
import com.lender.data.pojo.User;
import com.lender.data.repository.CustomerRepository;
import com.lender.data.repository.pojo.Customer;
import com.lender.exception.UndefinedError;

import reactor.core.publisher.Mono;

@Service
public class CustomerService {
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -18);
		EIGHTEEN_CHECK = cal.getTimeInMillis();
	}
	
	/**
	 * Unix Timestamp (ms) for eighteen years ago
	 */
	private final long EIGHTEEN_CHECK;
	
	@Autowired
	private CustomerRepository customerRepo;

	public Mono<Customer> saveNewCustomer(CustomerRequest customerReq, User user) {
		final Customer customer = Customer.safelyInitializeCustomer(user, customerReq);
		
		validateCredentials(customer);
		
		return customerRepo.save(customer)
				.map(i -> {

					switch (i) {

					case STS_OK:			return customer;
					case STS_UNKNOWN_ERR: 	throw new PersistenceException();
					case STS_CONFLICT: 		throw new DuplicateKeyException();

					}

					throw new UndefinedError("CustomerService#saveNewCustomer()");
				});
	}

	/**
	 * Get a Customer; if they don't exist, create a new Customer.
	 * @param uuid The authenticated user's ID
	 * @return An existing or new Customer with the same ID passed in.
	 */
	public Mono<Customer> getCustomer(UUID uuid) {
		return customerRepo.getCustomerByID(uuid, false)
				.map(c -> {
					if (c.isEmpty())
						throw new CustomerNotFoundException(uuid);

					return c.get();
				});
	}
	
	private void validateCredentials(Customer customer) {
		if (customer.getBirthday().getTime() > EIGHTEEN_CHECK)
			throw new CustomerRejectedException("Under 18");
		
		// TODO add other validation
	}
}