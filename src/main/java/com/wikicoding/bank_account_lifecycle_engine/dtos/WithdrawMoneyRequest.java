package com.wikicoding.bank_account_lifecycle_engine.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WithdrawMoneyRequest {
    private final double amount;
}
