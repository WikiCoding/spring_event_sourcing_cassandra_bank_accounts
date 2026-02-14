package com.wikicoding.bank_account_lifecycle_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<ErrorInfo> handleNotEnoughFundsException(NotEnoughFundsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorInfo(ex.getMessage()));
    }

    @ExceptionHandler(NotYetImplementedException.class)
    public ResponseEntity<ErrorInfo> handleNotYetImplementedException(NotYetImplementedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ErrorInfo(ex.getMessage()));
    }
}
