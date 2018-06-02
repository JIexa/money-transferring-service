package com.github.jiexa.controller;

import com.github.jiexa.service.AccountService;
import com.github.jiexa.service.exception.AccountServiceException;

public class MoneyController {

    private AccountService accountService;

    public MoneyController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Endpoint for creating account for the certain person
     * @param personId - id of a person in the system
     * @return RESTful response code FIXME: and, maybe, account data or just accountId
     * @throws AccountServiceException - FIXME: give more specified exception
     */
    public int createAccount(Long personId) throws AccountServiceException {

        accountService.createAccountFor(personId);

        return 200;
    }

}
