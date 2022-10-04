package com.lender.toolkit;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Mono;

public class RepositoryToolkit {	
	private static final String 	DUPLICATE_KEY_MSG = "ERROR: duplicate key value violates unique constraint ";
	
	public static final int			STS_OK = 1,
									STS_NOT_FOUND = 0,
									STS_UNKNOWN_ERR = -1,
									STS_CONFLICT = -2;
	
	/**
	 * Create a promise to get an object of type T
	 *
	 * @param <T> Entity type
	 * @param factory The factory to generate the request
	 * @param func The actions to perform during the session.
	 * @return An Optional of the requested object, empty if no result (NoResultException)
	 */
	public static <T> Mono<Optional<T>> createGetMono(SessionFactory factory, 
			Function<Session, Uni<T>> func) {
		final CompletableFuture<Optional<T>> u = factory.withSession(func)
				.map(obj -> Optional.ofNullable(obj))
				.onFailure(NoResultException.class).recoverWithItem(e -> Optional.empty())
				.subscribeAsCompletionStage();

		return Mono.fromFuture(u);
	}
	
	public static Mono<Integer> createUpdateMono(SessionFactory factory, 
			BiFunction<Mutiny.Session,Mutiny.Transaction,Uni<Integer>> func) {
		
		final CompletableFuture<Integer> future = factory.withTransaction(func)
				.onFailure(PersistenceException.class).recoverWithItem(handleUpdateErrors)
				.subscribeAsCompletionStage();
		
		return Mono.fromFuture(future);
	}
	
	public static Mono<Integer> createSaveMono(SessionFactory factory, 
			BiFunction<Mutiny.Session,Mutiny.Transaction,Uni<Void>> func) {
		
		final CompletableFuture<Integer> future = factory.withTransaction(func)
				.flatMap(v -> Uni.createFrom().item(STS_OK))
				.onFailure(PersistenceException.class).recoverWithItem(handleSaveErrors)
				.subscribeAsCompletionStage();
		
		return Mono.fromFuture(future);
	}
	
	/**
	 * Transforms an exception thrown by Hibernate into a status code when saving.
	 */
	private static final Function<Throwable, Integer> handleSaveErrors = (except) -> {
		final String msg = except.getMessage();
		
		if (msg.contains(DUPLICATE_KEY_MSG)) 
			return STS_CONFLICT;
		
		// Unhandled error, print & return -1
		System.err.println("-----".repeat(50));
		except.printStackTrace();
		System.err.println("-----".repeat(50));
		
		return STS_UNKNOWN_ERR;
	};
	
	/**
	 * Transforms an exception thrown by Hibernate into a status code when updating.
	 */
	private static final Function<Throwable, Integer> handleUpdateErrors = (except) -> {

		// Unhandled error, print & return -1
		System.err.println("-----".repeat(50));
		except.printStackTrace();
		System.err.println("-----".repeat(50));
		
		return STS_UNKNOWN_ERR;
	};
}
