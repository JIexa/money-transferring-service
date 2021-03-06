import com.google.inject.AbstractModule;
import service.AccountService;
import storage.AccountInMemoryStorage;
import storage.AccountJDBCStorage;
import storage.AccountStorage;



public class Module extends AbstractModule {

    @Override
    public void configure() {

        bind(AccountStorage.class).to(AccountJDBCStorage.class);

    }

}
