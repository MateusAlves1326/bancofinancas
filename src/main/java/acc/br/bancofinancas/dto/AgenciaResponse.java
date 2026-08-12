package acc.br.bancofinancas.dto;

/**
 * AgenciaResponse
 * essa classe representa a estrutura de dados para a resposta de uma agência bancária.
 * utilizamos getters e setters para acessar e modificar os campos da classe.
 * OBS: Id foi substituído por idAgencia para melhor representar a entidade.
 */

public class AgenciaResponse {
    private String name;
    private String address;
    private String phone;
    private Integer idAgency;
    private Integer idCustomer;

//getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getIdAgency() {
        return idAgency;
    }

    public Integer getIdCustomer() {
        return idCustomer;
    }
//setters
    public void setIdAgency(Integer idAgency) {
        this.idAgency = idAgency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIdCustomer(Integer idCustomer) {
        this.idCustomer = idCustomer;
    }
}
