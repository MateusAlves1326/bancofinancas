package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class CreateClienteComContaRequest {

    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "O email não pode estar em branco")
    private String email;

    @NotBlank(message = "O telefone não pode estar em branco")
    private String telefone;

    @NotBlank(message = "O CPF não pode estar em branco")
    private String cpf;

    private String endereco;

    @NotNull(message = "A agência é obrigatória")
    private Long agenciaId;

    @NotNull(message = "O número da conta é obrigatório")
    private Integer numero;

    @NotNull(message = "O saldo inicial é obrigatório")
    @DecimalMin(value = "0.00", message = "O saldo inicial não pode ser negativo")
    private BigDecimal saldo;

    @Pattern(regexp = "\\d{4}", message = "A senha deve ter exatamente 4 dígitos")
    private String senha;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}