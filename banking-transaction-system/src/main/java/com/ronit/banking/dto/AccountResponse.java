package com.ronit.banking.dto;

import com.ronit.banking.enums.AccountStatus;
import com.ronit.banking.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        Long customerId,
        AccountType accountType,
        BigDecimal balance,
        AccountStatus status,
        LocalDateTime createdAt
) {
}