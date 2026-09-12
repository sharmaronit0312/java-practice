package com.ronit.banking.dto;

import com.ronit.banking.enums.TransactionStatus;
import com.ronit.banking.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(

        Long id,

        String transactionReference,

        Long fromAccountId,

        Long toAccountId,

        BigDecimal amount,

        TransactionType transactionType,

        TransactionStatus status,

        LocalDateTime createdAt
) {
}