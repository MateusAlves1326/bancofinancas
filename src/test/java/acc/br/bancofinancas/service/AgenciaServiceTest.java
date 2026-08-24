package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
    void deveCriarAgenciaMapeandoDados() {
        CreateAgenciaRequest request = new CreateAgenciaRequest();
        request.setName("Centro");
        request.setAddress("Rua A");
        request.setPhone("1111");
        request.setIdCustomer(8);
        Agencia salva = agencia(1, "Centro");
        salva.setAddress("Rua A");
        salva.setPhone("1111");
        salva.setIdCustomer(8);
        when(agenciaRepository.save(any(Agencia.class))).thenReturn(salva);

        AgenciaResponse response = agenciaService.createAgencia(request);

        assertEquals(1, response.getIdAgency());
        assertEquals("Centro", response.getName());
        assertEquals("Rua A", response.getAddress());
        assertEquals(8, response.getIdCustomer());
    }

    @Test
    void deveListarAgenciasSemFiltro() {
        Agencia agencia = agencia(1, "Centro");
        when(agenciaRepository.findAllByOrderByNameAsc()).thenReturn(List.of(agencia));

        List<AgenciaResponse> response = agenciaService.listarAgencias(" ");

        assertEquals(1, response.size());
        assertEquals("Centro", response.get(0).getName());
    }

    @Test
    void deveListarAgenciasComFiltro() {
        Agencia agencia = agencia(2, "Norte");
        when(agenciaRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrderByNameAsc("Norte", "Norte"))
                .thenReturn(List.of(agencia));

        List<AgenciaResponse> response = agenciaService.listarAgencias("Norte");

        assertEquals(1, response.size());
        assertEquals(2, response.get(0).getIdAgency());
        verify(agenciaRepository).findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrderByNameAsc("Norte", "Norte");
    }

    private Agencia agencia(int id, String nome) {
        Agencia agencia = new Agencia();
        agencia.setIdAgency(id);
        agencia.setName(nome);
        return agencia;
    }
}
