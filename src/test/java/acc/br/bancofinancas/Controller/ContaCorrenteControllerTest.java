package acc.br.bancofinancas.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.service.ContaCorrenteService;

@ExtendWith(MockitoExtension.class)
class ContaCorrenteControllerTest {

    @Mock
    private ContaCorrenteService contaCorrenteService;

    @InjectMocks
    private ContaCorrenteController contaCorrenteController;

    @Test
    void deveListarContas() {
        List<ContaCorrenteResponse> esperadas = List.of(new ContaCorrenteResponse());
        when(contaCorrenteService.findAll()).thenReturn(esperadas);

        assertEquals(esperadas, contaCorrenteController.findAll().getBody());
    }

    @Test
    void deveCriarConta() {
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();
        ContaCorrenteResponse esperado = new ContaCorrenteResponse();
        when(contaCorrenteService.createContaCorrente(request)).thenReturn(esperado);

        var response = contaCorrenteController.createContaCorrente(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(esperado, response.getBody());
        verify(contaCorrenteService).createContaCorrente(request);
    }

    @Test
    void deveDelegarMetodoLegadoDeCriacao() {
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();

        contaCorrenteController.create(request);

        verify(contaCorrenteService).createContaCorrente(request);
    }
}
