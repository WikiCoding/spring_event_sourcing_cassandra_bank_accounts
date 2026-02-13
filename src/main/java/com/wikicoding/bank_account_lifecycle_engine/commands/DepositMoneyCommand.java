package com.wikicoding.bank_account_lifecycle_engine.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositMoneyCommand {
    private final String accountNumber;
    private final double amount;
}
