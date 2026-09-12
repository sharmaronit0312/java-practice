package com.ronit.banking.dto;

import com.ronit.banking.enums.AccountType;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Account type is required")
        AccountType accountType
) {
}