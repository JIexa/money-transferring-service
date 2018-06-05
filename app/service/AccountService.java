package service;

import com.google.inject.Inject;
import model.Account;
import model.exception.NotEnoughMoneyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.exception.AccountAlreadyExistsException;
import service.exception.AccountNotFoundException;
import service.exception.AccountServiceException;
import service.exception.IncorrectFormatAmountOfMoneyException;
import storage.AccountStorage;

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

    //    TODO: should I return exactly this Account entity? or it is reasonable to create specified DTO?
    public Account createAccountFor(Long personId) throws AccountServiceException {

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


    public Account putMoneyIntoAccountInRubles(Long id, BigDecimal amountOfMoney) throws AccountNotFoundException, IncorrectFormatAmountOfMoneyException {

        checkAmountOfMoneyIsCorrect(amountOfMoney);

        Optional<Account> account = accountStorage.getAccountById(id);

        checkAccountExists(id, account);

        account.get().replenishAccount(amountOfMoney);
        return account.get();
    }

    public Account withdrawMoneyFromAccountInRubles(Long id, BigDecimal amountOfMoney) throws AccountNotFoundException, NotEnoughMoneyException, IncorrectFormatAmountOfMoneyException {

        checkAmountOfMoneyIsCorrect(amountOfMoney);

        Optional<Account> account = accountStorage.getAccountById(id);

        checkAccountExists(id, account);

        account.get().withdrawFromAccount(amountOfMoney);
        return account.get();
    }

    public void transferMoneyBetweenAccountsInRubles(Long sourceAccountId, Long targetAccountId, BigDecimal amountOfMoney) throws AccountNotFoundException, NotEnoughMoneyException, IncorrectFormatAmountOfMoneyException {

        checkAmountOfMoneyIsCorrect(amountOfMoney);

        Optional<Account> sourceAccount = accountStorage.getAccountById(sourceAccountId);
        Optional<Account> targetAccount = accountStorage.getAccountById(targetAccountId);

        checkAccountExists(sourceAccountId, sourceAccount);
        checkAccountExists(targetAccountId, targetAccount);

//        TODO: implement in a transaction
        sourceAccount.get().withdrawFromAccount(amountOfMoney);
        targetAccount.get().replenishAccount(amountOfMoney);
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

}
