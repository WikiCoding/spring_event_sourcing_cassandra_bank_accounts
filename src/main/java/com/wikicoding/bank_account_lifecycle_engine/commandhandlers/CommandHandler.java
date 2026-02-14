package com.wikicoding.bank_account_lifecycle_engine.commandhandlers;

import com.wikicoding.bank_account_lifecycle_engine.commands.*;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.event_store.EventStore;
import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private final EventStore eventStore;

    public Account executeCommand(Command cmd) {
        CommandDetails commandDetails = extractCommandDetails(cmd);

        List<DomainEvent> domainEvents = eventStore.getAccountEvents(commandDetails.getAccountNumber());

        Account account = new Account();
        account.rebuildState(domainEvents);
        if (account.getAccountNumber() != null) log.info("Account rebuilt state: {}", account);
        account.apply(commandDetails.getDomainEvent());

        eventStore.persistState(account.getDomainEvents());
        log.info("Account persisted state for accountNumber: {}", account.getAccountNumber());

        return account;
    }

    private CommandDetails extractCommandDetails(Command cmd) {
        String accountNumber;
        DomainEvent domainEvent;

        switch (cmd) {
            case CreateAccountCommand command -> {
                accountNumber = command.getAccountNumber();
                domainEvent = new CreatedAccountEvent(
                        accountNumber,
                        command.getAccountName(),
                        command.getStartBalance(),
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        1
                );
                log.info("Creating account with account number: {}", accountNumber);
            }
            case DepositMoneyCommand command -> {
                domainEvent = new DepositedMoneyEvent(command.getAccountNumber(), command.getAmount());
                accountNumber = command.getAccountNumber();
                log.info("Depositing {}€ in account number {}", command.getAmount(), accountNumber);
            }
            case WithdrawMoneyCommand command -> {
                domainEvent = new WithdrewMoneyEvent(command.getAccountNumber(), command.getAmount());
                accountNumber = command.getAccountNumber();
                log.info("Withdrawing {}€ in account number {}", command.getAmount(), accountNumber);
            }
            default -> {
                log.error("Unknown command or not yet implemented: {}", cmd.getClass().getSimpleName());
                throw new NotYetImplementedException("Unknown Command or not yet implemented "
                        + cmd.getClass().getSimpleName());
            }
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
