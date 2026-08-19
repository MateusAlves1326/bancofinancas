package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import acc.br.bancofinancas.model.Operacao;
import java.math.BigDecimal;


public class CreateOperacaoRequest {
    @NotNull
    private Long contaCorrenteId;

    private Long clienteId;

    private Long contaDestinoId;

    private Long extratoOrigemId;

    private String motivo;

    @NotNull
    private Operacao operacao;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal valorOperacao;

    public Long getContaCorrenteId() {
        return contaCorrenteId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getContaDestinoId() {
        return contaDestinoId;
    }

    public Long getExtratoOrigemId() {
        return extratoOrigemId;
    }

    public String getMotivo() {
        return motivo;
    }

    public Operacao getOperacao() {
        return operacao;
    }

    public BigDecimal getValorOperacao() {
        return valorOperacao;
    }

    public void setContaCorrenteId(Long contaCorrenteId) {
        this.contaCorrenteId = contaCorrenteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setContaDestinoId(Long contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }

    public void setExtratoOrigemId(Long extratoOrigemId) {
        this.extratoOrigemId = extratoOrigemId;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public void setValorOperacao(BigDecimal valorOperacao) {
        this.valorOperacao = valorOperacao;
    }
}
