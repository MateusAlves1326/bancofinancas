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
import org.springframework.http.HttpStatus;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteComContaRequest;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.service.ClienteService;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void deveListarClientes() {
        List<ClienteResponse> esperados = List.of(new ClienteResponse());
        when(clienteService.findAll()).thenReturn(esperados);

        assertEquals(esperados, clienteController.findAll().getBody());
    }

    @Test
    void deveCriarClienteComStatusCreated() {
        CreateClienteRequest request = new CreateClienteRequest();
        ClienteResponse esperado = new ClienteResponse();
        when(clienteService.createCliente(request)).thenReturn(esperado);

        var response = clienteController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(esperado, response.getBody());
        verify(clienteService).createCliente(request);
    }

    @Test
    void deveCriarClienteComContaComStatusCreated() {
        CreateClienteComContaRequest request = new CreateClienteComContaRequest();
        ClienteResponse esperado = new ClienteResponse();
        when(clienteService.createClienteComConta(request)).thenReturn(esperado);

        var response = clienteController.createComConta(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }
}
