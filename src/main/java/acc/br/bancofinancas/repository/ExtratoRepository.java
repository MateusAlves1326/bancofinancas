package acc.br.bancofinancas.repository;

import acc.br.bancofinancas.model.Extrato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtratoRepository extends JpaRepository<Extrato, Integer> {
    List<Extrato> findByContaCorrente_IdContaCorrenteOrderByDataHoraMovimentoDesc(int contaCorrenteId);
}
