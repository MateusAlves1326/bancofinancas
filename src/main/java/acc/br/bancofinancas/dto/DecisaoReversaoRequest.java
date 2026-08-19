package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotNull;

public class DecisaoReversaoRequest {

    @NotNull
    private Long solicitacaoId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Boolean aprovar;

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Boolean getAprovar() {
        return aprovar;
    }

    public void setAprovar(Boolean aprovar) {
        this.aprovar = aprovar;
    }
}
