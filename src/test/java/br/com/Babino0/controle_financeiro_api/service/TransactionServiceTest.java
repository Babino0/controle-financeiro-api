package br.com.Babino0.controle_financeiro_api.service;

import br.com.Babino0.controle_financeiro_api.dto.TransactionRequest;
import br.com.Babino0.controle_financeiro_api.exception.InsufficientBalanceException;
import br.com.Babino0.controle_financeiro_api.exception.ResourceNotFoundException;
import br.com.Babino0.controle_financeiro_api.model.Account;
import br.com.Babino0.controle_financeiro_api.model.Category;
import br.com.Babino0.controle_financeiro_api.model.Transaction;
import br.com.Babino0.controle_financeiro_api.model.TransactionType;
import br.com.Babino0.controle_financeiro_api.repository.AccountRepository;
import br.com.Babino0.controle_financeiro_api.repository.CategoryRepository;
import br.com.Babino0.controle_financeiro_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(accountRepository, transactionRepository, categoryRepository);
    }

    private void assertBalance(String expected, Account account) {
        assertEquals(0, new BigDecimal(expected).compareTo(account.getBalance()),
                "expected balance " + expected + " but was " + account.getBalance());
    }

    @Test
    void validExpenseUpdatesBalanceAndSavesTransaction() {
        Account account = new Account("Wallet", new BigDecimal("100"));
        Category category = new Category("Food");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        TransactionRequest request = new TransactionRequest(
                1L, 2L, TransactionType.EXPENSE, new BigDecimal("30"), "lunch");

        Transaction result = service.register(request);

        assertBalance("70", account);
        assertEquals(TransactionType.EXPENSE, result.getType());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void unknownAccountIsRejectedAndNothingIsSaved() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());
        TransactionRequest request = new TransactionRequest(
                99L, 2L, TransactionType.EXPENSE, new BigDecimal("30"), "lunch");

        assertThrows(ResourceNotFoundException.class, () -> service.register(request));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void unknownCategoryIsRejectedAndAccountIsUntouched() {
        Account account = new Account("Wallet", new BigDecimal("100"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        TransactionRequest request = new TransactionRequest(
                1L, 99L, TransactionType.EXPENSE, new BigDecimal("30"), "lunch");

        assertThrows(ResourceNotFoundException.class, () -> service.register(request));

        assertBalance("100", account);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void insufficientBalanceIsRejectedAndNothingIsSaved() {
        Account account = new Account("Wallet", new BigDecimal("100"));
        Category category = new Category("Food");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        TransactionRequest request = new TransactionRequest(
                1L, 2L, TransactionType.EXPENSE, new BigDecimal("101"), "lunch");

        assertThrows(InsufficientBalanceException.class, () -> service.register(request));

        assertBalance("100", account);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}