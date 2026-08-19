package acc.br.bancofinancas.dto;

import java.math.BigDecimal;

public class ContaCorrenteResponse {
   private Long id;
    private Integer numero;
    private BigDecimal saldo;
    private Long clienteId;
    private Long agenciaId;
    private boolean bloqueada;
    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getAgenciaId() {
        return agenciaId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setAgenciaId(Long agenciaId) {
        this.agenciaId = agenciaId;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
    }
}
