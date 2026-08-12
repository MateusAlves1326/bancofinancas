package acc.br.bancofinancas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "agencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaCorrente> contas = new ArrayList<>();

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

    public List<ContaCorrente> getContas() {
        return contas;
    }

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

    public void setContas(List<ContaCorrente> contas) {
        this.contas = contas;
    }
}
