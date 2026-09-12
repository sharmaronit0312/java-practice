package banking_transaction_system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ronit.banking.dto.AccountResponse;
import com.ronit.banking.dto.CreateAccountRequest;
import com.ronit.banking.entity.Account;
import com.ronit.banking.entity.Customer;
import com.ronit.banking.enums.AccountStatus;
import com.ronit.banking.enums.AccountType;
import com.ronit.banking.repository.AccountRepository;
import com.ronit.banking.repository.CustomerRepository;
import com.ronit.banking.service.AccountService;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import com.ronit.banking.exception.CustomerNotFoundException;
import com.ronit.banking.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountSuccessfully() {

        // Given
        CreateAccountRequest request =
                new CreateAccountRequest(
                        1L,
                        AccountType.SAVINGS
                );

        Customer customer = new Customer();
        customer.setId(1L);

        Account savedAccount = new Account();
        savedAccount.setId(10L);
        savedAccount.setAccountNumber("123456789012");
        savedAccount.setCustomerId(1L);
        savedAccount.setAccountType(AccountType.SAVINGS);
        savedAccount.setBalance(BigDecimal.ZERO);
        savedAccount.setStatus(AccountStatus.ACTIVE);

        // Mock customer lookup
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        // Mock duplicate check
        when(accountRepository.existsByCustomerId(1L))
                .thenReturn(false);

        // Mock save
        when(accountRepository.save(org.mockito.ArgumentMatchers.any(Account.class)))
                .thenReturn(savedAccount);

        // When
        AccountResponse result =
                accountService.createAccount(request);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("123456789012", result.accountNumber());
        assertEquals(1L, result.customerId());
        assertEquals(AccountType.SAVINGS, result.accountType());
        assertEquals(BigDecimal.ZERO, result.balance());
        assertEquals(AccountStatus.ACTIVE, result.status());

        // Verify repository interactions
        verify(customerRepository).findById(1L);
        verify(accountRepository).existsByCustomerId(1L);
        verify(accountRepository).save(org.mockito.ArgumentMatchers.any(Account.class));
    }

    @Test
void shouldThrowExceptionWhenCustomerDoesNotExist() {

    // Given
    CreateAccountRequest request =
            new CreateAccountRequest(
                    999L,
                    AccountType.SAVINGS
            );

    when(customerRepository.findById(999L))
            .thenReturn(Optional.empty());

    // When + Then
    assertThrows(
            CustomerNotFoundException.class,
            () -> accountService.createAccount(request)
    );

    verify(customerRepository).findById(999L);

    verify(accountRepository, never())
            .existsByCustomerId(999L);

    verify(accountRepository, never())
            .save(any(Account.class));
}

@Test
void shouldThrowExceptionWhenCustomerAlreadyHasAccount() {

    // Given
    CreateAccountRequest request =
            new CreateAccountRequest(
                    1L,
                    AccountType.SAVINGS
            );

    Customer customer = new Customer();
    customer.setId(1L);

    when(customerRepository.findById(1L))
            .thenReturn(Optional.of(customer));

    when(accountRepository.existsByCustomerId(1L))
            .thenReturn(true);

    // When + Then
    assertThrows(
            DuplicateResourceException.class,
            () -> accountService.createAccount(request)
    );

    // Verify
    verify(customerRepository).findById(1L);

    verify(accountRepository)
            .existsByCustomerId(1L);

    verify(accountRepository, never())
            .save(any(Account.class));
}

}