package com.acmebank.repo;

import com.acmebank.model.TransferRecord;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

public interface TransferRecordRepository extends CrudRepository<TransferRecord, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    TransferRecord findByIdempotencyKey(String idempotencyKey);

    @Transactional
    @Modifying
    @Query("update TransferRecord r set r.transferResponse = :transferResponse where r.idempotencyKey = :idempotencyKey")
    void updateTransferResponse(String transferResponse, String idempotencyKey);
}
