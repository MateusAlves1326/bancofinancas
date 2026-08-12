package acc.br.bancofinancas.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class CreateContaCorrenteRequest {
    @NotNull(message = "idAgencia e obrigatorio")
    private Integer idAgencia;

    @NotNull(message = "Numero e obrigatorio")
    private Integer numero;

    @NotNull(message = "Saldo e obrigatorio")
    private BigDecimal saldo;

    @NotNull(message = "idCliente e obrigatorio")
    private Integer idCliente;

    public Integer getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(Integer idAgencia) {
        this.idAgencia = idAgencia;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
}
