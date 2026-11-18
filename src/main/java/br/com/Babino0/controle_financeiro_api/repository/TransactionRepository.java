package br.com.Babino0.controle_financeiro_api.repository;

import br.com.Babino0.controle_financeiro_api.dto.CategoryTotal;
import br.com.Babino0.controle_financeiro_api.model.Transaction;
import br.com.Babino0.controle_financeiro_api.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    List<Transaction> findByAccountIdOrderByDateTimeDesc( Long accountId );
    List<Transaction> findByAccountIdAndDateTimeGreaterThanEqualAndDateTimeLessThanOrderByDateTimeDesc(
            Long accountId, LocalDateTime start, LocalDateTime end);
    @Query("""
    SELECT new br.com.Babino0.controle_financeiro_api.dto.CategoryTotal(c.name, SUM(t.amount))
    FROM Transaction t JOIN t.category c
    WHERE t.account.id = :accountId AND t.type = :type
    GROUP BY c.id, c.name
    ORDER BY SUM(t.amount) DESC
    """)
    List<CategoryTotal> totalByCategory(@Param("accountId") Long accountId,
                                        @Param("type") TransactionType type);
}
