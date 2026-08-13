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

import acc.br.bancofinancas.dto.AgenciaResponse;
import acc.br.bancofinancas.dto.CreateAgenciaRequest;
import acc.br.bancofinancas.service.AgenciaService;

@ExtendWith(MockitoExtension.class)
class AgenciaControllerTest {

    @Mock
    private AgenciaService agenciaService;

    @InjectMocks
    private AgenciaController agenciaController;

    @Test
    void createDeveRetornarCreatedComBody() {
        CreateAgenciaRequest request = new CreateAgenciaRequest();
        AgenciaResponse responseEsperado = new AgenciaResponse();
        responseEsperado.setIdAgency(1);

        when(agenciaService.createAgencia(request)).thenReturn(responseEsperado);

        ResponseEntity<AgenciaResponse> response = agenciaController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseEsperado, response.getBody());
        verify(agenciaService).createAgencia(request);
    }
}
