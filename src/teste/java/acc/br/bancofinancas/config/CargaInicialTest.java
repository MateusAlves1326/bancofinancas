package acc.br.bancofinancas.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.AgenteRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;

@ExtendWith(MockitoExtension.class)
class CargaInicialTest {

    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private AgenteRepository agenteRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @InjectMocks
    private CargaInicial cargaInicial;

    @Test
    void deveCriarDezAgenciasComUmAgenteECinquentaClientesCada() throws Exception {
        when(agenciaRepository.findByName(anyString())).thenAnswer(invocation -> Optional.empty());
        when(agenciaRepository.save(any(Agencia.class))).thenAnswer(invocation -> {
            Agencia agencia = invocation.getArgument(0);
            agencia.setIdAgency(1);
            return agencia;
        });
        when(agenteRepository.existsByMatricula(anyString())).thenReturn(false);
        when(clienteRepository.findByCpf(anyString())).thenAnswer(invocation -> Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCustomer(1);
            return cliente;
        });
        when(contaCorrenteRepository.existsByCliente_IdCustomerAndAgencia_IdAgency(anyInt(), anyInt()))
                .thenReturn(false);

        cargaInicial.run();

        verify(agenciaRepository, times(10)).save(any(Agencia.class));
        verify(agenteRepository, times(10)).save(any());
        verify(clienteRepository, times(500)).save(any(Cliente.class));
        verify(contaCorrenteRepository, times(500)).save(any());
    }
}