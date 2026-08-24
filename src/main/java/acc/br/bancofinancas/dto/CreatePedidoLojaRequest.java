package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotNull;

public class CreatePedidoLojaRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private Long contaCorrenteId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getContaCorrenteId() {
        return contaCorrenteId;
    }

    public void setContaCorrenteId(Long contaCorrenteId) {
        this.contaCorrenteId = contaCorrenteId;
    }
}