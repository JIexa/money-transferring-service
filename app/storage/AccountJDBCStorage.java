package storage;

import io.ebean.Ebean;
import models.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.exception.AccountAlreadyExistsException;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Singleton
public class AccountJDBCStorage implements AccountStorage {

    private final Logger log = LoggerFactory.getLogger(AccountJDBCStorage.class);

    @Override
    public List<Account> getAllAccounts() {

        return Ebean.find(Account.class).findList();

    }

    @Override
    public Optional<Account> getAccountByPersonId(Long personId) {
        return Optional.ofNullable(
                Ebean.find(Account.class)
                        .where()
                        .eq("owner_id", personId)
                        .findOne()
        );
    }

    @Override
    public Account createDefaultAccountByPersonId(Long personId) throws AccountAlreadyExistsException {

        Account oldAcc = Ebean.find(Account.class)
                .where()
                .eq("owner_id", personId)
                .and()
                .eq("currency", Account.Currency.RU)
                .findOne();

        if (oldAcc != null) {
            throw new AccountAlreadyExistsException("for this person with the same currency");
        }

        Account account = new Account(
                personId, new BigDecimal(0),
                Account.Currency.RU);
        Ebean.save(account);
        return account;
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return Optional.ofNullable(
                Ebean.find(Account.class, id)
        );
    }
}
