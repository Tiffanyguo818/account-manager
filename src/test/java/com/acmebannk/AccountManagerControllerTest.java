package com.acmebannk;

import com.acmebank.AccountManagerController;
import com.acmebank.AccountManagerException;
import com.acmebank.dto.AccountBalanceRequest;
import com.acmebank.dto.TransferMoneyRequest;
import com.acmebank.model.TransferRecord;
import com.acmebank.repo.AccountRepository;
import com.acmebank.repo.TransferRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountManagerControllerTest {
    private static final String CUSTOMER_ID = "tester";
    private static final String ACCOUNT_NUMBER = "12345678";
    private static final String IDEMPOTENCY_KEY = "5cb2786a-b2c8-4a24-8334-898c1874be8a";
    private static final String TRANSFER_RESPONSE = "Transfer successfully";
    
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransferRecordRepository transferRecordRepository;
    @InjectMocks
    private AccountManagerController accountManagerController;

    @Test
    public void getAccountBalance_AccountNotExist() {
        AccountBalanceRequest request = new AccountBalanceRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setAccountNumber(ACCOUNT_NUMBER);

        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getAccountNumber())).thenReturn(null);
        Exception exception = assertThrows(AccountManagerException.class, () -> {
            accountManagerController.getAccountBalance(request);
        });
        assertEquals(String.format("account number %s does not exist under customerID %s",
                ACCOUNT_NUMBER, CUSTOMER_ID), exception.getMessage());
    }

    @Test
    public void transferMoney_InvalidIdempotencyKey() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setSendingAccountNumber(ACCOUNT_NUMBER);
        request.setReceivingAccountNumber(ACCOUNT_NUMBER);
        request.setAmount(new BigDecimal("100"));

        Exception exception = assertThrows(AccountManagerException.class, () -> {
            accountManagerController.transferMoney(request, "123");
        });
        assertEquals("idempotency-key should be a valid UUID", exception.getMessage());
    }

    @Test
    public void transferMoney_DuplicateRequest() throws AccountManagerException{
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setSendingAccountNumber(ACCOUNT_NUMBER);
        request.setReceivingAccountNumber(ACCOUNT_NUMBER);
        request.setAmount(new BigDecimal("100"));

        TransferRecord record = new TransferRecord(IDEMPOTENCY_KEY);
        record.setTransferResponse(TRANSFER_RESPONSE);
        when(transferRecordRepository.findByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(record);
        assertEquals(TRANSFER_RESPONSE, accountManagerController.transferMoney(request, IDEMPOTENCY_KEY));
    }
}
