package com.wikicoding.bank_account_lifecycle_engine.handlers;

import com.wikicoding.bank_account_lifecycle_engine.commands.*;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.event_store.EventStore;
import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.exceptions.AccountNotFoundException;
import com.wikicoding.bank_account_lifecycle_engine.exceptions.NotYetImplementedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private final EventStore eventStore;

    public Account executeCommand(Command cmd) {
        log.info("Processing command {}", cmd);
        DomainEvent domainEvent = extractCommandDetails(cmd);

        Optional<Account> accountSnapshot = eventStore.getAccountSnapshotState(cmd.getAccountNumber());

        return accountSnapshot.map(account -> processUsingSnapshot(account, domainEvent))
                .orElseGet(() -> processWithoutSnapshot(cmd, domainEvent));
    }

    private @NonNull Account processWithoutSnapshot(Command cmd, DomainEvent domainEvent) {
        List<DomainEvent> domainEvents = eventStore.getAccountEvents(cmd.getAccountNumber());

        if (!(domainEvent instanceof CreatedAccountEvent) && domainEvents.isEmpty()) {
            log.error("No events found for account number: {}", cmd.getAccountNumber());
            throw new AccountNotFoundException("No events found for account number: " + cmd.getAccountNumber());
        }

        Account account = new Account();
        account.rebuildState(domainEvents);
        if (account.getAccountNumber() != null) log.info("Account rebuilt state: {}", account);
        account.apply(domainEvent);

        eventStore.persistState(account.getDomainEvents(), account);
        log.info("Account persisted state for accountNumber: {}", account.getAccountNumber());

        return account;
    }

    private @NonNull Account processUsingSnapshot(Account account, DomainEvent domainEvent) {
        log.info("Processing using snapshot for accountNumber: {}", account.getAccountNumber());
        account.apply(domainEvent);
        eventStore.persistState(account.getDomainEvents(), account);
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
}
