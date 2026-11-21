package br.com.Babino0.controle_financeiro_api.controller;

import br.com.Babino0.controle_financeiro_api.dto.TransactionRequest;
import br.com.Babino0.controle_financeiro_api.dto.TransactionResponse;
import br.com.Babino0.controle_financeiro_api.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> list(@RequestParam Long accountId,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return transactionService.listByAccount(accountId, from, to)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping

    public ResponseEntity<TransactionResponse> register(@RequestBody @Valid TransactionRequest request) {
        TransactionResponse saved = TransactionResponse.from(transactionService.register(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
