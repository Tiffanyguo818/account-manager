package com.acmebannk;

import com.acmebank.TransferMoneyService;
import com.acmebank.dto.TransferMoneyRequest;
import com.acmebank.model.Account;
import com.acmebank.repo.AccountRepository;
import com.acmebank.repo.TransferRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferMoneyServiceTest {
    private static final String CUSTOMER_ID = "tester";
    private static final String SENDING_ACCOUNT_NUMBER = "12345678";
    private static final String RECEIVING_ACCOUNT_NUMBER = "88888888";
    private static final String IDEMPOTENCY_KEY = "5cb2786a-b2c8-4a24-8334-898c1874be8a";
    private static final BigDecimal ACCOUNT_BALANCE = new BigDecimal("1000000");
    private static final Account SENDING_ACCOUNT = new Account(SENDING_ACCOUNT_NUMBER, ACCOUNT_BALANCE, CUSTOMER_ID);
    private static final Account RECEIVING_ACCOUNT = new Account(RECEIVING_ACCOUNT_NUMBER, ACCOUNT_BALANCE, CUSTOMER_ID);


    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransferRecordRepository transferRecordRepository;
    @InjectMocks
    private TransferMoneyService transferMoneyService;

    @Test
    public void transferMoney_SendingAccountNotExist() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setSendingAccountNumber(SENDING_ACCOUNT_NUMBER);
        request.setReceivingAccountNumber(RECEIVING_ACCOUNT_NUMBER);
        request.setAmount(new BigDecimal("10.00"));

        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getSendingAccountNumber())).thenReturn(null);
        assertEquals(String.format("Account number %s does not exist under customerID %s.",
                SENDING_ACCOUNT_NUMBER, CUSTOMER_ID), transferMoneyService.transferMoney(request, IDEMPOTENCY_KEY));
    }

    @Test
    public void transferMoney_ReceivingAccountNotExist() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setSendingAccountNumber(SENDING_ACCOUNT_NUMBER);
        request.setReceivingAccountNumber(RECEIVING_ACCOUNT_NUMBER);
        request.setAmount(new BigDecimal("10.00"));

        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getSendingAccountNumber())).thenReturn(SENDING_ACCOUNT);
        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getReceivingAccountNumber())).thenReturn(null);
        assertEquals(String.format("Account number %s does not exist under customerID %s.",
                RECEIVING_ACCOUNT_NUMBER, CUSTOMER_ID), transferMoneyService.transferMoney(request, IDEMPOTENCY_KEY));
    }

    @Test
    public void transferMoney_AmountNotEnough() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setSendingAccountNumber(SENDING_ACCOUNT_NUMBER);
        request.setReceivingAccountNumber(RECEIVING_ACCOUNT_NUMBER);
        request.setAmount(new BigDecimal("1000001"));

        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getSendingAccountNumber())).thenReturn(SENDING_ACCOUNT);
        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getReceivingAccountNumber())).thenReturn(RECEIVING_ACCOUNT);
        assertEquals(String.format("Account number %s under customerID tester does not have enough money to transfer.",
                SENDING_ACCOUNT_NUMBER, CUSTOMER_ID), transferMoneyService.transferMoney(request, IDEMPOTENCY_KEY));
    }

    @Test
    public void transferMoney_Successful() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setSendingAccountNumber(SENDING_ACCOUNT_NUMBER);
        request.setReceivingAccountNumber(RECEIVING_ACCOUNT_NUMBER);
        request.setAmount(new BigDecimal("500000"));

        String expectedTransferResponse = String.format("Successfully transfer %s from account number %s to account number %s.",
                request.getAmount(), SENDING_ACCOUNT_NUMBER, RECEIVING_ACCOUNT_NUMBER);
        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getSendingAccountNumber())).thenReturn(SENDING_ACCOUNT);
        when(accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getReceivingAccountNumber())).thenReturn(RECEIVING_ACCOUNT);
        assertEquals(expectedTransferResponse, transferMoneyService.transferMoney(request, "5cb2786a-b2c8-4a24-8334-898c1874be8a"));
        Mockito.verify(accountRepository).updateBalance(new BigDecimal(500000), SENDING_ACCOUNT_NUMBER, CUSTOMER_ID);
        Mockito.verify(accountRepository).updateBalance(new BigDecimal(1500000), RECEIVING_ACCOUNT_NUMBER, CUSTOMER_ID);
        Mockito.verify(transferRecordRepository).updateTransferResponse(expectedTransferResponse, IDEMPOTENCY_KEY);
    }
}
