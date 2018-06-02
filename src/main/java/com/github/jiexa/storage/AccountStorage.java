package com.github.jiexa.storage;

import com.github.jiexa.model.Account;

import java.util.Optional;

public interface AccountStorage {

    Optional<Account> getAccountByPersonId(Long personId);
    Account createAccountByPersonId(Long personId);
}
