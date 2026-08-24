package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotBlank;

public class PagarPedidoCodigoRequest {

    @NotBlank
    private String codigoPagamento;

    public String getCodigoPagamento() {
        return codigoPagamento;
    }

    public void setCodigoPagamento(String codigoPagamento) {
        this.codigoPagamento = codigoPagamento;
    }
}