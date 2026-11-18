package br.com.Babino0.controle_financeiro_api.repository;

import br.com.Babino0.controle_financeiro_api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByDateTimeDesc( Long accountId );
}
