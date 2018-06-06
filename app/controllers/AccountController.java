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
     * @return Result with created Account or description of exception in case of failure
     *
     */
    public Result create(Long personId) {

        return handleExceptions(
                () -> Json.toJson(accountService.createAccountFor(personId))
        );
    }

    public Result get(Long id) {

        return handleExceptions(
                () -> Json.toJson(accountService.getAccountById(id))
        );
    }

    public Result getByPerson(Long personId) {

        return handleExceptions(
                () -> Json.toJson(accountService.getAccountPersonId(personId))
        );
    }

    public Result getAll() {

        return handleExceptions(
                () -> Json.toJson(accountService.getAllAccounts())
        );
    }

    public Result replenish(Long id, Double amount)  {

        return handleExceptions(
                () -> Json.toJson(accountService.putMoneyIntoAccountInRubles(id, new BigDecimal(amount)))
        );

    }

    public Result withdraw(Long id, Double amount){

        return handleExceptions(
                () -> Json.toJson(accountService.withdrawMoneyFromAccountInRubles(id, new BigDecimal(amount)))
        );

    }

    public Result transfer() {

        TransferMoneyDto dto = Json.fromJson(request().body().asJson(), TransferMoneyDto.class);

        return handleExceptions(
                () -> Json.toJson(
                        accountService.transferMoneyBetweenAccountsInRubles(
                            dto.getSourceAccountId(),
                            dto.getTargetAccountId(),
                            new BigDecimal(dto.getAmountOfMoney())
                        )
                )
        );
    }

    private Result handleExceptions(ExceptionHandler<JsonNode> handler) {
        try {
            return Results.ok(Json.toJson(handler.handleOperation()));
        } catch (AccountAlreadyExistsException e) {
            return Results.badRequest(Json.toJson(e.getLocalizedMessage()));
        } catch (AccountNotFoundException e) {
            return Results.internalServerError(Json.toJson(e.getLocalizedMessage()));
        } catch (AccountServiceException e) {
            return Results.badRequest(Json.toJson(e.getLocalizedMessage()));
        } catch (NotEnoughMoneyException e) {
            return Results.notFound(Json.toJson(e.getLocalizedMessage()));
        }
    }

}
