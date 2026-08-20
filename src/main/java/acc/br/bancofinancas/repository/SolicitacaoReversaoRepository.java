package acc.br.bancofinancas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import acc.br.bancofinancas.model.SolicitacaoReversao;
import acc.br.bancofinancas.model.StatusSolicitacaoReversao;

public interface SolicitacaoReversaoRepository extends JpaRepository<SolicitacaoReversao, Integer> {
    List<SolicitacaoReversao> findByStatusOrderByDataSolicitacaoAsc(StatusSolicitacaoReversao status);
    List<SolicitacaoReversao> findByContaCorrente_Agencia_IdAgencyAndStatusOrderByDataSolicitacaoAsc(
            int agenciaId, StatusSolicitacaoReversao status);
}
