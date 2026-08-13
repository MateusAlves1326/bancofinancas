package acc.br.bancofinancas.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ClienteDtoTest {

    @Test
    void clienteResponseGettersESetters() {
        ClienteResponse dto = new ClienteResponse();

        dto.setIdCustomer(2);
        dto.setNome("Joao");
        dto.setCpf("12345678900");
        dto.setEmail("joao@email.com");
        dto.setTelefone("11911111111");

        assertEquals(2, dto.getIdCustomer());
        assertEquals("Joao", dto.getNome());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("11911111111", dto.getTelefone());
    }

    @Test
    void createClienteRequestGettersESetters() {
        CreateClienteRequest dto = new CreateClienteRequest();

        dto.setNome("Joao");
        dto.setCpf("12345678900");
        dto.setEmail("joao@email.com");
        dto.setTelefone("11911111111");

        assertEquals("Joao", dto.getNome());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("11911111111", dto.getTelefone());
    }
}
