package com.acmebank;

import com.acmebank.dto.AccountBalanceRequest;
import com.acmebank.dto.AccountBalanceResponse;
import com.acmebank.dto.TransferMoneyRequest;
import com.acmebank.model.Account;
import com.acmebank.model.TransferRecord;
import com.acmebank.repo.AccountRepository;
import com.acmebank.repo.TransferRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountManagerController {
    private Logger logger = LoggerFactory.getLogger(TransferMoneyService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRecordRepository transferRecordRepository;

    @Autowired
    private TransferMoneyService transferMoneyService;

    @PostMapping("/account/balance")
    public AccountBalanceResponse getAccountBalance(@Valid @RequestBody AccountBalanceRequest request) throws AccountManagerException{
        Account account = accountRepository.findByCustomerIdAndAccountNumber(request.getCustomerId(), request.getAccountNumber());
        if (account == null) {
            throw new AccountManagerException(String.format("account number %s does not exist under customerID %s", request.getAccountNumber(), request.getCustomerId()));
        }
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setAccountNumber(request.getAccountNumber());
        response.setCustomerId(request.getCustomerId());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        return response;
    }

    @PostMapping("/account/transfer")
    public String transferMoney(@Valid @RequestBody TransferMoneyRequest request, @RequestHeader(value="idempotency-key") String idempotencyKey) throws AccountManagerException {
        if (!AccountManagerUtil.validateUUID(idempotencyKey)) {
            throw new AccountManagerException("idempotency-key should be a valid UUID");
        }
        TransferRecord transferRecord = transferRecordRepository.findByIdempotencyKey(idempotencyKey);
        if (transferRecord != null && transferRecord.getTransferResponse() != null) {
            logger.info(String.format("Duplicate transfer money request with idempotencyKey %s", idempotencyKey));
            return transferRecord.getTransferResponse();
        }

        if (transferRecord == null) {
            transferRecordRepository.save(new TransferRecord(idempotencyKey));
        }
        return transferMoneyService.transferMoney(request, idempotencyKey);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return String.format("Invalid request: %s.", String.join(", ", errors));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public String handleHeaderException(MissingRequestHeaderException ex) {
        List<String> errors = new ArrayList<>();
        return String.format("Invalid request: %s.", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountManagerException.class)
    public String handleAccountManagerException(AccountManagerException ex) {
        return String.format("Invalid request: %s.", ex.getMessage());
    }
}
