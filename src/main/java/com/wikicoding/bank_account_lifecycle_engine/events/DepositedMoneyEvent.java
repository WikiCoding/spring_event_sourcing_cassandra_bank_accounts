package com.wikicoding.bank_account_lifecycle_engine.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepositedMoneyEvent implements DomainEvent {
    private String accountNumber;
    private double amount;
    @Setter
    private int version;

    public DepositedMoneyEvent(String accountNumber, double amount) {
        this.amount = amount;
        this.accountNumber = accountNumber;
    }

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
