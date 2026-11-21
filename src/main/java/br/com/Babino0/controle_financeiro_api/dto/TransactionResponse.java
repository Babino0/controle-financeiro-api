package br.com.Babino0.controle_financeiro_api.dto;

import br.com.Babino0.controle_financeiro_api.model.Transaction;
import br.com.Babino0.controle_financeiro_api.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(Long id,
                                  TransactionType type,
                                  BigDecimal amount,
                                  String description,
                                  LocalDateTime dateTime,
                                  Long accountId,
                                  Long categoryId,
                                  String categoryName) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDateTime(),
                transaction.getAccount().getId(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName());
    }
}
