package com.wikicoding.bank_account_lifecycle_engine.exceptions;

public class NotEnoughFundsException extends RuntimeException {
    public NotEnoughFundsException(String message) {
        super(message);
    }
}
