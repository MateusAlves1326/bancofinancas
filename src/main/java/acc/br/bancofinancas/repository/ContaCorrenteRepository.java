package acc.br.bancofinancas.repository;

import acc.br.bancofinancas.model.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {
}
