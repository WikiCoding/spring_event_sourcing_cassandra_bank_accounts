package com.wikicoding.bank_account_projections_and_recommendations.dto;

import com.wikicoding.bank_account_projections_and_recommendations.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private final String accountNumber;
    private final String accountName;
    private final double balance;
    private final long createdAt;
    private final int version;

    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.accountName = account.getAccountName();
        this.balance = account.getBalance();
        this.createdAt = account.getCreatedAt();
        this.version = account.getVersion();
    }
}

