package service;

import models.exception.NotEnoughMoneyException;
import service.exception.AccountServiceException;

@FunctionalInterface
public interface AccountConsumer<T> {

    void accept(T t) throws AccountServiceException, NotEnoughMoneyException;
}
