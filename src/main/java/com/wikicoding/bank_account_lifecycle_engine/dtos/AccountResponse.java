package com.wikicoding.bank_account_lifecycle_engine.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private final String accountNumber;
    private final double currentBalance;
    private final int currentVersion;
}
