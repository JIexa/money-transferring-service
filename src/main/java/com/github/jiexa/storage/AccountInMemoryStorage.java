package com.github.jiexa.storage;

import com.github.jiexa.model.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountInMemoryStorage implements AccountStorage {

    private final Map<Long, Account> accountStorageByPerson = new HashMap<>();

    @Override
    public Optional<Account> getAccountByPersonId(Long personId) {

        return Optional.ofNullable(accountStorageByPerson.get(personId));
    }

    @Override
    public Account createAccountByPersonId(Long personId) {
        Account account = new Account(UUID.randomUUID(), personId);

        accountStorageByPerson.put(personId, account);

        return account;
    }
}
