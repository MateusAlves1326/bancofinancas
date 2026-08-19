package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import acc.br.bancofinancas.dto.AgenteResponse;
import acc.br.bancofinancas.dto.CreateAgenteRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Agente;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.AgenteRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;

@ExtendWith(MockitoExtension.class)
class AgenteServiceTest {

    @Mock
    private AgenteRepository agenteRepository;

    @Mock
    private AgenciaRepository agenciaRepository;

    @InjectMocks
    private AgenteService agenteService;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criarAgenteDeveVincularFuncionarioAAgencia() {
        CreateAgenteRequest request = new CreateAgenteRequest();
        request.setNome("Ana Silva");
        request.setMatricula("AG-123");
        request.setAgenciaId(2L);

        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);

        Agente agenteSalvo = new Agente();
        agenteSalvo.setId(10);
        agenteSalvo.setNome("Ana Silva");
        agenteSalvo.setMatricula("AG-123");
        agenteSalvo.setAgencia(agencia);

        when(agenteRepository.existsByMatricula("AG-123")).thenReturn(false);
        when(agenciaRepository.findById(2)).thenReturn(Optional.of(agencia));
        when(agenteRepository.save(any(Agente.class))).thenReturn(agenteSalvo);

        AgenteResponse response = agenteService.criarAgente(request);

        assertEquals(10L, response.getId());
        assertEquals("Ana Silva", response.getNome());
        assertEquals("AG-123", response.getMatricula());
        assertEquals(2L, response.getAgenciaId());
    }

    @Test
    void criarAgenteDeveRecusarMatriculaDuplicada() {
        CreateAgenteRequest request = new CreateAgenteRequest();
        request.setMatricula("AG-123");

        when(agenteRepository.existsByMatricula("AG-123")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> agenteService.criarAgente(request));

        assertEquals("Matrícula já cadastrada", exception.getMessage());
    }

    @Test
    void criarAgenteDeveRecusarAgenciaDiferenteDaAutenticada() {
        CreateAgenteRequest request = new CreateAgenteRequest();
        request.setNome("Ana Silva");
        request.setMatricula("AG-123");
        request.setAgenciaId(3L);

        Agencia agencia = new Agencia();
        agencia.setIdAgency(3);

        AuthenticatedUser usuario = new AuthenticatedUser("agente", "senha", Role.AGENCIA, null, 2);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));

        when(agenteRepository.existsByMatricula("AG-123")).thenReturn(false);
        when(agenciaRepository.findById(3)).thenReturn(Optional.of(agencia));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> agenteService.criarAgente(request));

        assertEquals("Agência só pode cadastrar agentes da própria agência", exception.getMessage());
    }
}