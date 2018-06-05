package model.dto;

import lombok.Data;

@Data
public class TransferMoneyDto {
    private Long sourceAccountId;
    private Long targetAccountId;
    private Double amountOfMoney;
}
