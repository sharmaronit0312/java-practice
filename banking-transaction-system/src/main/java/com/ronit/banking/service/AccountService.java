package com.ronit.banking.service;

import com.ronit.banking.dto.AccountResponse;
import com.ronit.banking.dto.CreateAccountRequest;
import com.ronit.banking.entity.Account;
import com.ronit.banking.entity.Customer;
import com.ronit.banking.enums.AccountStatus;
import com.ronit.banking.exception.CustomerNotFoundException;
import com.ronit.banking.exception.DuplicateResourceException;
import com.ronit.banking.repository.AccountRepository;
import com.ronit.banking.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + request.customerId()
                        ));

        if (accountRepository.existsByCustomerId(request.customerId())) {
            throw new DuplicateResourceException(
                    "Customer already has an account"
            );
        }

        String accountNumber = generateAccountNumber();

        Account account = new Account();

        account.setAccountNumber(accountNumber);
        account.setCustomerId(customer.getId());
        account.setAccountType(request.accountType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(
                savedAccount.getId(),
                savedAccount.getAccountNumber(),
                savedAccount.getCustomerId(),
                savedAccount.getAccountType(),
                savedAccount.getBalance(),
                savedAccount.getStatus(),
                savedAccount.getCreatedAt()
        );
    }

    private String generateAccountNumber() {

        long number = ThreadLocalRandom.current()
                .nextLong(100000000000L, 999999999999L);

        return String.valueOf(number);
    }
}