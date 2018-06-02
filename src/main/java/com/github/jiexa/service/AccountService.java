package com.github.jiexa.service;

import com.github.jiexa.model.Account;
import com.github.jiexa.service.exception.AccountAlreadyExistsException;
import com.github.jiexa.service.exception.AccountNotFoundException;
import com.github.jiexa.service.exception.AccountServiceException;
import com.github.jiexa.storage.AccountStorage;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

@Log4j2
public class AccountService {

    private AccountStorage accountStorage;

    public AccountService(AccountStorage accountStorage) {
        this.accountStorage = accountStorage;
    }

    //    TODO: should I return exactly this Account entity? or it is reasonable to create specified DTO?
    public Account createAccountFor(Long personId) throws AccountServiceException {

//        FIXME: probably, it is better to verify existence of the account on the data storage layer
        if (isAccountNotExistFor(personId)) {
            return accountStorage.createDefaultAccountByPersonId(personId);
        }
        throw new AccountServiceException("undefined exception");
    }

    public Account getAccountFor(Long personId) throws AccountNotFoundException {

        Optional<Account> account = accountStorage.getAccountByPersonId(personId);
        if (!account.isPresent()) {
            log.error("account for the person with id={} does not exist", personId);
            throw new AccountNotFoundException(String.format("error while getting an account for the person with id=%d", personId));
        }
        return account.get();
    }


    //    TODO: to guarantee an uniqueness of the Person preferably using UUID, but for the sake of simplicity I have chosen a long value
    private boolean isAccountNotExistFor(Long personId) throws AccountAlreadyExistsException {
        if (accountStorage.getAccountByPersonId(personId).isPresent()) {
            log.error("account for the person with id={} exists", personId);
            throw new AccountAlreadyExistsException(String.format("with id=%d", personId));
        }
        return true;
    }


    public void putMoneyIntoAccountInRubles(UUID accountId, Double amountOfMoney) throws AccountNotFoundException {

        Optional<Account> account = accountStorage.getAccountById(accountId);
        if (!account.isPresent()) {
            log.error("account with id={} does not exist", accountId);
            throw new AccountNotFoundException("error while getting an account with id="+ accountId);
        }

        account.get().replenishAccount(amountOfMoney);
    }


    public void withdrawMoneyFromAccountInRubles(UUID accountId, Double amountOfMoney) throws AccountNotFoundException {

        Optional<Account> account = accountStorage.getAccountById(accountId);
        if (!account.isPresent()) {
            log.error("account with id={} does not exist", accountId);
            throw new AccountNotFoundException("error while getting an account with id=" + accountId);
        }

        account.get().replenishAccount(amountOfMoney);
    }
}
