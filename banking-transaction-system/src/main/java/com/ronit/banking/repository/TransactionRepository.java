package com.ronit.banking.repository;

import com.ronit.banking.entity.Account;
import com.ronit.banking.entity.Transaction;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(
            String transactionReference);

    boolean existsByTransactionReference(
            String transactionReference);

            @Query(value = "SELECT nextval('banking.transaction_reference_seq')", nativeQuery = true)
Long getNextTransactionReference();

Page<Transaction> findByFromAccountIdOrToAccountId(
        Long fromAccountId,
        Long toAccountId,
        Pageable pageable
);

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Account a WHERE a.id = :id")
Optional<Account> findByIdForUpdate(@Param("id") Long id);

}