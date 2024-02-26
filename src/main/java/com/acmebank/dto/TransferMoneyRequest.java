package com.acmebank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class TransferMoneyRequest {

    @NotBlank(message="customerId is mandatory")
    private String customerId;

    @NotBlank(message="sending accountNumber is mandatory")
    @Pattern(regexp="\\d+", message="sending accountNumber should only contains numbers")
    private String sendingAccountNumber;

    @NotBlank(message="receiving accountNumber is mandatory")
    @Pattern(regexp="\\d+", message="receiving accountNumber should only contains numbers")
    private String receivingAccountNumber;

    @NotNull(message="amount is mandatory")
    @DecimalMin(value="0", inclusive=false, message="transfer amount should be larger than 0")
    private BigDecimal amount;
}
