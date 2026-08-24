package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateClienteRequest {
    
    @NotBlank (message = "O nome não pode estar em branco")
    private String nome;

    @NotBlank (message = "O email não pode estar em branco")
    private String email;

    @NotBlank (message = "O telefone não pode estar em branco")
    private String telefone;

    @NotBlank (message = "O CPF não pode estar em branco")
    private String cpf;

    private String endereco;

    //getters
    
     public String getCpf() {
        return cpf;
    }


    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    //setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

}
