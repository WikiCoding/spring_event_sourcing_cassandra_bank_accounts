package com.wikicoding.bank_account_lifecycle_engine.domain;

import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.exceptions.NotEnoughFundsException;
import com.wikicoding.bank_account_lifecycle_engine.exceptions.NotYetImplementedException;
import com.wikicoding.bank_account_lifecycle_engine.repository.SnapshotDataModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Slf4j
@ToString
public class Account {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    @Getter
    private String accountNumber;
    @Getter
    private String accountName;
    @Getter
    private double balance;
    @Getter
    private long createdAt;
    @Getter
    private int version;

    public void apply(DomainEvent domainEvent) {
        switch (domainEvent) {
            case CreatedAccountEvent event -> applyCreatedAccountDomainEvent(event);
            case DepositedMoneyEvent event -> applyDepositMoneyEvent(event);
            case WithdrewMoneyEvent event -> applyWithdrewMoneyEvent(event);
            default -> throw new NotYetImplementedException("Unknown DomainEvent type: "
                    + domainEvent.getClass().getSimpleName());
        }
    }

    private void applyCreatedAccountDomainEvent(CreatedAccountEvent event) {
        this.accountNumber = event.getAccountNumber();
        this.accountName = event.getAccountName();
        this.balance = event.getStartBalance();
        this.createdAt = event.getDateTime();
        this.version = 1;
        domainEvents.add(event);
    }

    private void applyDepositMoneyEvent(DepositedMoneyEvent depositedMoneyEvent) {
        this.version += 1;
        depositedMoneyEvent.setVersion(version);
        this.balance += depositedMoneyEvent.getAmount();
        domainEvents.add(depositedMoneyEvent);
    }

    private void applyWithdrewMoneyEvent(WithdrewMoneyEvent withdrewMoneyEvent) {
        this.version += 1;
        withdrewMoneyEvent.setVersion(version);
        if (withdrewMoneyEvent.getAmount() > balance) {
            log.error("Withdrawal amount exceeds current balance.");
            throw new NotEnoughFundsException("Withdrawal amount exceeds current balance.");
        }
        this.balance -= withdrewMoneyEvent.getAmount();
        domainEvents.add(withdrewMoneyEvent);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void rebuildState(List<DomainEvent> domainEvents) {
        for (DomainEvent event : domainEvents) {
            if (event instanceof CreatedAccountEvent createdAccountEvent) {
                this.accountNumber = createdAccountEvent.getAccountNumber();
                this.accountName = createdAccountEvent.getAccountName();
                this.balance = createdAccountEvent.getStartBalance();
                this.createdAt = createdAccountEvent.getDateTime();
                this.version = 1;
                continue;
            }

            if (event instanceof DepositedMoneyEvent depositedMoneyEvent) {
                this.balance += depositedMoneyEvent.getAmount();
                this.version += 1;
                continue;
            }

            if (event instanceof WithdrewMoneyEvent withdrewMoneyEvent) {
                this.balance -= withdrewMoneyEvent.getAmount();
                this.version += 1;
            }
        }
    }

    public void setStateFromSnapshot(SnapshotDataModel snapshot) {
        this.accountNumber = snapshot.getAccountNumber();
        this.accountName = snapshot.getAccountName();
        this.balance = snapshot.getBalance();
        this.createdAt = snapshot.getCreatedAt();
        this.version = snapshot.getVersion();
    }
}

