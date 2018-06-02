package com.github.jiexa.storage;

import com.github.jiexa.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountStorage {

    List<Account> getAllAccounts();
    Optional<Account> getAccountByPersonId(Long personId);
    Account createAccountByPersonId(Long personId);
}
