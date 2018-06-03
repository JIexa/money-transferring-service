package storage;

import model.Account;
import model.Currency;

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
    public Account createDefaultAccountByPersonId(Long personId) {
        Account account = new Account(UUID.randomUUID(), personId, new BigDecimal(0), Currency.RU);

        accountStorageByPerson.put(personId, account);

        return account;
    }

    @Override
    public Optional<Account> getAccountById(UUID accountId) {
//      FIXME: this approach is ineffective in large amount of accounts. Implements some benchmarks before searching another approach
        for (Account account : accountStorageByPerson.values()) {
            if (account.getAccountId().equals(accountId)) {
                return Optional.of(account);
            }
        }

        return Optional.ofNullable(null);
    }
}