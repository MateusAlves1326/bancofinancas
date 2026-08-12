package acc.br.bancofinancas.dto;

import java.math.BigDecimal;

public class ContaCorrenteResponse {
    private Integer idContaCorrente;
    private Integer idAgencia;
    private Integer numero;
    private BigDecimal saldo;
    private Integer idCliente;

//getters
    public Integer getIdContaCorrente() {
        return idContaCorrente;
    }

    public Integer getIdAgencia() {
        return idAgencia;
    }

    public Integer getNumero() {
        return numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Integer getIdCliente() {
        return idCliente;
    }
//setters
    public void setIdContaCorrente(Integer idContaCorrente) {
        this.idContaCorrente = idContaCorrente;
    }

    public void setIdAgencia(Integer idAgencia) {
        this.idAgencia = idAgencia;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
}
