package br.com.Babino0.controle_financeiro_api.dto;

import br.com.Babino0.controle_financeiro_api.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransactionRequest(@NotNull(message = "accountId is required") Long accountId,
                                 @NotNull(message = "categoryId is required") Long categoryId,
                                 @NotNull(message = "type is required") TransactionType type,
                                 @NotNull(message = "amount is required") @Positive BigDecimal amount,
                                 @Size(max = 255, message = "description must have at most {max} characters") String description) {
}
