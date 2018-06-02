package com.github.jiexa;

import com.github.jiexa.model.Account;
import com.github.jiexa.service.AccountService;
import com.github.jiexa.service.exception.AccountAlreadyExistsException;
import com.github.jiexa.service.exception.AccountNotFoundException;
import com.github.jiexa.service.exception.AccountServiceException;
import com.github.jiexa.storage.AccountInMemoryStorage;
import com.github.jiexa.storage.AccountStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTests {

    private final Long personId = 1L;

    @Mock
    private AccountStorage spyStorage;

    private AccountStorage accountStorage;
    private AccountService accountService;

    @Before
    public void setUp() {
        accountStorage = new AccountInMemoryStorage();
        spyStorage = Mockito.spy(accountStorage);
        accountService = new AccountService(spyStorage);
    }

    @Test
    public void nonExistingAccount_creatingNewAccount_accountForPersonWasCreated() throws AccountServiceException {

        Assert.assertEquals(
                String.format("the storage already has an account for the person with id=%d", personId),
                false,
                accountStorage.getAccountByPersonId(personId).isPresent());

        Account newAccount = accountService.createAccountFor(personId);

        verify(spyStorage, times(1)).createDefaultAccountByPersonId(personId);
        Assert.assertEquals("accounts owners are different", personId, newAccount.getOwnerId());
    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void existingAccount_creatingNewAccount_throwAccountAlreadyExistsException() throws AccountServiceException {
        accountStorage.createDefaultAccountByPersonId(personId);

        accountService.createAccountFor(personId);
    }

    @Test(expected = AccountNotFoundException.class)
    public void nonExistingAccount_putMoneyIntoAccount_throwAccountNotFoundException() throws AccountNotFoundException {

        UUID accountId = UUID.randomUUID();
        Double amountOfMoney = 14397.4;

        accountService.putMoneyIntoAccountInRubles(accountId, amountOfMoney);
    }

    @Test
    public void existingAccount_putMoneyIntoAccount_successfulReplenishmentOfAccount() throws AccountServiceException {
        Account account = accountService.createAccountFor(personId);
        Double addingAmountOfMoney = 14397.4;
        Double expectedAmountOfMoney = Double.sum(account.getAmountOfMoney(), addingAmountOfMoney);

        accountService.putMoneyIntoAccountInRubles(account.getAccountId(), addingAmountOfMoney);

        Assert.assertEquals("replenishing was proceeded incorrectly", expectedAmountOfMoney, account.getAmountOfMoney());
    }
}
