package com.acmebank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Getter @Setter
    private String accountNumber;

    @Getter @Setter
    private BigDecimal balance;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private AccountCurrency currency;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Getter @Setter
    private String customerId;

    public Account(String accountNumber, BigDecimal balance, String customerId) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customerId = customerId;
    }
}
