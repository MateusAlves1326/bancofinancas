package acc.br.bancofinancas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
//mark class as an Entity 
@Entity
//defining class name as Table name
@Table(name = "cliente")
public class Cliente 
{
	//mark id as primary key
	@Id
	//defining id as column name
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idCliente;
	//defining nome as column name
	@Column(name = "nome", nullable = false, length = 45)
	private String nome;
	//defining cpf as column name
	@Column(name = "cpf", nullable = false, length = 20)
	private String cpf;
	//defining telefone as column name
	@Column(name = "telefone", nullable = false, length = 20)
	private String telefone;
	@OneToMany(mappedBy = "agencia", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Cliente> Clientes = new ArrayList<>();
	
	public Cliente() {}
	
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	

}
