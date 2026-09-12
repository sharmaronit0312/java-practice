package com.ronit.banking.service;

import com.ronit.banking.dto.CreateDepositRequest;
import com.ronit.banking.dto.CreateTransferRequest;
import com.ronit.banking.dto.TransactionResponse;
import com.ronit.banking.entity.Account;
import com.ronit.banking.entity.Transaction;
import com.ronit.banking.enums.AccountStatus;
import com.ronit.banking.enums.TransactionStatus;
import com.ronit.banking.enums.TransactionType;
import com.ronit.banking.exception.InsufficientBalanceException;
import com.ronit.banking.exception.InvalidTransactionException;
import com.ronit.banking.repository.AccountRepository;
import com.ronit.banking.repository.TransactionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse transfer(CreateTransferRequest request) {

        // 1. Validate source and destination accounts are different
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new InvalidTransactionException(
                    "Source and destination accounts must be different");
        }

        // 2. Find source account
        Account fromAccount = accountRepository.findById(request.fromAccountId())
                .orElseThrow(() -> new InvalidTransactionException(
                        "Source account not found with id: "
                                + request.fromAccountId()));

        // 3. Find destination account
        Account toAccount = accountRepository.findById(request.toAccountId())
                .orElseThrow(() -> new InvalidTransactionException(
                        "Destination account not found with id: "
                                + request.toAccountId()));

        // 4. Check accounts are active
        if (fromAccount.getStatus() != AccountStatus.ACTIVE ||
                toAccount.getStatus() != AccountStatus.ACTIVE) {

            throw new InvalidTransactionException(
                    "Both accounts must be active");
        }

        // 5. Check sufficient balance
        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in source account");
        }

        // 6. Debit source account
        fromAccount.setBalance(
                fromAccount.getBalance().subtract(request.amount()));

        // 7. Credit destination account
        toAccount.setBalance(
                toAccount.getBalance().add(request.amount()));

        // 8. Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 9. Get next transaction reference number
        Long sequence = transactionRepository.getNextTransactionReference();

        // 10. Create transaction record
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                String.format("TXN%06d", sequence));

        transaction.setFromAccountId(fromAccount.getId());
        transaction.setToAccountId(toAccount.getId());
        transaction.setAmount(request.amount());
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setStatus(TransactionStatus.SUCCESS);

        // 11. Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 12. Return response
        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getTransactionReference(),
                savedTransaction.getFromAccountId(),
                savedTransaction.getToAccountId(),
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType(),
                savedTransaction.getStatus(),
                savedTransaction.getCreatedAt());
    }

    @Transactional
    public TransactionResponse deposit(CreateDepositRequest request) {

        // 1. Find account
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new InvalidTransactionException(
                        "Account not found with id: "
                                + request.accountId()));

        // 2. Check account is active
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidTransactionException(
                    "Account must be active");
        }

        // 3. Credit amount
        account.setBalance(
                account.getBalance().add(request.amount()));

        // 4. Save updated account
        accountRepository.save(account);

        // 5. Get next transaction reference number
        Long sequence = transactionRepository.getNextTransactionReference();

        // 6. Create transaction record
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                String.format("TXN%06d", sequence));

        transaction.setFromAccountId(account.getId());
        transaction.setToAccountId(account.getId());
        transaction.setAmount(request.amount());
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        
        // 7. Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 8. Return response
        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getTransactionReference(),
                savedTransaction.getFromAccountId(),
                savedTransaction.getToAccountId(),
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType(),
                savedTransaction.getStatus(),
                savedTransaction.getCreatedAt());
    }

    public Page<TransactionResponse> getTransactionsByAccount(
        Long accountId,
        Pageable pageable) {

    Page<Transaction> transactions =
            transactionRepository
                    .findByFromAccountIdOrToAccountId(
                            accountId,
                            accountId,
                            pageable
                    );

    return transactions.map(this::mapToResponse);
}

private TransactionResponse mapToResponse(Transaction transaction) {

    return new TransactionResponse(
            transaction.getId(),
            transaction.getTransactionReference(),
            transaction.getFromAccountId(),
            transaction.getToAccountId(),
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getStatus(),
            transaction.getCreatedAt()
    );
}

}