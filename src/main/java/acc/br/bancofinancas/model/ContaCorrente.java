package acc.br.bancofinancas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

//mark class as an Entity 
@Entity
//defining class name as Table name
@Table(name = "contacorrente")
public class ContaCorrente {

	//mark id as primary key
	@Id
	//defining id as column name
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idContaCorrente;
	@ManyToOne
    @JoinColumn(name = "idAgencia")
	private Agencia agencia;
	@Column(name = "numero", nullable = false, length = 10)
	private int numero;
	@Column(name = "saldo", nullable = false, precision = 10, scale = 2)
	private BigDecimal saldo;
	@ManyToOne
    @JoinColumn(name = "idCliente")
	private Cliente cliente;
	@OneToMany(mappedBy = "contaCorrente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Extrato> Extratos = new ArrayList<>();
	
	
	public int getIdContaCorrente() {
		return idContaCorrente;
	}
	public void setIdContaCorrente(int idContaCorrente) {
		this.idContaCorrente = idContaCorrente;
	}
	public Agencia getAgencia() {
		return agencia;
	}
	public void setAgencia(Agencia agencia) {
		this.agencia = agencia;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	

}
