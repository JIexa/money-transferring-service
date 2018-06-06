package storage;

import models.Account;
import storage.exception.AccountAlreadyExistsException;

import java.util.List;
import java.util.Optional;

public interface AccountStorage {

    List<Account> getAllAccounts();
    Optional<Account> getAccountByPersonId(Long personId);
    Account createDefaultAccountByPersonId(Long personId) throws AccountAlreadyExistsException;
    Optional<Account> getAccountById(Long id);
}
