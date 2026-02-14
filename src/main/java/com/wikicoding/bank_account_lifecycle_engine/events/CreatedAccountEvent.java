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

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
