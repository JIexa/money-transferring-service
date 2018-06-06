package service;

import models.exception.NotEnoughMoneyException;
import service.exception.AccountServiceException;

@FunctionalInterface
public interface AccountConsumer {

    void accept() throws AccountServiceException, NotEnoughMoneyException;
}
