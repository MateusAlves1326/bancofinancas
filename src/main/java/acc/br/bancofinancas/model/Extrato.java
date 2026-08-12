package acc.br.bancofinancas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//mark class as an Entity 
@Entity
//defining class name as Table name
@Table(name = "extrato")
public class Extrato {
	
	
	//mark id as primary key
	@Id
	//defining id as column name
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idExtrato;
	//defining endereco as column name
	@Column(name = "dataHoraMovimento", nullable = false)
	private LocalDateTime dataHoraMovimento;
	//defining endereco as column name
	@Enumerated(EnumType.STRING)
	@Column(name = "operacao", nullable = false, length = 20)
	private Operacao operacao;
	//defining endereco as column name
	@Column(name = "valorOperacao", nullable = false,  precision = 10, scale = 2)
	private BigDecimal valorOperacao;
	@ManyToOne
    @JoinColumn(name = "idContaCorrente")
	private ContaCorrente contaCorrente;
	public int getIdExtrato() {
		return idExtrato;
	}
	public void setIdExtrato(int idExtrato) {
		this.idExtrato = idExtrato;
	}
	public LocalDateTime getDataHoraMovimento() {
		return dataHoraMovimento;
	}
	public void setDataHoraMovimento(LocalDateTime dataHoraMovimento) {
		this.dataHoraMovimento = dataHoraMovimento;
	}
	public Operacao getOperacao() {
		return operacao;
	}
	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}
	public BigDecimal getValorOperacao() {
		return valorOperacao;
	}
	public void setValorOperacao(BigDecimal valorOperacao) {
		this.valorOperacao = valorOperacao;
	}
	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}
	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}


}
