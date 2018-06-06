package controllers;

import models.exception.NotEnoughMoneyException;
import service.exception.AccountNotFoundException;
import service.exception.AccountServiceException;
import storage.exception.AccountAlreadyExistsException;

@FunctionalInterface
public interface ExceptionHandler<R> {
    R handleOperation() throws AccountAlreadyExistsException, AccountServiceException, NotEnoughMoneyException;
}
