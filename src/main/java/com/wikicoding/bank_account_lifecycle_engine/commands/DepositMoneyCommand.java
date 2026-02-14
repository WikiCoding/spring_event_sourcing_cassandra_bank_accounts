package com.wikicoding.bank_account_lifecycle_engine.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DepositMoneyCommand implements Command {
    private final String accountNumber;
    private final double amount;
}
