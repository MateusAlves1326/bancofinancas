package acc.br.bancofinancas.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;


//mark class as an Entity 
@Entity
//defining class name as Table name
@Table(name = "agencia")
public class Agencia {
	
	//mark id as primary key
	@Id
	//defining id as column name
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idAgencia;
	//defining nome as column name
	@Column(name = "nome", nullable = false, length = 45)
	private String nome;
	//defining endereco as column name
	@Column(name = "endereco", nullable = false, length = 100)
	private String endereco;
	//defining telefone as column name
	@Column(name = "telefone", nullable = false, length = 20)
	private String telefone;
	//defining nome as column name
	@Column(name = "idCliente", nullable = false)
	@ManyToOne
    @JoinColumn(name = "idCliente")
	private Cliente cliente;
	
	public int getIdAgencia() {
		return idAgencia;
	}
	
	public void setIdAgencia(int idAgencia) {
		this.idAgencia = idAgencia;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}


}
