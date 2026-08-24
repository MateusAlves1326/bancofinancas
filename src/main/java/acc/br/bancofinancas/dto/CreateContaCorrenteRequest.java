package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class CreateContaCorrenteRequest {
    @NotNull
    private Long clienteId;

    @NotNull
    private Long agenciaId;

    @NotNull
    private Integer numero;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal saldo;

    @Pattern(regexp = "\\d{4}", message = "A senha deve ter exatamente 4 dígitos")
    private String senha;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getAgenciaId() {
        return agenciaId;
    }

    public void setAgenciaId(Long agenciaId) {
        this.agenciaId = agenciaId;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
