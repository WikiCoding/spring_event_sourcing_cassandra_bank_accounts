package com.wikicoding.bank_account_lifecycle_engine.controllers;

import com.wikicoding.bank_account_lifecycle_engine.commands.CreateAccountCommand;
import com.wikicoding.bank_account_lifecycle_engine.commands.DepositMoneyCommand;
import com.wikicoding.bank_account_lifecycle_engine.commands.WithdrawMoneyCommand;
import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.dtos.AccountResponse;
import com.wikicoding.bank_account_lifecycle_engine.dtos.CreateAccountRequest;
import com.wikicoding.bank_account_lifecycle_engine.dtos.DepositMoneyRequest;
import com.wikicoding.bank_account_lifecycle_engine.dtos.WithdrawMoneyRequest;
import com.wikicoding.bank_account_lifecycle_engine.commandhandlers.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountsController {
    private final CommandHandler commandHandler;

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody CreateAccountRequest request) {
        if (request.getAccountName() == null || request.getAccountName().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (request.getStartBalance() <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        log.info("Executing create account request");
        Account account = commandHandler.executeCommand(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.getAccountName(),
                request.getStartBalance()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getVersion()
        ));
    }

    @PostMapping("{accountNumber}/deposit")
    public ResponseEntity<Object> depositMoney(
            @PathVariable String accountNumber,
            @RequestBody DepositMoneyRequest request) {
        if (request.getAmount() <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Account account = commandHandler.executeCommand(new DepositMoneyCommand(accountNumber, request.getAmount()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getVersion()
        ));
    }

    @PostMapping("{accountNumber}/withdraw")
    public ResponseEntity<Object> withdrawMoney(
            @PathVariable String accountNumber,
            @RequestBody WithdrawMoneyRequest request) {
        if (request.getAmount() <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Account account = commandHandler.executeCommand(new WithdrawMoneyCommand(accountNumber, request.getAmount()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getVersion()
        ));
    }

    @GetMapping("{accountNumber}")
    public ResponseEntity<Object> getAccounts(@PathVariable String accountNumber) {
        Account account = commandHandler.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }
}
