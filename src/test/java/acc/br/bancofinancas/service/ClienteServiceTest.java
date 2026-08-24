package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteComContaRequest;
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

    @Mock
    private PasswordEncoder passwordEncoder;

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
        when(passwordEncoder.encode("1234")).thenReturn("hashed-password");
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
}
