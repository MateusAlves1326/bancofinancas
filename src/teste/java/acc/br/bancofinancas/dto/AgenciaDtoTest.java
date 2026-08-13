package acc.br.bancofinancas.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AgenciaDtoTest {

    @Test
    void agenciaResponseGettersESetters() {
        AgenciaResponse dto = new AgenciaResponse();

        dto.setIdAgency(1);
        dto.setName("Agencia 1");
        dto.setAddress("Rua A");
        dto.setPhone("11900000000");
        dto.setIdCustomer(10);

        assertEquals(1, dto.getIdAgency());
        assertEquals("Agencia 1", dto.getName());
        assertEquals("Rua A", dto.getAddress());
        assertEquals("11900000000", dto.getPhone());
        assertEquals(10, dto.getIdCustomer());
    }

    @Test
    void createAgenciaRequestGettersESetters() {
        CreateAgenciaRequest dto = new CreateAgenciaRequest();

        dto.setName("Agencia 1");
        dto.setAddress("Rua A");
        dto.setPhone("11900000000");
        dto.setIdCustomer(10);

        assertEquals("Agencia 1", dto.getName());
        assertEquals("Rua A", dto.getAddress());
        assertEquals("11900000000", dto.getPhone());
        assertEquals(10, dto.getIdCustomer());
    }
}
