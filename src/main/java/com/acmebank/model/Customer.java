package com.acmebank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
@Entity
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    private String customerId;

    @OneToMany(mappedBy = "customerId")
    @Getter @Setter
    private List<Account> accounts;

    public Customer(String customerId) {
        this.customerId = customerId;
    }
}
