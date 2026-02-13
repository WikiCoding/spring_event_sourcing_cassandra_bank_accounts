package com.wikicoding.bank_account_lifecycle_engine.events;

public interface DomainEvent {
    String getAccountNumber();
    String getEventType();
    int getVersion();
}
