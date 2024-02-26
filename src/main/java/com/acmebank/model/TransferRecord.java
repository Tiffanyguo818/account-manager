package com.acmebank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter @NoArgsConstructor
public class TransferRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Getter @Setter
    private String idempotencyKey;

    @Getter @Setter
    private String transferResponse;

    public TransferRecord(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
