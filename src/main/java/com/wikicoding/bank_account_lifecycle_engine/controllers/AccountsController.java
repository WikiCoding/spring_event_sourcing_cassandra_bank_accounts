package com.wikicoding.bank_account_lifecycle_engine.controllers;

import com.wikicoding.bank_account_lifecycle_engine.commands.CreateAccountCommand;
import com.wikicoding.bank_account_lifecycle_engine.commands.DepositMoneyCommand;
import com.wikicoding.bank_account_lifecycle_engine.commands.WithdrawMoneyCommand;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.services.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final CommandHandler commandHandler;

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody CreateAccountCommand command) {
        commandHandler.executeCommand(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("deposit")
    public ResponseEntity<Object> depositMoney(@RequestBody DepositMoneyCommand command) {
        commandHandler.executeCommand(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("withdraw")
    public ResponseEntity<Object> withdrawMoney(@RequestBody WithdrawMoneyCommand command) {
        commandHandler.executeCommand(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{accountNumber}")
    public ResponseEntity<Object> getAccounts(@PathVariable String accountNumber) {
        Account account = commandHandler.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }
}
