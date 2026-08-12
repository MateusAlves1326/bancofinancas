package acc.br.bancofinancas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agencias")
public class Agencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAgency;

    private String name;
    private String address; 
    private String phone;
    private Integer idCustomer;
// Getters
    public Integer getIdAgency() {
        return idAgency;
    }
  
    public String getName() {
        return name;
    }
  
    public String getAddress() {
        return address;
    }
    
    public String getPhone() {
        return phone;
    }

    public Integer getIdCustomer() {
        return idCustomer;
    }

    // Setters
    
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
