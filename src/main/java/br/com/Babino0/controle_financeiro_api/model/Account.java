package br.com.Babino0.controle_financeiro_api.model;

import br.com.Babino0.controle_financeiro_api.exception.InsufficientBalanceException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private BigDecimal balance;

    public Account() {
    }

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    private void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    private void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        balance = balance.subtract(amount);
    }

    public void apply(TransactionType type, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        switch (type) {
            case INCOME -> deposit(amount);
            case EXPENSE -> withdraw(amount);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
