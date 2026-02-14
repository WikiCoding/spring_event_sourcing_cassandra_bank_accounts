package com.wikicoding.bank_account_lifecycle_engine.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorInfo {
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
