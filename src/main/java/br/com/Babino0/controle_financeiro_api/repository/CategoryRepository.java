package br.com.Babino0.controle_financeiro_api.repository;

import br.com.Babino0.controle_financeiro_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
