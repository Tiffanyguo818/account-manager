package com.acmebank.repo;

import com.acmebank.model.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByCustomerIdAndAccountNumber(String customerId, String accountNumber);

    @Transactional
    @Modifying
    @Query("update Account a set a.balance = :balance where a.accountNumber = :accountNumber and a.customerId = :customerId")
    void updateBalance(@Param("balance") BigDecimal balance, @Param("accountNumber") String accountNumber, @Param("customerId") String customerId);
}
