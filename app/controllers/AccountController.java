package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.dto.TransferMoneyDto;
import models.exception.NotEnoughMoneyException;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import service.AccountService;
import service.exception.AccountNotFoundException;
import service.exception.AccountServiceException;
import service.exception.IncorrectFormatAmountOfMoneyException;
import storage.exception.AccountAlreadyExistsException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

import static play.mvc.Controller.request;

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
    public Result create(Long personId) throws AccountAlreadyExistsException {

        JsonNode account = Json.toJson(accountService.createAccountFor(personId));

        return Results.ok("Account was created: " + account);
    }

    public Result get(Long id) throws AccountServiceException {

        JsonNode account = Json.toJson(accountService.getAccountById(id));

        return Results.ok(account);
    }

    public Result getByPerson(Long personId) throws AccountServiceException {

        JsonNode account = Json.toJson(accountService.getAccountPersonId(personId));

        return Results.ok(account);
    }

    public Result getAll() {

        JsonNode accounts = Json.toJson(accountService.getAllAccounts());

        return Results.ok(accounts);
    }

    public Result replenish(Long id, Double amount) throws AccountServiceException {

        Account account = accountService.putMoneyIntoAccountInRubles(id, new BigDecimal(amount));

        JsonNode jsonAccount = Json.toJson(account);

        return Results.ok(jsonAccount);
    }

    public Result withdraw(Long id, Double amount) throws AccountServiceException {

        Account account = accountService.withdrawMoneyFromAccountInRubles(id, new BigDecimal(amount));

        JsonNode jsonAccount = Json.toJson(account);

        return Results.ok(jsonAccount);
    }

    public Result transfer() throws AccountServiceException, NotEnoughMoneyException {

        TransferMoneyDto dto = Json.fromJson(request().body().asJson(), TransferMoneyDto.class);

        accountService.transferMoneyBetweenAccountsInRubles(dto.getSourceAccountId(), dto.getTargetAccountId(), new BigDecimal(dto.getAmountOfMoney()));

        return Results.ok(String.format("%s was transferred from account with id=%d to id=%d", dto.getAmountOfMoney(), dto.getSourceAccountId(), dto.getTargetAccountId()));
    }

}
