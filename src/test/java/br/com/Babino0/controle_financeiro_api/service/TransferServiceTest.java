package br.com.Babino0.controle_financeiro_api.service;

import br.com.Babino0.controle_financeiro_api.dto.TransferRequest;
import br.com.Babino0.controle_financeiro_api.exception.InsufficientBalanceException;
import br.com.Babino0.controle_financeiro_api.exception.ResourceNotFoundException;
import br.com.Babino0.controle_financeiro_api.model.Account;
import br.com.Babino0.controle_financeiro_api.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferMovesMoneyBetweenAccounts() {
        Account from = new Account("A", new BigDecimal("100"));
        Account to = new Account("B", new BigDecimal("50"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("30")));

        assertEquals(0, new BigDecimal("70").compareTo(from.getBalance()));
        assertEquals(0, new BigDecimal("80").compareTo(to.getBalance()));
        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
    }

    @Test
    void transferWithInsufficientBalanceChangesNothing() {
        Account from = new Account("A", new BigDecimal("20"));
        Account to = new Account("B", new BigDecimal("50"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(InsufficientBalanceException.class,
                () -> transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("30"))));

        assertEquals(0, new BigDecimal("20").compareTo(from.getBalance()));
        assertEquals(0, new BigDecimal("50").compareTo(to.getBalance()));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transferToSameAccountIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(new TransferRequest(1L, 1L, new BigDecimal("10"))));

        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transferToMissingAccountChangesNothing() {
        Account from = new Account("A", new BigDecimal("100"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("30"))));

        assertEquals(0, new BigDecimal("100").compareTo(from.getBalance()));
        verify(accountRepository, never()).save(any());
    }
}