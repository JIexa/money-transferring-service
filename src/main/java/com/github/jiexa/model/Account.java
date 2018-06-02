package com.github.jiexa.model;

import com.github.jiexa.Currency;
import com.github.jiexa.model.exception.NotEnoughMoneyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Account {
    private UUID accountId;
    private Long ownerId;
    private Double amountOfMoney;
    private Currency currency;

    public void replenishAccount(Double addingAmount) {
        amountOfMoney = Double.sum(amountOfMoney, addingAmount);
    }

    public void withdrawFromAccount(Double withdrawingAmount) throws NotEnoughMoneyException {
        Double calculation = amountOfMoney - withdrawingAmount;
        if (isEnoughMoney(calculation)) {
            amountOfMoney = calculation;
        } else {
            throw new NotEnoughMoneyException("Not enough money to proceed withdrawing");
        }
    }

    private boolean isEnoughMoney(Double calculation) {
        return calculation < 0.0;
    }
}
