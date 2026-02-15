package com.wikicoding.bank_account_projections_and_recommendations.controllers;

import com.wikicoding.bank_account_projections_and_recommendations.dto.AccountResponse;
import com.wikicoding.bank_account_projections_and_recommendations.model.Account;
import com.wikicoding.bank_account_projections_and_recommendations.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountsController {
    private final AccountsRepository accountsRepository;

    @GetMapping("{accountNumber}")
    public ResponseEntity<Object> getAccountByAccountNumber(@PathVariable String accountNumber) {
        log.info("Getting account by account number: {}", accountNumber);
        Optional<Account> account = accountsRepository.findById(accountNumber);
        return account.<ResponseEntity<Object>>map(value ->
                ResponseEntity.status(HttpStatus.OK).body(new AccountResponse(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found"));
    }
}
