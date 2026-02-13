package com.wikicoding.bank_account_lifecycle_engine.domain;

import com.wikicoding.bank_account_lifecycle_engine.commands.CreateAccountCommand;
import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    public Account(CreateAccountCommand command) {
        applyCreatedAccountDomainEvent(
                new CreatedAccountEvent(
                        UUID.randomUUID().toString(),
                        command.getAccountName(),
                        command.getStartBalance(),
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        1
                )
        );
    }

    private void applyCreatedAccountDomainEvent(CreatedAccountEvent event) {
        this.accountNumber = event.getAccountNumber();
        this.accountName = event.getAccountName();
        this.balance = event.getStartBalance();
        this.createdAt = event.getDateTime();
        this.version = 1;
        domainEvents.add(event);
    }

    public void applyDepositMoneyEvent(DepositedMoneyEvent depositedMoneyEvent) {
        this.version += 1;
        depositedMoneyEvent.setVersion(version);
        this.balance += depositedMoneyEvent.getAmount();
        domainEvents.add(depositedMoneyEvent);
    }

    public void applyWithdrewMoneyEvent(WithdrewMoneyEvent withdrewMoneyEvent) {
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
