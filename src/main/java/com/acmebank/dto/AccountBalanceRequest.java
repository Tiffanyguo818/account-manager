package com.acmebank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter @NoArgsConstructor
public class AccountBalanceRequest {

    @NotBlank(message="customerId is mandatory")
    private String customerId;

    @NotBlank(message="accountNumber is mandatory")
    @Pattern(regexp="\\d+", message="accountNumber should only contains numbers")
    private String accountNumber;
}
