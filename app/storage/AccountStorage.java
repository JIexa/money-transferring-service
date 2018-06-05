package storage;

import model.Account;
import storage.exception.AccountAlreadyExistsException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountStorage {

    List<Account> getAllAccounts();
    Optional<Account> getAccountByPersonId(Long personId);
    Account createDefaultAccountByPersonId(Long personId) throws AccountAlreadyExistsException;
    Optional<Account> getAccountById(Long id);
}
