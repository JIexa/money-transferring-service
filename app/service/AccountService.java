package service;

import com.google.inject.Inject;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.Account;
import models.exception.NotEnoughMoneyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.exception.AccountNotFoundException;
import service.exception.AccountServiceException;
import service.exception.IncorrectFormatAmountOfMoneyException;
import storage.AccountStorage;
import storage.exception.AccountAlreadyExistsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private AccountStorage accountStorage;

    @Inject
    public AccountService(AccountStorage accountStorage) {
        this.accountStorage = accountStorage;
    }

    public Account createAccountFor(Long personId) throws AccountAlreadyExistsException {

        return accountStorage.createDefaultAccountByPersonId(personId);
    }

    public Account getAccountPersonId(Long personId) throws AccountNotFoundException {

        Optional<Account> account = accountStorage.getAccountByPersonId(personId);

        checkAccountExists(account.get().getId(), account);

        return account.get();
    }

    public Account getAccountById(Long id) throws AccountNotFoundException {

        Optional<Account> account = accountStorage.getAccountById(id);

        checkAccountExists(account.get().getId(), account);

        return account.get();
    }

    public List<Account> getAllAccounts() {
        return accountStorage.getAllAccounts();
    }


    public Account putMoneyIntoAccountInRubles(Long id, BigDecimal amountOfMoney) throws AccountServiceException {

        checkAmountOfMoneyIsCorrect(amountOfMoney);

        Optional<Account> account = accountStorage.getAccountById(id);
        checkAccountExists(id, account);
        return processInTransaction(() -> {
            account.get().replenishAccount(amountOfMoney);
            Ebean.save(account.get());
            log.debug("replenishment of the account with id={}", account.get().getId());
            return account.get();
        });
    }

    public Account withdrawMoneyFromAccountInRubles(Long id, BigDecimal amountOfMoney) throws IncorrectFormatAmountOfMoneyException, AccountServiceException {

        checkAmountOfMoneyIsCorrect(amountOfMoney);

        Optional<Account> optAccount = accountStorage.getAccountById(id);
        checkAccountExists(id, optAccount);
        Account account = optAccount.get();
        Account updatedAccount = processInTransaction(() -> {
            try {
                account.withdrawFromAccount(amountOfMoney);
                Ebean.save(account);
                log.debug("withdrawing from the account with id={}", account.getId());
                return account;
            } catch (NotEnoughMoneyException e) {
                log.error("withdrawing from the account with id={} was finished with failure", account.getId());
                throw new AccountServiceException("cannot withdraw money from the account", e);
            }
        });

        return updatedAccount;

    }

    public String transferMoneyBetweenAccountsInRubles(Long sourceAccountId, Long targetAccountId, BigDecimal amountOfMoney) throws AccountServiceException, NotEnoughMoneyException {

        checkAmountOfMoneyIsCorrect(amountOfMoney);

        Optional<Account> sourceAccount = accountStorage.getAccountById(sourceAccountId);
        Optional<Account> targetAccount = accountStorage.getAccountById(targetAccountId);

        checkAccountExists(sourceAccountId, sourceAccount);
        checkAccountExists(targetAccountId, targetAccount);

        processInTransaction(() -> {
            sourceAccount.get().withdrawFromAccount(amountOfMoney);
            targetAccount.get().replenishAccount(amountOfMoney);
            Ebean.save(sourceAccount.get());
            Ebean.save(targetAccount.get());
            log.debug("transferring from the account with id={} to id={} was successful", sourceAccount.get().getId(), targetAccount.get().getId());
        });

        return "transferring money was proceeded successfully";
    }

    private void checkAmountOfMoneyIsCorrect(BigDecimal amountOfMoney) throws IncorrectFormatAmountOfMoneyException {
        if (amountOfMoney.compareTo(BigDecimal.ZERO) < 0) {
            log.error("cannot proceed an operation due to incorrect amount of money: {}. Must be positive", amountOfMoney);
            throw new IncorrectFormatAmountOfMoneyException("amountOfMoney must be a positive value");
        }
    }

    private void checkAccountExists(Long accountId, Optional<Account> account) throws AccountNotFoundException {
        if (!account.isPresent()) {
            log.error("account with id={} does not exist", accountId);
            throw new AccountNotFoundException("error while getting an account with id=" + accountId);
        }
    }

    private Account processInTransaction(AccountFunction<Account> action) throws AccountServiceException {
        Ebean.beginTransaction();
        Account account = null;
        try {
            account = action.apply();
            Ebean.commitTransaction();
            return account;
        } catch (AccountServiceException e) {
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    private void processInTransaction(AccountConsumer action) throws AccountServiceException, NotEnoughMoneyException {
        Transaction transaction = Ebean.beginTransaction();
        try {
            action.accept();
            transaction.commit();
        } catch (AccountServiceException | NotEnoughMoneyException e) {
            throw e;
        } finally {
            transaction.end();
        }
    }
}
