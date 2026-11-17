package br.com.Babino0.controle_financeiro_api.repository;

import br.com.Babino0.controle_financeiro_api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
