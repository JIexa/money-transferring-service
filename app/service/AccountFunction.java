package service;

import service.exception.AccountServiceException;

@FunctionalInterface
public interface AccountFunction<T, R> {

    R apply(T t) throws AccountServiceException;
}
