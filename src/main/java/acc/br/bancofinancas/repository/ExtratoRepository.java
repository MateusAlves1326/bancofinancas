package acc.br.bancofinancas.repository;

import acc.br.bancofinancas.model.Extrato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtratoRepository extends JpaRepository<Extrato, Integer> {
}
