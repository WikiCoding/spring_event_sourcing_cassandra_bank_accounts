package com.wikicoding.bank_account_lifecycle_engine.commands;

import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommandDetails {
    private final String accountNumber;
    private final DomainEvent domainEvent;
}
