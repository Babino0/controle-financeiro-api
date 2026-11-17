package br.com.Babino0.controle_financeiro_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;

    private String description;

    private LocalDateTime dateTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Transaction() {
    }

    public Transaction(TransactionType type, BigDecimal amount,
                       String description, LocalDateTime dateTime,
                       Account account, Category category) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
        this.account = account;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Account getAccount() {
        return account;
    }

    public Category getCategory() {
        return category;
    }
}
