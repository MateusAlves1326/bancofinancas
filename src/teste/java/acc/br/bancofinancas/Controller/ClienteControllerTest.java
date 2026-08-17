package acc.br.bancofinancas.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.service.ClienteService;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void createDeveRetornarCreatedComBody() {
        CreateClienteRequest request = new CreateClienteRequest();
        ClienteResponse responseEsperado = new ClienteResponse();
        responseEsperado.setIdCustomer(1);

        when(clienteService.createCliente(request)).thenReturn(responseEsperado);

        ResponseEntity<ClienteResponse> response = clienteController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseEsperado, response.getBody());
        verify(clienteService).createCliente(request);
    }
}
