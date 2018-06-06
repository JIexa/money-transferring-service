package service;

import service.exception.AccountServiceException;

@FunctionalInterface
public interface AccountFunction<R> {

    R apply() throws AccountServiceException;
}
