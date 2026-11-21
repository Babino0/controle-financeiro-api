package br.com.Babino0.controle_financeiro_api.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(@NotNull(message = "fromAccountId is required") Long fromAccountId, @NotNull(message = "toAccountId is required") Long toAccountId,  @NotNull(message = "amount is required")@Positive BigDecimal amount) {
}