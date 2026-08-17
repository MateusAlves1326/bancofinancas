package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.AgenciaResponse;
import acc.br.bancofinancas.dto.CreateAgenciaRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.repository.AgenciaRepository;

@ExtendWith(MockitoExtension.class)
class AgenciaServiceTest {

    @Mock
    private AgenciaRepository agenciaRepository;

    @InjectMocks
    private AgenciaService agenciaService;

    @Test
    void createAgenciaDeveMapearSalvarERetornarResponse() {
        CreateAgenciaRequest request = new CreateAgenciaRequest();
        request.setName("Agencia Centro");
        request.setAddress("Rua A");
        request.setPhone("11999999999");
        request.setIdCustomer(10);

        Agencia salvo = new Agencia();
        salvo.setIdAgency(1);
        salvo.setName("Agencia Centro");
        salvo.setAddress("Rua A");
        salvo.setPhone("11999999999");
        salvo.setIdCustomer(10);

        when(agenciaRepository.save(any(Agencia.class))).thenReturn(salvo);

        AgenciaResponse response = agenciaService.createAgencia(request);

        assertEquals(1, response.getIdAgency());
        assertEquals("Agencia Centro", response.getName());
        assertEquals("Rua A", response.getAddress());
        assertEquals("11999999999", response.getPhone());
        assertEquals(10, response.getIdCustomer());
    }
}
