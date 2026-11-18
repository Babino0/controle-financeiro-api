package br.com.Babino0.controle_financeiro_api.controller;

import br.com.Babino0.controle_financeiro_api.dto.CategoryTotal;
import br.com.Babino0.controle_financeiro_api.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final TransactionService transactionService;

    public ReportController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/by-category")
    public List<CategoryTotal> expensesByCategory(@RequestParam Long accountId) {
        return transactionService.expensesByCategory(accountId);
    }
}