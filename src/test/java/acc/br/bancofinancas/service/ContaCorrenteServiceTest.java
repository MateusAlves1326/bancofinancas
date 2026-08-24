package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    @Test
    void createContaCorrenteRetornaResponseComDadosDoRequest() {
        ContaCorrenteService service = new ContaCorrenteService(
                contaCorrenteRepository, clienteRepository, agenciaRepository);

        Cliente cliente = new Cliente();
        cliente.setIdCustomer(1);
        cliente.setNome("Maria");

        Agencia agencia = new Agencia();
        agencia.setIdAgency(1);

        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();
        request.setAgenciaId(1L);
        request.setNumero(12345);
        request.setSaldo(new BigDecimal("1000.00"));
        request.setClienteId(1L);
        request.setSenha("1234");

        ContaCorrente contaSalva = new ContaCorrente();
        contaSalva.setIdContaCorrente(1);
        contaSalva.setCliente(cliente);
        contaSalva.setAgencia(agencia);
        contaSalva.setNumero(12345);
        contaSalva.setSaldo(new BigDecimal("1000.00"));

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(agenciaRepository.findById(1)).thenReturn(Optional.of(agencia));
        when(contaCorrenteRepository.save(any(ContaCorrente.class))).thenReturn(contaSalva);

        ContaCorrenteResponse response = service.createContaCorrente(request);

        assertEquals(1L, response.getId());
        assertEquals(1L, response.getAgenciaId());
        assertEquals(12345, response.getNumero());
        assertEquals(new BigDecimal("1000.00"), response.getSaldo());
        assertEquals(1L, response.getClienteId());
    }
}
