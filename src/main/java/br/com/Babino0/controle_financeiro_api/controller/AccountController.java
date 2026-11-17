package br.com.Babino0.controle_financeiro_api.controller;

import br.com.Babino0.controle_financeiro_api.model.Account;
import br.com.Babino0.controle_financeiro_api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public List<Account> list() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> find(@PathVariable Long id) {
        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Account account) {
        Account saved = accountRepository.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
