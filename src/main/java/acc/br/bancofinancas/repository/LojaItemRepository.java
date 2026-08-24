package acc.br.bancofinancas.repository;

import acc.br.bancofinancas.model.LojaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LojaItemRepository extends JpaRepository<LojaItem, Integer> {
    List<LojaItem> findByAtivoTrueOrderByNomeAsc();
}