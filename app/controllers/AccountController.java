package controllers;

import model.Account;
import play.mvc.Result;
import play.mvc.Results;
import service.AccountService;
import service.exception.AccountServiceException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountController {

    private AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Endpoint for creating account for the certain person
     * @param personId - id of a person in the system
     * @return RESTful response code FIXME: and, maybe, account data or just accountId
     * @throws AccountServiceException - FIXME: give more specified exception
     */
    public Result create(Long personId) throws AccountServiceException {

        accountService.createAccountFor(personId);

        return Results.ok("Account was created");
    }

    public Result getAccountFor(Long personId) throws AccountServiceException {

        Account account = accountService.getAccountFor(personId);

        return Results.ok("accountId="+account.getAccountId().toString()+", money on the account: "+account.getAmountOfMoney());
    }

    public Result getMoney() {

        return Results.ok("50 000 rub");
    }

}
