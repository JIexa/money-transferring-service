package models;

import io.ebean.Model;
import lombok.*;
import models.exception.NotEnoughMoneyException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.MathContext;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account extends Model {

    @Id
    private Long id;
    private Long ownerId;
    private BigDecimal amountOfMoney;
    private Currency currency;

    public Account(Long ownerId, BigDecimal amountOfMoney, Currency currency) {
        this.ownerId = ownerId;
        this.amountOfMoney = amountOfMoney;
        this.currency = currency;
    }

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


    public enum Currency {
        RU, GB, US, GER
    }

}
