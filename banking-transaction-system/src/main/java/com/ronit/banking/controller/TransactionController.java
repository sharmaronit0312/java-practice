package com.ronit.banking.controller;

import com.ronit.banking.dto.CreateDepositRequest;
import com.ronit.banking.dto.CreateTransferRequest;
import com.ronit.banking.dto.TransactionResponse;
import com.ronit.banking.service.TransactionService;
import jakarta.validation.Valid;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody CreateTransferRequest request) {

        TransactionResponse response =
                transactionService.transfer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/deposit")
public ResponseEntity<TransactionResponse> deposit(
        @Valid @RequestBody CreateDepositRequest request) {

    TransactionResponse response =
            transactionService.deposit(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}

@GetMapping("/account/{accountId}")
public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccount(
        @PathVariable Long accountId,
        Pageable pageable) {

    return ResponseEntity.ok(
            transactionService.getTransactionsByAccount(
                    accountId,
                    pageable
            )
    );
}

}