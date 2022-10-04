package com.lender.service;

import static com.lender.toolkit.RepositoryToolkit.STS_CONFLICT;
import static com.lender.toolkit.RepositoryToolkit.STS_OK;
import static com.lender.toolkit.RepositoryToolkit.STS_UNKNOWN_ERR;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lender.data.exception.DuplicateKeyException;
import com.lender.data.repository.LoanRepository;
import com.lender.data.repository.pojo.LoanGrant;
import com.lender.exception.UndefinedError;

import reactor.core.publisher.Mono;

@Service
public class LoanService {
	@Autowired
	private LoanRepository loanRepo;
	
	public Mono<LoanGrant> saveNewLoanGrant(LoanGrant grant) {
		return loanRepo.save(grant).map(i -> {
			switch (i) {
			
				case STS_OK:			return grant;
				case STS_UNKNOWN_ERR: 	throw new PersistenceException();
				case STS_CONFLICT: 		throw new DuplicateKeyException();
			
			}
			
			throw new UndefinedError("LoanService#saveNewLoanGrant()");
		});
	}
}
