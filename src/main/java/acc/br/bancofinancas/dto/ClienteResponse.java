package acc.br.bancofinancas.dto;

public class ClienteResponse {
    private Integer idCustomer;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String endereco;


    public Integer getIdCustomer() {
        return idCustomer;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    } 

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setIdCustomer(Integer idCustomer) {
        this.idCustomer = idCustomer;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    }
