package acc.br.bancofinancas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * CreateAgenciaRequest
 * essa classe representa a estrutura de dados para criar uma nova agência bancária.
 * utilizamos NotBlank para validar que os campos nome, endereço e telefone não podem ser nulos ou vazios.
 * utilizamos NotNull para validar que o campo idAgencia não pode ser nulo.
 */

public class CreateAgenciaRequest {
    @NotBlank(message = "Nome é obrigatório")
private String name;

@NotBlank(message = "Endereço é obrigatório")
private String address;

@NotBlank(message = "Telefone é obrigatório")
private String phone;

@NotNull(message = "idCliente é obrigatório")
private Integer idCustomer;

public String getName() { return name; }
public void setName(String name) { this.name = name; }

public String getAddress() { return address; }
public void setAddress(String address) { this.address = address; }

public String getPhone() { return phone; }
public void setPhone(String phone) { this.phone = phone; }

public Integer getIdCustomer() { return idCustomer; }
public void setIdCustomer(Integer idCustomer) { this.idCustomer = idCustomer; }
}
