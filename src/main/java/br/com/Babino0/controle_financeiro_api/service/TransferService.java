package br.com.Babino0.controle_financeiro_api.service;

import br.com.Babino0.controle_financeiro_api.dto.TransferRequest;
import br.com.Babino0.controle_financeiro_api.exception.ResourceNotFoundException;
import br.com.Babino0.controle_financeiro_api.model.Account;
import br.com.Babino0.controle_financeiro_api.model.TransactionType;
import br.com.Babino0.controle_financeiro_api.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(TransferRequest r) {
        if (Objects.equals(r.fromAccountId(), r.toAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        Account from = accountRepository.findById(r.fromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        Account to = accountRepository.findById(r.toAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        from.apply(TransactionType.EXPENSE, r.amount());
        to.apply(TransactionType.INCOME, r.amount());

        accountRepository.save(from);
        accountRepository.save(to);
    }
}