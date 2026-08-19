package acc.br.bancofinancas.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;

@ExtendWith(MockitoExtension.class)
class ContaCorrenteServiceTest {

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AgenciaRepository agenciaRepository;

    @InjectMocks
    private ContaCorrenteService contaCorrenteService;

    @Test
    void createContaCorrenteDeveLancarExcecaoQuandoClienteNaoEncontrado() {
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();
        request.setClienteId(1L);
        request.setAgenciaId(2L);

        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contaCorrenteService.createContaCorrente(request));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void createContaCorrenteDeveLancarExcecaoQuandoAgenciaNaoEncontrada() {
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();
        request.setClienteId(1L);
        request.setAgenciaId(2L);

        Cliente cliente = new Cliente();
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(agenciaRepository.findById(2)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contaCorrenteService.createContaCorrente(request));

        assertEquals("Agência não encontrada", ex.getMessage());
    }

    @Test
    void createContaCorrenteDeveSalvarERetornarResponse() {
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();
        request.setClienteId(1L);
        request.setAgenciaId(2L);
        request.setNumero(1234);
        request.setSaldo(new BigDecimal("100.00"));

        Cliente cliente = new Cliente();
        cliente.setIdCustomer(1);

        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);

        ContaCorrente salvo = new ContaCorrente();
        salvo.setIdContaCorrente(99);
        salvo.setNumero(1234);
        salvo.setSaldo(new BigDecimal("100.00"));
        salvo.setCliente(cliente);
        salvo.setAgencia(agencia);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(agenciaRepository.findById(2)).thenReturn(Optional.of(agencia));
        when(contaCorrenteRepository.save(any(ContaCorrente.class))).thenReturn(salvo);

        ContaCorrenteResponse response = contaCorrenteService.createContaCorrente(request);

        assertEquals(99L, response.getId());
        assertEquals(1234, response.getNumero());
        assertEquals(new BigDecimal("100.00"), response.getSaldo());
        assertEquals(1L, response.getClienteId());
        assertEquals(2L, response.getAgenciaId());
    }

    @Test
    void atualizarBloqueioDevePersistirNovoEstadoDaConta() {
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(1);

        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);

        ContaCorrente conta = new ContaCorrente();
        conta.setIdContaCorrente(99);
        conta.setNumero(1234);
        conta.setSaldo(new BigDecimal("100.00"));
        conta.setCliente(cliente);
        conta.setAgencia(agencia);

        when(contaCorrenteRepository.findById(99)).thenReturn(Optional.of(conta));
        when(contaCorrenteRepository.save(conta)).thenReturn(conta);

        ContaCorrenteResponse response = contaCorrenteService.atualizarBloqueio(99L, 1L, true);

        assertEquals(true, conta.isBloqueada());
        assertEquals(true, response.isBloqueada());
    }
}
