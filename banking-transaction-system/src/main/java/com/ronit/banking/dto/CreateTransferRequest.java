package com.ronit.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransferRequest(

        @NotNull(message = "From account ID is required")
        Long fromAccountId,

        @NotNull(message = "To account ID is required")
        Long toAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
) {
}