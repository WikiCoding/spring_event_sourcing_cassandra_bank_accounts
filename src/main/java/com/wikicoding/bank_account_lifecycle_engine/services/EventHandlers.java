package com.wikicoding.bank_account_lifecycle_engine.services;

import com.wikicoding.bank_account_lifecycle_engine.commands.CreateAccountCommand;
import com.wikicoding.bank_account_lifecycle_engine.commands.DepositMoneyCommand;
import com.wikicoding.bank_account_lifecycle_engine.commands.WithdrawMoneyCommand;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.event_store.EventStore;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventHandlers {
    private final EventStore eventStore;

    public void createAccountEventHandler(CreateAccountCommand command) {
        Account account = new Account(command);
        eventStore.persistState(account.getDomainEvents());
    }

    public void depositMoneyEventHandler(DepositMoneyCommand command) {
        DepositedMoneyEvent depositedMoneyEvent = new DepositedMoneyEvent(command.getAccountNumber(), command.getAmount());

        List<DomainEvent> domainEvents = eventStore.getAccountEvents(command.getAccountNumber());

        Account account = new Account();
        account.rebuildState(domainEvents);
        account.applyDepositMoneyEvent(depositedMoneyEvent);

        eventStore.persistState(account.getDomainEvents());
    }

    public void withdrawMoneyEventHandler(WithdrawMoneyCommand command) {
        WithdrewMoneyEvent withdrewMoneyEvent = new WithdrewMoneyEvent(command.getAccountNumber(), command.getAmount());

        List<DomainEvent> domainEvents = eventStore.getAccountEvents(command.getAccountNumber());

        Account account = new Account();
        account.rebuildState(domainEvents);
        account.applyWithdrewMoneyEvent(withdrewMoneyEvent);

        eventStore.persistState(account.getDomainEvents());
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        List<DomainEvent> domainEvents = eventStore.getAccountEvents(accountNumber);

        Account account = new Account();
        account.rebuildState(domainEvents);

        return account;
    }
}
