package com.ronit.banking.controller;

import com.ronit.banking.dto.AccountResponse;
import com.ronit.banking.dto.CreateAccountRequest;
import com.ronit.banking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
        summary = "Create a new bank account",
        description = "Creates a new account for an existing customer"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Account created successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Customer not found"
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Customer already has an account"
        )
})
@PostMapping
public ResponseEntity<AccountResponse> createAccount(
        @Valid @RequestBody CreateAccountRequest request) {

    AccountResponse response = accountService.createAccount(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}
}