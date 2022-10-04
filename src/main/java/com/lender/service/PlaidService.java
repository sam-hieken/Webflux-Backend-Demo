package com.lender.service;

import static com.lender.toolkit.PlaidToolkit.CLIENT_NAME;
import static com.lender.toolkit.PlaidToolkit.COUNTRY_CODES;
import static com.lender.toolkit.PlaidToolkit.PRODUCTS;
import static com.lender.toolkit.RepositoryToolkit.STS_NOT_FOUND;
import static com.lender.toolkit.RepositoryToolkit.STS_UNKNOWN_ERR;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lender.data.exception.CustomerNotFoundException;
import com.lender.data.pojo.User;
import com.lender.data.pojo.plaid.AccountAccessType;
import com.lender.data.pojo.plaid.LinkTokenCreateRequest;
import com.lender.data.pojo.plaid.LinkTokenCreateResponse;
import com.lender.data.repository.CustomerRepository;
import com.lender.exception.BadResponseException;
import com.lender.exception.UndefinedError;
import com.lender.exception.plaid.BadAccountException;
import com.lender.exception.plaid.BadTokenException;
import com.lender.toolkit.PlaidToolkit;

import reactor.core.publisher.Mono;

@Service
public class PlaidService {
	@Autowired
	private CustomerRepository 	customerRepo;

	@Autowired
	private PlaidToolkit		toolkit;

	/**
	 * Create a link token for frontend.
	 * @param user The authorized user.
	 * @return Link token as a Mono< String >
	 */
	public Mono<String> createLinkToken(User user) {
		var req = new LinkTokenCreateRequest(CLIENT_NAME, "en", COUNTRY_CODES,
				PRODUCTS, LinkTokenCreateRequest.newUser(user.getUuid().toString()));

		return toolkit.post("/link/token/create", Mono.just(req), LinkTokenCreateRequest.class, LinkTokenCreateResponse.class)
				.flatMap(e -> 
					Mono.just(e.getLink_token()));
	}

	/**
	 * Create an access token through Plaid's API
	 * @param publicToken The public token received from link module.
	 * @return The access token as a Mono< String >
	 */
	public Mono<String> createAccessToken(String publicToken) {
		var req = Map.of("public_token", publicToken);

		return toolkit.post("/item/public_token/exchange", Mono.just(req), Map.class, Map.class)
				.flatMap(
						e -> Mono.just((String) e.get("access_token")))
				.doOnError(e -> {
					if (e instanceof BadResponseException)
						throw new BadTokenException(BadTokenException.TOKEN_INVALID);

					e.printStackTrace();
					throw new UndefinedError("Add other exceptions");
				});
	}

	/**
	 * Create a new processor token from a User.
	 * @param user A verified user.
	 * @return A processor token as a Mono< String >
	 */
	public Mono<String> createProcessorToken(User user) {
		return getBankInfo(user, AccountAccessType.PROCESSOR)
				.flatMap(map -> {
					String accessToken = (String) map.get("access_token");
					String accountID = (String) map.get("account_id");

					return createProcessorToken(accessToken, accountID);
				});

	}

	/**
	 * Get user's bank info.
	 * @param user The User to retrieve bank info from.
	 * @param accessType The access settings.
	 * @return
	 */
	public Mono<Map<String, Object>> getBankInfo(User user, AccountAccessType accessType) {
		return customerRepo.getCustomerByID(user.getUuid(), false)
				.flatMap(c -> {
					if (c.isEmpty())
						throw new CustomerNotFoundException(user.getUuid());

					final String accessToken = c.get().getAccessToken();
					
					if (accessToken == null)
						throw new BadTokenException(BadTokenException.TOKEN_MISSING);
					
					return getBankInfo(accessToken, accessType);
				});
	}
	
	/**
	 * Update the access token.
	 * @param user The authorized user.
	 * @param accessToken The new access token.
	 */
	public Mono<Void> updatePlaidToken(User user, String accessToken) {
		
		return customerRepo.updateBankToken(user.getUuid(), accessToken)
				.flatMap(i -> {
					if (i > 0)
						return Mono.empty();

					switch (i) {

					case STS_NOT_FOUND:		throw new CustomerNotFoundException(user.getUuid());
					case STS_UNKNOWN_ERR: 	throw new PersistenceException();

					}

					throw new UndefinedError("CustomerService#updatePlaidToken()");
				});
	}

	/**
	 * Delete bank tokens associated with this account.
	 * @param user
	 * @return
	 */
	public Mono<Void> deleteBankInfo(User user) {
		return customerRepo.deletePlaidToken(user.getUuid())
				.flatMap(i -> {
					if (i == 0)
						throw new BadTokenException(BadTokenException.TOKEN_MISSING);

					else if (i != 1) 
						throw new UndefinedError("Deleting bank info " + i);

					return Mono.empty();
				});
	}

	/**
	 * Get processor token from Plaid for Dwolla.
	 * @param accessToken The user's access token
	 * @param accountID The user's bank account ID on Plaid
	 * @return A Mono containing the processor token
	 */
	private Mono<String> createProcessorToken(String accessToken, String accountID) {
		var req = Map.of("access_token", accessToken, "account_id", accountID, "processor", "dwolla");

		return toolkit.post("/processor/token/create", Mono.just(req), Map.class, Map.class)
				.map(
						e -> (String) e.get("processor_token"))
				.doOnError(e -> {
					if (e instanceof BadResponseException)
						throw new BadTokenException(BadTokenException.TOKEN_INVALID);

					e.printStackTrace();
					throw new UndefinedError("Add other exceptions");
				});
	}

	/**
	 * Get bank account information from Plaid.
	 * @param accessToken The access token for the account.
	 * @param accessType The access setting. Most notably used to enable/disable account numbers
	 * @return
	 */
	private Mono<Map<String, Object>> getBankInfo(String accessToken, AccountAccessType accessType) {
		var req = Map.of("access_token", accessToken);

		return toolkit.post("/identity/get", Mono.just(req), Map.class, Map.class)
				.map(extractAccountDetails(accessToken, accessType))
				.flatMap(addAccountNumbers(accessToken, accessType))
				.doOnError(handleAccountNumberErrors());
	}
	
	/**
	 * Function to add account numbers if the request specified to do so.
	 * @param customer The Customer to add the account numbers to.
	 * @param add Whether or not to actually trigger the add behavior.
	 * @return
	 */
	private Function<Map<String,Object>, Mono<Map<String, Object>>> addAccountNumbers(String accessToken, AccountAccessType accessType) {
		return accounts -> {
			if (accessType != AccountAccessType.ACCOUNT_AND_NUMBERS)
				return Mono.just(accounts);

			var req = Map.of("access_token", accessToken);

			return toolkit.post("/auth/get", Mono.just(req), Map.class, Map.class)
					.map(numbs -> 
						Map.of("info", accounts, "numbers", numbs.get("numbers")));
		};
	}

	/**
	 * Get the first account from the list retrieved, and make necessary mods
	 * @param customer
	 * @param accessType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Function<Map, Map<String, Object>> extractAccountDetails(
			String accessToken, AccountAccessType accessType) {

		return map -> {
			List<Object> accounts = (List) map.get("accounts");
			Map<String, Object> account = (Map<String, Object>) accounts.get(0);

			// If we're getting the account details for generating processor token
			if (accessType == AccountAccessType.PROCESSOR)
				account.put("access_token", accessToken);

			return account;
		};
	}

	/**
	 * Logic applied when an error occurs retrieving account numbers from Plaid.
	 */
	private Consumer<Throwable> handleAccountNumberErrors() {
		return e -> {
			if (e instanceof BadResponseException) {
				final boolean invalidAccount = e.getMessage().split("\\:\\:")[2].equals("NO_AUTH_ACCOUNTS");
				if (invalidAccount)
					throw new BadAccountException();
			}
			
			else if (e instanceof BadTokenException)
				 return;

			throw new UndefinedError("addAccountNumbers");
		};
	}
}
