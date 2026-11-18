package br.com.Babino0.controle_financeiro_api.model;

import br.com.Babino0.controle_financeiro_api.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    private Account accountWith(String balance) {
        return new Account("Wallet", new BigDecimal(balance));
    }

    private void assertBalance(String expected, Account account) {
        assertEquals(0, new BigDecimal(expected).compareTo(account.getBalance()),
                "expected balance " + expected + " but was " + account.getBalance());
    }

    @Test
    void incomeIncreasesBalance() {
        Account account = accountWith("100");

        account.apply(TransactionType.INCOME, new BigDecimal("50"));

        assertBalance("150", account);
    }

    @Test
    void expenseDecreasesBalance() {
        Account account = accountWith("100");

        account.apply(TransactionType.EXPENSE, new BigDecimal("30"));

        assertBalance("70", account);
    }

    @Test
    void expenseEqualToBalanceZeroesIt() {
        Account account = accountWith("100");

        account.apply(TransactionType.EXPENSE, new BigDecimal("100"));

        assertBalance("0", account);
    }

    @Test
    void expenseGreaterThanBalanceIsRejectedAndKeepsBalance() {
        Account account = accountWith("100");

        assertThrows(InsufficientBalanceException.class,
                () -> account.apply(TransactionType.EXPENSE, new BigDecimal("101")));

        assertBalance("100", account);
    }

    @Test
    void zeroAmountIsRejected() {
        Account account = accountWith("100");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> account.apply(TransactionType.INCOME, BigDecimal.ZERO));

        assertEquals("Amount must be positive", e.getMessage());
    }

    @Test
    void negativeAmountIsRejectedAndKeepsBalance() {
        Account account = accountWith("100");

        assertThrows(IllegalArgumentException.class,
                () -> account.apply(TransactionType.EXPENSE, new BigDecimal("-5")));

        assertBalance("100", account);
    }
}