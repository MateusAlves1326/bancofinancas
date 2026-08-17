package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;

class ContaCorrenteServiceTest {

    @Test
    void createContaCorrenteRetornaResponseComDadosDoRequest() {
        ContaCorrenteService service = new ContaCorrenteService();
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();
        request.setIdAgencia(1);
        request.setNumero(12345);
        request.setSaldo(new BigDecimal("1000.00"));
        request.setIdCliente(1);

        ContaCorrenteResponse response = service.createContaCorrente(request);

        assertEquals(1, response.getIdContaCorrente());
        assertEquals(1, response.getIdAgencia());
        assertEquals(12345, response.getNumero());
        assertEquals(new BigDecimal("1000.00"), response.getSaldo());
        assertEquals(1, response.getIdCliente());
    }
}
