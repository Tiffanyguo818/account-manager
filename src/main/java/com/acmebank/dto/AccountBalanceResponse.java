package com.acmebank.dto;

import com.acmebank.model.AccountCurrency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class AccountBalanceResponse {
    private String customerId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountCurrency currency;
}
