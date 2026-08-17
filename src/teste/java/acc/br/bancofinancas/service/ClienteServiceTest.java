package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void createClienteDeveLancarExcecaoQuandoCpfJaExiste() {
        CreateClienteRequest request = new CreateClienteRequest();
        request.setCpf("12345678900");

        when(clienteRepository.existsByCpf("12345678900")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteService.createCliente(request));

        assertEquals("CPF já cadastrado", ex.getMessage());
    }

    @Test
    void createClienteDeveMapearSalvarERetornarResponse() {
        CreateClienteRequest request = new CreateClienteRequest();
        request.setNome("Maria");
        request.setCpf("12345678900");
        request.setEmail("maria@email.com");
        request.setTelefone("11911111111");

        Cliente salvo = new Cliente();
        salvo.setIdCustomer(5);
        salvo.setNome("Maria");
        salvo.setCpf("12345678900");
        salvo.setEmail("maria@email.com");
        salvo.setTelefone("11911111111");

        when(clienteRepository.existsByCpf("12345678900")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(salvo);

        ClienteResponse response = clienteService.createCliente(request);

        assertEquals(5, response.getIdCustomer());
        assertEquals("Maria", response.getNome());
        assertEquals("12345678900", response.getCpf());
        assertEquals("maria@email.com", response.getEmail());
        assertEquals("11911111111", response.getTelefone());
    }
}
