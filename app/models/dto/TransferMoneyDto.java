package models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferMoneyDto {
    private Long sourceAccountId;
    private Long targetAccountId;
    private Double amountOfMoney;
}
