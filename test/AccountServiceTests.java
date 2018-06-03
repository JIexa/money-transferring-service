import model.Account;
import model.exception.NotEnoughMoneyException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import service.AccountService;
import service.exception.AccountAlreadyExistsException;
import service.exception.AccountNotFoundException;
import service.exception.AccountServiceException;
import storage.AccountInMemoryStorage;
import storage.AccountStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.MathContext;
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
    public void existingAccount_creatingNewAccount_throwsAccountAlreadyExistsException() throws AccountServiceException {
        accountStorage.createDefaultAccountByPersonId(personId);

        accountService.createAccountFor(personId);
    }

    @Test(expected = AccountNotFoundException.class)
    public void nonExistingAccount_putMoney_throwsAccountNotFoundException() throws AccountNotFoundException {

        UUID accountId = UUID.randomUUID();
        BigDecimal amountOfMoney = new BigDecimal(14397.4);

        accountService.putMoneyIntoAccountInRubles(accountId, amountOfMoney);
    }

    @Test
    public void existingAccount_putMoney_successfulReplenishmentOfAccount() throws AccountServiceException {
        Account account = accountService.createAccountFor(personId);
        BigDecimal addingAmountOfMoney = new BigDecimal(14397.4);
        BigDecimal expectedAmountOfMoney = account.getAmountOfMoney().add(addingAmountOfMoney).round(MathContext.DECIMAL64);

        accountService.putMoneyIntoAccountInRubles(account.getAccountId(), addingAmountOfMoney);

        Assert.assertEquals("replenishing was proceeded incorrectly", expectedAmountOfMoney, account.getAmountOfMoney());
    }

    @Test
    public void existingAccount_withdrawMoney_successfulWithdrawingFromAccount() throws AccountServiceException, NotEnoughMoneyException {
//        try to withdraw part of the money from the account
        Account account = accountService.createAccountFor(personId);
        BigDecimal addingAmountOfMoney = new BigDecimal(1400.4);
        BigDecimal withdrawingAmountOfMoney = new BigDecimal(400.4);
        account.replenishAccount(addingAmountOfMoney);
        BigDecimal expectedAmountOfMoney = account.getAmountOfMoney().subtract(withdrawingAmountOfMoney).round(MathContext.DECIMAL64);

        accountService.withdrawMoneyFromAccountInRubles(account.getAccountId(), withdrawingAmountOfMoney);

        Assert.assertEquals("withdrawing was proceeded incorrectly", expectedAmountOfMoney, account.getAmountOfMoney());

//        try to withdraw all money from the account
        BigDecimal secondWithdrawing = new BigDecimal(1000.0);
        expectedAmountOfMoney = BigDecimal.ZERO.round(MathContext.DECIMAL64);

        accountService.withdrawMoneyFromAccountInRubles(account.getAccountId(), secondWithdrawing);

        Assert.assertEquals("withdrawing all money was proceeded incorrectly", expectedAmountOfMoney.byteValueExact(), account.getAmountOfMoney().byteValueExact());
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void existingAccount_withdrawMoney_throwsNotEnoughMoneyException() throws AccountServiceException, NotEnoughMoneyException {
        Account account = accountService.createAccountFor(personId);
        BigDecimal withdrawingAmountOfMoney = new BigDecimal(404);

        accountService.withdrawMoneyFromAccountInRubles(account.getAccountId(), withdrawingAmountOfMoney);
    }

    @Test(expected = AccountNotFoundException.class)
    public void nonExistingSourceAccount_transferMoney_throwsAccountNotFoundException() throws AccountServiceException, NotEnoughMoneyException {

        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = accountService.createAccountFor(personId).getAccountId();
        BigDecimal amountOfMoney = new BigDecimal(413.7);

        accountService.transferMoneyBetweenAccountsInRubles(sourceAccountId, targetAccountId, amountOfMoney);
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void existingSourceAccount_transferMoney_throwsNotEnoughMoneyException() throws AccountServiceException, NotEnoughMoneyException {

        UUID sourceAccountId = accountService.createAccountFor(personId).getAccountId();
        UUID targetAccountId = accountService.createAccountFor(2L).getAccountId();
        BigDecimal amountOfMoney = new BigDecimal(413.7);

        accountService.transferMoneyBetweenAccountsInRubles(sourceAccountId, targetAccountId, amountOfMoney);
    }

    @Test
    public void existingSourceAccount_transferMoney_successfulTransferring() throws AccountServiceException, NotEnoughMoneyException {
//        prepare source account
        Account sourceAccount = accountService.createAccountFor(personId);
        BigDecimal addingAmountOfMoney = new BigDecimal(10000);
        sourceAccount.replenishAccount(addingAmountOfMoney);
        BigDecimal initialValueOfSourceAccount = sourceAccount.getAmountOfMoney();

//        prepare target stuff
        Account targetAccount = accountService.createAccountFor(2L);
        BigDecimal initialValueOfTargetAccount = targetAccount.getAmountOfMoney();
        BigDecimal transferringAmountOfMoney = new BigDecimal(413.7);

        BigDecimal expectedAmountOfMoneyIntoSourceAccount = initialValueOfSourceAccount.subtract(transferringAmountOfMoney).round(MathContext.DECIMAL64);
        BigDecimal expectedAmountOfMoneyIntoTargetAccount = initialValueOfTargetAccount.add(transferringAmountOfMoney).round(MathContext.DECIMAL64);

        accountService.transferMoneyBetweenAccountsInRubles(sourceAccount.getAccountId(), targetAccount.getAccountId(), transferringAmountOfMoney);

        Assert.assertEquals("Incorrect amount of money was transferred from the source account", expectedAmountOfMoneyIntoSourceAccount, sourceAccount.getAmountOfMoney());
        Assert.assertEquals("Incorrect amount of money was transferred to the target account", expectedAmountOfMoneyIntoTargetAccount, targetAccount.getAmountOfMoney());
    }
}
