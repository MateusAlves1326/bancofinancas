package acc.br.bancofinancas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import acc.br.bancofinancas.model.Operacao;
import acc.br.bancofinancas.model.StatusSolicitacaoReversao;

public class SolicitacaoReversaoResponse {

    private Long solicitacaoId;
    private Long contaCorrenteId;
    private Long clienteId;
    private BigDecimal valor;
    private Operacao operacaoReversa;
    private StatusSolicitacaoReversao status;
    private String motivo;
    private LocalDateTime dataSolicitacao;

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public Long getContaCorrenteId() {
        return contaCorrenteId;
    }

    public void setContaCorrenteId(Long contaCorrenteId) {
        this.contaCorrenteId = contaCorrenteId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Operacao getOperacaoReversa() {
        return operacaoReversa;
    }

    public void setOperacaoReversa(Operacao operacaoReversa) {
        this.operacaoReversa = operacaoReversa;
    }

    public StatusSolicitacaoReversao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoReversao status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
}
