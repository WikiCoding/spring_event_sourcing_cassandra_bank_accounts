package com.wikicoding.bank_account_lifecycle_engine.handlers;

import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.event_store.EventStore;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryHandler {
    private final EventStore eventStore;

    public Account getAccountByAccountNumber(String accountNumber) {
        log.info("Processing query to get account by accountNumber {}", accountNumber);
        Optional<Account> accountSnapshot = eventStore.getAccountSnapshotState(accountNumber);

        return accountSnapshot.orElse(processWithoutSnapshot(accountNumber));
    }

    private @NonNull Account processWithoutSnapshot(String accountNumber) {
        List<DomainEvent> domainEvents = eventStore.getAccountEvents(accountNumber);

        if (domainEvents.isEmpty()) {
            log.error("No events found for account number: {}", accountNumber);
            throw new AccountNotFoundException("No events found for account number: " + accountNumber);
        }

        Account account = new Account();
        account.rebuildState(domainEvents);

        return account;
    }
}
