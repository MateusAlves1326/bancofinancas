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
    void deveCriarAgenciaComStatusCreated() {
        CreateAgenciaRequest request = new CreateAgenciaRequest();
        AgenciaResponse esperado = new AgenciaResponse();
        esperado.setIdAgency(1);
        when(agenciaService.createAgencia(request)).thenReturn(esperado);

        ResponseEntity<AgenciaResponse> response = agenciaController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(esperado, response.getBody());
        verify(agenciaService).createAgencia(request);
    }

    @Test
    void deveListarAgenciasComFiltro() {
        List<AgenciaResponse> esperadas = List.of(new AgenciaResponse());
        when(agenciaService.listarAgencias("Centro")).thenReturn(esperadas);

        List<AgenciaResponse> response = agenciaController.listar("Centro");

        assertEquals(esperadas, response);
        verify(agenciaService).listarAgencias("Centro");
    }
}
