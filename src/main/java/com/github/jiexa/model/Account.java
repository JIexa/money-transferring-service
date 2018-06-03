package com.github.jiexa.model;

import com.github.jiexa.Currency;
import com.github.jiexa.model.exception.NotEnoughMoneyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Account {
    private UUID accountId;
    private Long ownerId;
    private BigDecimal amountOfMoney;
    private Currency currency;

    public void replenishAccount(BigDecimal addingAmount) {
        addingAmount = getRoundedBigDecimal(addingAmount);
        amountOfMoney = amountOfMoney.add(addingAmount);
    }

    public void withdrawFromAccount(BigDecimal withdrawingAmount) throws NotEnoughMoneyException {
        withdrawingAmount = getRoundedBigDecimal(withdrawingAmount);
        BigDecimal calculation = getRoundedBigDecimal(amountOfMoney.subtract(withdrawingAmount));
        if (isEnoughMoney(calculation)) {
            amountOfMoney = calculation;
        } else {
            throw new NotEnoughMoneyException("Not enough money to proceed withdrawing");
        }
    }

    private boolean isEnoughMoney(BigDecimal calculation) {
        return calculation.compareTo(getRoundedBigDecimal(new BigDecimal(0.0))) >= 0;
    }

    private BigDecimal getRoundedBigDecimal(BigDecimal value) {
        return value.round(MathContext.DECIMAL64);
    }


}
