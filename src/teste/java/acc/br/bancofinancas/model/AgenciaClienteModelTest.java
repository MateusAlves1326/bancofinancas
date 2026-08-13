package acc.br.bancofinancas.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class AgenciaClienteModelTest {

    @Test
    void agenciaGettersESetters() {
        Agencia agencia = new Agencia();
        List<ContaCorrente> contas = new ArrayList<>();

        agencia.setIdAgency(1);
        agencia.setName("Agencia Centro");
        agencia.setAddress("Rua A");
        agencia.setPhone("11900000000");
        agencia.setIdCustomer(9);
        agencia.setContas(contas);

        assertEquals(1, agencia.getIdAgency());
        assertEquals("Agencia Centro", agencia.getName());
        assertEquals("Rua A", agencia.getAddress());
        assertEquals("11900000000", agencia.getPhone());
        assertEquals(9, agencia.getIdCustomer());
        assertEquals(contas, agencia.getContas());
    }

    @Test
    void clienteConstrutorPadraoGettersESetters() {
        Cliente cliente = new Cliente();
        List<ContaCorrente> contas = new ArrayList<>();

        cliente.setIdCustomer(2);
        cliente.setNome("Maria");
        cliente.setCpf("12345678900");
        cliente.setTelefone("11911111111");
        cliente.setEmail("maria@email.com");
        cliente.setContas(contas);

        assertEquals(2, cliente.getIdCustomer());
        assertEquals("Maria", cliente.getNome());
        assertEquals("12345678900", cliente.getCpf());
        assertEquals("11911111111", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
        assertEquals(contas, cliente.getContas());
    }
}
