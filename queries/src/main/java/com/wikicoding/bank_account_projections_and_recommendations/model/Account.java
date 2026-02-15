package com.wikicoding.bank_account_projections_and_recommendations.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {
    @Id
    private String accountNumber;
    private String accountName;
    private double balance;
    private long createdAt;
    private int version;
}
