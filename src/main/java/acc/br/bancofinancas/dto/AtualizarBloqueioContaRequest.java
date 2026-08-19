package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotNull;

public class AtualizarBloqueioContaRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private Boolean bloqueada;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Boolean getBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(Boolean bloqueada) {
        this.bloqueada = bloqueada;
    }
}