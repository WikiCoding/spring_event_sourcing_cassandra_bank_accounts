package com.wikicoding.bank_account_lifecycle_engine.domain;

import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class Account {
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
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public void apply(DomainEvent domainEvent) {
        if (domainEvent instanceof CreatedAccountEvent event) {
            applyCreatedAccountDomainEvent(event);
        } else if (domainEvent instanceof DepositedMoneyEvent event) {
            applyDepositMoneyEvent(event);
        } else if (domainEvent instanceof WithdrewMoneyEvent event) {
            applyWithdrewMoneyEvent(event);
        } else {
            throw new IllegalArgumentException("Unknown DomainEvent type: " + domainEvent.getClass().getSimpleName());
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
}
