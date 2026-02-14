package com.wikicoding.bank_account_lifecycle_engine.services;

import com.wikicoding.bank_account_lifecycle_engine.commands.*;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.event_store.EventStore;
import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandHandler {
    private final EventStore eventStore;

    public void executeCommand(Command cmd) {
        CommandDetails commandDetails = extractCommandDetails(cmd);

        List<DomainEvent> domainEvents = eventStore.getAccountEvents(commandDetails.getAccountNumber());

        Account account = new Account();
        account.rebuildState(domainEvents);
        account.apply(commandDetails.getDomainEvent());

        eventStore.persistState(account.getDomainEvents());
    }

    private CommandDetails extractCommandDetails(Command cmd) {
        String accountNumber;
        DomainEvent domainEvent;

        switch (cmd) {
            case CreateAccountCommand command -> {
                accountNumber = UUID.randomUUID().toString();
                domainEvent = new CreatedAccountEvent(
                        UUID.randomUUID().toString(),
                        command.getAccountName(),
                        command.getStartBalance(),
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        1
                );
            }
            case DepositMoneyCommand command -> {
                domainEvent = new DepositedMoneyEvent(command.getAccountNumber(), command.getAmount());
                accountNumber = command.getAccountNumber();
            }
            case WithdrawMoneyCommand command -> {
                domainEvent = new WithdrewMoneyEvent(command.getAccountNumber(), command.getAmount());
                accountNumber = command.getAccountNumber();
            }
            default -> throw new IllegalArgumentException("Unknown Command " + cmd.getClass().getSimpleName());
        }

        return new CommandDetails(accountNumber, domainEvent);
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        List<DomainEvent> domainEvents = eventStore.getAccountEvents(accountNumber);

        Account account = new Account();
        account.rebuildState(domainEvents);

        return account;
    }
}
