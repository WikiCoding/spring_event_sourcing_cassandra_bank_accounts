package com.wikicoding.bank_account_lifecycle_engine.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositMoneyRequest {
    private final double amount;
}
