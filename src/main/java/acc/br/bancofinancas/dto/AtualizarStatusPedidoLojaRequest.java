package acc.br.bancofinancas.dto;

import acc.br.bancofinancas.model.StatusPedidoLoja;
import jakarta.validation.constraints.NotNull;

public class AtualizarStatusPedidoLojaRequest {

    @NotNull
    private StatusPedidoLoja status;

    public StatusPedidoLoja getStatus() {
        return status;
    }

    public void setStatus(StatusPedidoLoja status) {
        this.status = status;
    }
}