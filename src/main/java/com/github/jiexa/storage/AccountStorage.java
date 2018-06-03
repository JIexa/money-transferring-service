package com.github.jiexa.storage;

import com.github.jiexa.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountStorage {

    List<Account> getAllAccounts();
    Optional<Account> getAccountByPersonId(Long personId);
    Account createDefaultAccountByPersonId(Long personId);
    Optional<Account> getAccountById(UUID accountId);
}
