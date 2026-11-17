package br.com.Babino0.controle_financeiro_api.dto;

import br.com.Babino0.controle_financeiro_api.model.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(Long accountId, Long categoryId, TransactionType type,
                                 BigDecimal amount, String description) {
}
