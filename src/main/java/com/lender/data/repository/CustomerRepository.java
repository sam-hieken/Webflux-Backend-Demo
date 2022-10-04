package com.lender.data.repository;

import static com.lender.toolkit.RepositoryToolkit.*;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lender.data.repository.pojo.Customer;

import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Mono;

@Repository
public class CustomerRepository {
	@Autowired
	private Mutiny.SessionFactory factory;

	public Mono<Optional<Customer>> getCustomerByID(UUID userID, boolean includeLoanGrants) {
		final String fetch = includeLoanGrants ? "LEFT JOIN FETCH c.loanGrants" : "";

		final Function<Session, Uni<Customer>> func = session -> (
					session.createQuery("FROM Customer c " + fetch + " WHERE c.userID = :userID", Customer.class)
						.setParameter("userID", userID)
						.getSingleResult()
				);

		return createGetMono(factory, func);
	}

	public Mono<Integer> updateBankToken(UUID userID, String accessToken) {
		var func = updateSingleField(userID, "accessToken", accessToken);
		return createUpdateMono(factory, func);
	}
	
	public Mono<Integer> deletePlaidToken(UUID userID) {
		BiFunction<Mutiny.Session,Mutiny.Transaction,Uni<Integer>> func = (session, tx) -> {
			return session.createQuery("UPDATE Customer SET accessToken = null WHERE userID = :userID")
					.setParameter("userID", userID)
					.executeUpdate();
		};

		return createUpdateMono(factory, func);
	}

	public Mono<Integer> save(Customer customer) {
		BiFunction<Mutiny.Session,Mutiny.Transaction,Uni<Void>> func = (session, tx) -> session.persist(customer);

		return createSaveMono(factory, func);
	}
	
	/**
	 * Shorthand to create a BiFunction that updates a single field
	 * 
	 * @param <T> value's type
	 * @param userID The user's ID.
	 * @param fieldName The name of the POJO's field
	 * @param value The new value for the field being updated.
	 * @return A BiFunction that can be plugged into createUpdateMono()
	 */
	private <T> BiFunction<Mutiny.Session,Mutiny.Transaction,Uni<Integer>> updateSingleField(UUID userID, String fieldName, T value) {
		return (session, tx) -> 
			session.createQuery("UPDATE Customer SET " + fieldName + " = :param WHERE userID = :userID")
					.setParameter("param", value)
					.setParameter("userID", userID)
					.executeUpdate();
	}
	
}
