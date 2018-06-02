package com.github.jiexa.service;

import com.github.jiexa.model.Account;
import com.github.jiexa.storage.AccountStorage;
import lombok.extern.log4j.Log4j2;

import java.nio.channels.AcceptPendingException;
import java.util.Optional;
import java.util.function.Supplier;

@Log4j2
public class AccountService {

    private AccountStorage accountStorage;

    public AccountService(AccountStorage accountStorage) {
        this.accountStorage = accountStorage;
    }

    //    TODO: should I return exactly this Account entity? or it is reasonable to create specified DTO?
    public Account createAccountFor(Long personId) throws AccountServiceException {
        if (isAccountNotExistFor(personId)) {
            return accountStorage.createAccountByPersonId(personId);
        }
        throw new AccountServiceException("undefined exception");
    }

    public Account getAccountFor(Long personId) throws AccountServiceException {

        Optional<Account> account = accountStorage.getAccountByPersonId(personId);
        if (!account.isPresent()) {
            log.error("account for the person with id={} does not exist", personId);
            throw new AccountServiceException("error while getting an account for the person with id=" + personId);
        }
        return account.get();
    }



    //    TODO: to guarantee an uniqueness of the Person preferably using UUID, but for the sake of simplicity I have chosen a long value
    private boolean isAccountNotExistFor(Long personId) throws AccountServiceException {
        if (accountStorage.getAccountByPersonId(personId).isPresent()) {
            log.error("account for the person with id={} exists", personId);
            throw new AccountServiceException("account already exists");
        }
        return true;
    }
}
