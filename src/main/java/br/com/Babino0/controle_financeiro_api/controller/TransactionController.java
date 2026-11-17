package br.com.Babino0.controle_financeiro_api.controller;

import br.com.Babino0.controle_financeiro_api.dto.TransactionRequest;
import br.com.Babino0.controle_financeiro_api.model.Transaction;
import br.com.Babino0.controle_financeiro_api.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> register(@RequestBody TransactionRequest request) {
        Transaction saved = transactionService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
