package com.wikicoding.bank_account_lifecycle_engine.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrewMoneyEvent implements DomainEvent {
    private String accountNumber;
    private double amount;
    @Setter
    private int version;

    public WithdrewMoneyEvent(String accountNumber, double amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
