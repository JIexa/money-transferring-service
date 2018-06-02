package com.github.jiexa;

import com.github.jiexa.controller.MoneyController;
import com.github.jiexa.model.Account;
import com.github.jiexa.service.AccountService;
import com.github.jiexa.service.AccountServiceException;
import com.github.jiexa.storage.AccountInMemoryStorage;
import com.github.jiexa.storage.AccountStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountServiceTests {

    private AccountStorage accountStorage;
    private AccountService accountService;

    @Before
    public void setUp() {
        accountStorage = new AccountInMemoryStorage();
        accountService = new AccountService(accountStorage);
    }

    @Test
    public void nonExistingAccount_creatingNewAccount_accountForPersonExists() throws AccountServiceException {
//        accountStorage is empty
        Long personId = 1L;

        Account newAccount = accountService.createAccountFor(personId);
        accountService.getAccountFor(personId);

        Assert.assertEquals("accounts UUIDs are different", accountStorage.getAccountByPersonId(personId).get().getAccountId(), newAccount.getAccountId());
    }

}
