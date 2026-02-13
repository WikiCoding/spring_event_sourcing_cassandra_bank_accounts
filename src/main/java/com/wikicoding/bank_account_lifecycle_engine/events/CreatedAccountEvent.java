package com.wikicoding.bank_account_lifecycle_engine.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedAccountEvent implements DomainEvent {
    private String accountNumber;
    private String accountName;
    private double startBalance;
    private long dateTime;
    private int version;

    public CreatedAccountEvent(String accountNumber, String accountName, double startBalance, long dateTime) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.startBalance = startBalance;
        this.dateTime = dateTime;
    }

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
