package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteComContaRequest;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private AgenciaRepository agenciaRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveCriarClienteEContaEmUmaUnicaOperacao() {
        CreateClienteComContaRequest request = new CreateClienteComContaRequest();
        request.setNome("Maria");
        request.setEmail("maria@email.com");
        request.setTelefone("11999999999");
        request.setCpf("12345678909");
        request.setEndereco("Rua A, 10");
        request.setAgenciaId(1L);
        request.setNumero(12345);
        request.setSaldo(new BigDecimal("150.00"));
        request.setSenha("1234");

        Agencia agencia = new Agencia();
        agencia.setIdAgency(1);

        when(clienteRepository.existsByCpf("12345678909")).thenReturn(false);
        when(agenciaRepository.findById(1)).thenReturn(Optional.of(agencia));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCustomer(10);
            return cliente;
        });
        when(contaCorrenteRepository.save(any(ContaCorrente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponse response = clienteService.createClienteComConta(request);

        assertNotNull(response);
        assertEquals("Maria", response.getNome());
        assertEquals(10, response.getIdCustomer());
        verify(clienteRepository).save(any(Cliente.class));
        verify(contaCorrenteRepository).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfDoClienteComContaJaExiste() {
        CreateClienteComContaRequest request = criarRequest();
        when(clienteRepository.existsByCpf(request.getCpf())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clienteService.createClienteComConta(request));

        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoAgenciaNaoExiste() {
        CreateClienteComContaRequest request = criarRequest();
        when(agenciaRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clienteService.createClienteComConta(request));

        assertEquals("Agência não encontrada", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNumeroDaContaJaExiste() {
        CreateClienteComContaRequest request = criarRequest();
        Agencia agencia = new Agencia();
        agencia.setIdAgency(1);

        when(agenciaRepository.findById(1)).thenReturn(Optional.of(agencia));
        when(contaCorrenteRepository.existsByAgencia_IdAgencyAndNumero(1, 12345)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clienteService.createClienteComConta(request));

        assertEquals("Número de conta já cadastrado para esta agência", exception.getMessage());
    }

    @Test
    void deveUsarSenhaPadraoQuandoSenhaNaoForInformada() {
        CreateClienteComContaRequest request = criarRequest();
        request.setSenha(null);
        Agencia agencia = new Agencia();
        agencia.setIdAgency(1);

        when(agenciaRepository.findById(1)).thenReturn(Optional.of(agencia));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCustomer(10);
            return cliente;
        });

        clienteService.createClienteComConta(request);

        ArgumentCaptor<ContaCorrente> captor = ArgumentCaptor.forClass(ContaCorrente.class);
        verify(contaCorrenteRepository).save(captor.capture());
        assertEquals("0000", captor.getValue().getSenha());
    }

    @Test
    void deveCriarClienteSemConta() {
        CreateClienteRequest request = new CreateClienteRequest();
        request.setNome("João");
        request.setCpf("98765432100");
        request.setEmail("joao@email.com");
        request.setTelefone("11988888888");
        request.setEndereco("Rua B, 20");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setIdCustomer(11);
        clienteSalvo.setNome("João");
        clienteSalvo.setCpf(request.getCpf());
        clienteSalvo.setEmail(request.getEmail());
        clienteSalvo.setTelefone(request.getTelefone());
        clienteSalvo.setEndereco(request.getEndereco());

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponse response = clienteService.createCliente(request);

        assertEquals(11, response.getIdCustomer());
        assertEquals("João", response.getNome());
        assertEquals("Rua B, 20", response.getEndereco());
    }

    @Test
    void deveLancarExcecaoAoCriarClienteComCpfExistente() {
        CreateClienteRequest request = new CreateClienteRequest();
        request.setCpf("12345678909");
        when(clienteRepository.existsByCpf(request.getCpf())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clienteService.createCliente(request));

        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    @Test
    void deveListarClientesMapeandoTodosOsDados() {
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(12);
        cliente.setNome("Ana");
        cliente.setCpf("11122233344");
        cliente.setEmail("ana@email.com");
        cliente.setTelefone("11977777777");
        cliente.setEndereco("Rua C, 30");
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<ClienteResponse> response = clienteService.findAll();

        assertEquals(1, response.size());
        assertEquals("Ana", response.get(0).getNome());
        assertEquals("11122233344", response.get(0).getCpf());
        assertEquals("ana@email.com", response.get(0).getEmail());
        assertEquals("11977777777", response.get(0).getTelefone());
        assertEquals("Rua C, 30", response.get(0).getEndereco());
    }

    private CreateClienteComContaRequest criarRequest() {
        CreateClienteComContaRequest request = new CreateClienteComContaRequest();
        request.setNome("Maria");
        request.setEmail("maria@email.com");
        request.setTelefone("11999999999");
        request.setCpf("12345678909");
        request.setAgenciaId(1L);
        request.setNumero(12345);
        request.setSaldo(new BigDecimal("150.00"));
        request.setSenha("1234");
        return request;
    }
}
