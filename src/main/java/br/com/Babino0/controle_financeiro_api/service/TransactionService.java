package br.com.Babino0.controle_financeiro_api.service;

import br.com.Babino0.controle_financeiro_api.dto.TransactionRequest;
import br.com.Babino0.controle_financeiro_api.exception.ResourceNotFoundException;
import br.com.Babino0.controle_financeiro_api.model.Account;
import br.com.Babino0.controle_financeiro_api.model.Category;
import br.com.Babino0.controle_financeiro_api.model.Transaction;
import br.com.Babino0.controle_financeiro_api.repository.AccountRepository;
import br.com.Babino0.controle_financeiro_api.repository.CategoryRepository;
import br.com.Babino0.controle_financeiro_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Transaction register(TransactionRequest r) {
        Account account = accountRepository.findById(r.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Category category = categoryRepository.findById(r.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        account.apply(r.type(), r.amount());

        accountRepository.save(account);

        Transaction transaction = new Transaction(r.type(), r.amount(), r.description(),
                LocalDateTime.now(), account, category);
        return transactionRepository.save(transaction);
    }
}
