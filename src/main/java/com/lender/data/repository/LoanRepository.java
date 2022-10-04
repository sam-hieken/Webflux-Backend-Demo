package com.lender.data.repository;

import static com.lender.toolkit.RepositoryToolkit.createGetMono;
import static com.lender.toolkit.RepositoryToolkit.createSaveMono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lender.data.repository.pojo.LoanGrant;

import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Mono;

@Repository
public class LoanRepository {
	@Autowired
	private Mutiny.SessionFactory factory;

	public Mono<Optional<List<LoanGrant>>> getLoanGrantsByUser(UUID userID) {
		final Function<Session, Uni<List<LoanGrant>>> func = session -> 
				session.createQuery("FROM LoanGrant l WHERE l.userID = :userID", LoanGrant.class)
						.setParameter("userID", userID)
						.getResultList();
		
		return createGetMono(factory, func);
	}

	public Mono<Optional<LoanGrant>> getLoanGrantByID(UUID loanID) {
		final Function<Session, Uni<LoanGrant>> func = session -> session.createQuery("FROM LoanGrant WHERE loanID = :loanID", LoanGrant.class)
										.setParameter("loanID", loanID)
										.getSingleResult();
		
		return createGetMono(factory, func);
	}

	public Mono<Integer> save(LoanGrant loan) {
		BiFunction<Mutiny.Session,Mutiny.Transaction,Uni<Void>> func = 
				(session, tx) -> session.persist(loan);

		return createSaveMono(factory, func);
	}
}
