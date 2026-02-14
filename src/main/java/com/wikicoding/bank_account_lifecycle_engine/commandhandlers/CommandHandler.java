package com.wikicoding.bank_account_lifecycle_engine.commandhandlers;

import com.wikicoding.bank_account_lifecycle_engine.commands.*;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.event_store.EventStore;
import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.exceptions.NotYetImplementedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private final EventStore eventStore;

    public Account executeCommand(Command cmd) {
        DomainEvent domainEvent = extractCommandDetails(cmd);

        List<DomainEvent> domainEvents = eventStore.getAccountEvents(cmd.getAccountNumber());

        Account account = new Account();
        account.rebuildState(domainEvents);
        if (account.getAccountNumber() != null) log.info("Account rebuilt state: {}", account);
        account.apply(domainEvent);

        eventStore.persistState(account.getDomainEvents());
        log.info("Account persisted state for accountNumber: {}", account.getAccountNumber());

        return account;
    }

    private DomainEvent extractCommandDetails(Command cmd) {
        DomainEvent domainEvent;

        switch (cmd) {
            case CreateAccountCommand command -> {
                domainEvent = new CreatedAccountEvent(
                        command.getAccountNumber(),
                        command.getAccountName(),
                        command.getStartBalance(),
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        1
                );
                log.info("Creating account with account number: {}", command.getAccountNumber());
            }
            case DepositMoneyCommand command -> {
                domainEvent = new DepositedMoneyEvent(command.getAccountNumber(), command.getAmount());
                log.info("Depositing {}€ in account number {}", command.getAmount(), command.getAccountNumber());
            }
            case WithdrawMoneyCommand command -> {
                domainEvent = new WithdrewMoneyEvent(command.getAccountNumber(), command.getAmount());
                log.info("Withdrawing {}€ in account number {}", command.getAmount(), command.getAccountNumber());
            }
            default -> {
                log.error("Unknown command or not yet implemented: {}", cmd.getClass().getSimpleName());
                throw new NotYetImplementedException("Unknown Command or not yet implemented "
                        + cmd.getClass().getSimpleName());
            }
        }

        return domainEvent;
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        List<DomainEvent> domainEvents = eventStore.getAccountEvents(accountNumber);

        Account account = new Account();
        account.rebuildState(domainEvents);

        return account;
    }
}
