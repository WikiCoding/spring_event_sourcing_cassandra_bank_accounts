package com.wikicoding.bank_account_lifecycle_engine.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAccountCommand implements Command {
    private final String accountName;
    private final double startBalance;
}
