package com.acmebank;

import com.acmebank.dto.TransferMoneyRequest;
import com.acmebank.model.Account;
import com.acmebank.model.TransferRecord;
import com.acmebank.repo.AccountRepository;
import com.acmebank.repo.TransferRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class TransferMoneyService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRecordRepository transferRecordRepository;

    @Transactional
    public String transferMoney(TransferMoneyRequest request, String idempotencyKey) {
        TransferRecord record = transferRecordRepository.findByIdempotencyKey(idempotencyKey);
        String transferResponse;

        Account sendingAccount = accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getSendingAccountNumber());
        Account receivingAccount = accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getReceivingAccountNumber());
        if (sendingAccount == null) {
            transferResponse = String.format("Account number %s does not exist under customerID %s.", request.getSendingAccountNumber(), request.getCustomerId());
        } else if (receivingAccount == null) {
            transferResponse = String.format("Account number %s does not exist under customerID %s.", request.getReceivingAccountNumber(), request.getCustomerId());
        } else if (sendingAccount.getBalance().compareTo(request.getAmount()) < 0) {
            transferResponse = String.format("Account number %s under customerID %s does not have enough money to transfer.", request.getSendingAccountNumber(), request.getCustomerId());
        } else {
            BigDecimal sendingAccountBalance = sendingAccount.getBalance().subtract(request.getAmount());
            BigDecimal receivingAccountBalance = receivingAccount.getBalance().add(request.getAmount());
            accountRepository.updateBalance(sendingAccountBalance, request.getSendingAccountNumber(), request.getCustomerId());
            accountRepository.updateBalance(receivingAccountBalance, request.getReceivingAccountNumber(), request.getCustomerId());
            transferResponse = String.format("Successfully transfer %s from account number %s to account number %s.", request.getAmount(), request.getSendingAccountNumber(), request.getReceivingAccountNumber());
        }

        transferRecordRepository.updateTransferResponse(transferResponse, idempotencyKey);
        return transferResponse;
    }
}
