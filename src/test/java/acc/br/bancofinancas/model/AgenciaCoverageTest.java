package acc.br.bancofinancas.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AgenciaCoverageTest {

    @Test
    void deveLerDadosDaAgencia() {
        Agencia agencia = new Agencia();
        agencia.setIdAgency(3);
        agencia.setName("Centro");
        agencia.setAddress("Rua A");
        agencia.setPhone("1111");
        agencia.setIdCustomer(7);

        assertEquals(3, agencia.getIdAgency());
        assertEquals("Centro", agencia.getName());
        assertEquals("Rua A", agencia.getAddress());
        assertEquals("1111", agencia.getPhone());
        assertEquals(7, agencia.getIdCustomer());
        assertEquals(0, agencia.getContas().size());
    }
}
