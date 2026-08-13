package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import acc.br.bancofinancas.model.Operacao;
import java.math.BigDecimal;


public class CreateOperacaoRequest {
    @NotNull
    private Long contaCorrenteId;

    @NotNull
    private Operacao operacao;
    
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal valorOperacao;


    public Long getContaCorrenteId() {
        return contaCorrenteId;
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

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public void setValorOperacao(BigDecimal valorOperacao) {
        this.valorOperacao = valorOperacao;
    }


}
