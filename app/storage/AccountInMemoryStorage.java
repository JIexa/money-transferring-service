package storage;

import models.Account;
import storage.exception.AccountAlreadyExistsException;

import java.math.BigDecimal;
import java.util.*;

public class AccountInMemoryStorage implements AccountStorage {

    private final Map<Long, Account> accountStorageByPerson = new HashMap<>();

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accountStorageByPerson.values());
    }

    @Override
    public Optional<Account> getAccountByPersonId(Long personId) {

        return Optional.ofNullable(accountStorageByPerson.get(personId));
    }

    @Override
    public Account createDefaultAccountByPersonId(Long personId) throws AccountAlreadyExistsException {
        Account account = new Account(
                new Random().longs().filter(l -> l >= 0).findAny().getAsLong(),
                personId, new BigDecimal(0),
                Account.Currency.RU
        );

        if (accountStorageByPerson.containsKey(personId)) {
            throw new AccountAlreadyExistsException(String.format("account for the person with id=%d", personId));
        }
        accountStorageByPerson.put(personId, account);

        return account;
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
//      FIXME: this approach is ineffective in large amount of accounts. Implements some benchmarks before searching another approach
        for (Account account : accountStorageByPerson.values()) {
            if (account.getId().equals(id)) {
                return Optional.of(account);
            }
        }

        return Optional.ofNullable(null);
    }
}
