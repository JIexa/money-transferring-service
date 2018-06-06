import io.ebean.Ebean;
import models.Account;
import models.exception.NotEnoughMoneyException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.test.Helpers;
import service.AccountService;
import service.exception.AccountNotFoundException;
import service.exception.AccountServiceException;
import storage.AccountJDBCStorage;
import storage.AccountStorage;
import storage.exception.AccountAlreadyExistsException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTests {

    private final Long personId = 1L;

    private Application app;

    @Mock
    private AccountStorage spyStorage;

    private AccountStorage accountStorage;
    private AccountService accountService;

    @Before
    public void setUp() {

        Map<String, String> settings = new HashMap<>();
        settings.put("db.default.url", "jdbc:h2:mem:play;INIT=runscript from 'classpath:evolutions/init_script.sql'");
        Application app = Helpers.fakeApplication(settings);
        Helpers.start(app);

        accountStorage = new AccountJDBCStorage();
        spyStorage = Mockito.spy(accountStorage);
        accountService = new AccountService(spyStorage);
    }

//    @After
//    public void tearDown() {
//        Helpers.stop(app);
//    }

    @Test
    public void nonExistingAccount_creatingNewAccount_accountForPersonWasCreated() throws AccountServiceException, AccountAlreadyExistsException {

        Assert.assertEquals(
                String.format("the storage already has an account for the person with id=%d", personId),
                false,
                accountStorage.getAccountByPersonId(personId).isPresent());

        Account newAccount = accountService.createAccountFor(personId);

        verify(spyStorage, times(1)).createDefaultAccountByPersonId(personId);
        Assert.assertEquals("accounts owners are different", personId, newAccount.getOwnerId());
    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void existingAccount_creatingNewAccount_throwsAccountAlreadyExistsException() throws AccountServiceException, AccountAlreadyExistsException {
        accountStorage.createDefaultAccountByPersonId(personId);

        accountService.createAccountFor(personId);
    }

    @Test(expected = AccountNotFoundException.class)
    public void nonExistingAccount_putMoney_throwsAccountNotFoundException() throws AccountServiceException {

        Long accountId = 1L;
        BigDecimal amountOfMoney = new BigDecimal(14397.4);

        accountService.putMoneyIntoAccountInRubles(accountId, amountOfMoney);
    }

    @Test
    public void existingAccount_putMoney_successfulReplenishmentOfAccount() throws AccountServiceException, AccountAlreadyExistsException {

        Account account = null;
        account = accountService.createAccountFor(personId);
        BigDecimal addingAmountOfMoney = new BigDecimal(14397.4);
        BigDecimal expectedAmountOfMoney = account.getAmountOfMoney().add(addingAmountOfMoney).round(MathContext.DECIMAL64);

        account = accountService.putMoneyIntoAccountInRubles(account.getId(), addingAmountOfMoney);

        Assert.assertEquals("replenishing was proceeded incorrectly", expectedAmountOfMoney, account.getAmountOfMoney());

    }

    @Test
    public void existingAccount_withdrawMoney_successfulWithdrawingFromAccount() throws AccountServiceException, NotEnoughMoneyException, AccountAlreadyExistsException {
//        try to withdraw part of the money from the account
        Account account = accountService.createAccountFor(personId);
        BigDecimal addingAmountOfMoney = new BigDecimal(1400.4);
        BigDecimal withdrawingAmountOfMoney = new BigDecimal(400.4);
        account.replenishAccount(addingAmountOfMoney);
        Ebean.save(account);
        BigDecimal expectedAmountOfMoney = account.getAmountOfMoney().subtract(withdrawingAmountOfMoney).round(MathContext.DECIMAL64);

        account = accountService.withdrawMoneyFromAccountInRubles(account.getId(), withdrawingAmountOfMoney);

        Assert.assertEquals("withdrawing was proceeded incorrectly", expectedAmountOfMoney, account.getAmountOfMoney());

//        try to withdraw all money from the account
        BigDecimal secondWithdrawing = new BigDecimal(1000.0);
        expectedAmountOfMoney = BigDecimal.ZERO.round(MathContext.DECIMAL64);

        account = accountService.withdrawMoneyFromAccountInRubles(account.getId(), secondWithdrawing);

        Assert.assertEquals("withdrawing all money was proceeded incorrectly", expectedAmountOfMoney.byteValueExact(), account.getAmountOfMoney().byteValueExact());
    }

    @Test(expected = AccountServiceException.class)
    public void existingAccount_withdrawMoney_throwsNotEnoughMoneyException() throws  AccountAlreadyExistsException, AccountServiceException {
        Account account = accountService.createAccountFor(personId);
        BigDecimal withdrawingAmountOfMoney = new BigDecimal(404);

        accountService.withdrawMoneyFromAccountInRubles(account.getId(), withdrawingAmountOfMoney);
    }

    @Test(expected = AccountNotFoundException.class)
    public void nonExistingSourceAccount_transferMoney_throwsAccountNotFoundException() throws AccountServiceException, NotEnoughMoneyException, AccountAlreadyExistsException {

        Long sourceAccountId = 9999L;
        Long targetAccountId = accountService.createAccountFor(personId).getId();
        BigDecimal amountOfMoney = new BigDecimal(413.7);

        accountService.transferMoneyBetweenAccountsInRubles(sourceAccountId, targetAccountId, amountOfMoney);
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void existingSourceAccount_transferMoney_throwsNotEnoughMoneyException() throws AccountServiceException, NotEnoughMoneyException, AccountAlreadyExistsException {

        Long sourceAccountId = accountService.createAccountFor(personId).getId();
        Long targetAccountId = accountService.createAccountFor(2L).getId();
        BigDecimal amountOfMoney = new BigDecimal(413.7);

        accountService.transferMoneyBetweenAccountsInRubles(sourceAccountId, targetAccountId, amountOfMoney);
    }

    @Test
    public void existingSourceAccount_transferMoney_successfulTransferring() throws AccountServiceException, NotEnoughMoneyException, AccountAlreadyExistsException {
//        prepare source account
        Account sourceAccount = accountService.createAccountFor(personId);
        BigDecimal addingAmountOfMoney = new BigDecimal(10000);
        sourceAccount.replenishAccount(addingAmountOfMoney);
        Ebean.save(sourceAccount);
        BigDecimal initialValueOfSourceAccount = sourceAccount.getAmountOfMoney();

//        prepare target stuff
        Account targetAccount = accountService.createAccountFor(2L);
        BigDecimal initialValueOfTargetAccount = targetAccount.getAmountOfMoney();
        BigDecimal transferringAmountOfMoney = new BigDecimal(413.7);

        BigDecimal expectedAmountOfMoneyIntoSourceAccount = initialValueOfSourceAccount.subtract(transferringAmountOfMoney).round(MathContext.DECIMAL64);
        BigDecimal expectedAmountOfMoneyIntoTargetAccount = initialValueOfTargetAccount.add(transferringAmountOfMoney).round(MathContext.DECIMAL64);

        accountService.transferMoneyBetweenAccountsInRubles(sourceAccount.getId(), targetAccount.getId(), transferringAmountOfMoney);

        sourceAccount = accountService.getAccountById(sourceAccount.getId());
        targetAccount = accountService.getAccountById(targetAccount.getId());

        Assert.assertEquals("Incorrect amount of money was transferred from the source account", expectedAmountOfMoneyIntoSourceAccount, sourceAccount.getAmountOfMoney());
        Assert.assertEquals("Incorrect amount of money was transferred to the target account", expectedAmountOfMoneyIntoTargetAccount, targetAccount.getAmountOfMoney());
    }
}
