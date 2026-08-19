package acc.br.bancofinancas.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Agente;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.AgenteRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;

@Component
@ConditionalOnProperty(name = "app.carga-inicial.enabled", havingValue = "true")
public class CargaInicial implements CommandLineRunner {

    private static final int QUANTIDADE_AGENCIAS = 10;
    private static final int CLIENTES_POR_AGENCIA = 50;

    private final AgenciaRepository agenciaRepository;
    private final AgenteRepository agenteRepository;
    private final ClienteRepository clienteRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;

    public CargaInicial(
            AgenciaRepository agenciaRepository,
            AgenteRepository agenteRepository,
            ClienteRepository clienteRepository,
            ContaCorrenteRepository contaCorrenteRepository) {
        this.agenciaRepository = agenciaRepository;
        this.agenteRepository = agenteRepository;
        this.clienteRepository = clienteRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (int agenciaIndice = 1; agenciaIndice <= QUANTIDADE_AGENCIAS; agenciaIndice++) {
            Agencia agencia = obterOuCriarAgencia(agenciaIndice);
            criarAgenteSeNecessario(agencia, agenciaIndice);

            for (int clienteIndice = 1; clienteIndice <= CLIENTES_POR_AGENCIA; clienteIndice++) {
                Cliente cliente = obterOuCriarCliente(agenciaIndice, clienteIndice);
                criarContaSeNecessario(agencia, cliente, agenciaIndice, clienteIndice);
            }
        }
    }

    private Agencia obterOuCriarAgencia(int agenciaIndice) {
        String nome = String.format("Agencia Carga %02d", agenciaIndice);
        return agenciaRepository.findByName(nome).orElseGet(() -> {
            Agencia agencia = new Agencia();
            agencia.setName(nome);
            agencia.setAddress("Rua da Carga, " + agenciaIndice);
            agencia.setPhone(String.format("113000%04d", agenciaIndice));
            return agenciaRepository.save(agencia);
        });
    }

    private void criarAgenteSeNecessario(Agencia agencia, int agenciaIndice) {
        String matricula = String.format("AGC-%02d", agenciaIndice);
        if (agenteRepository.existsByMatricula(matricula)) {
            return;
        }

        Agente agente = new Agente();
        agente.setNome("Agente Carga " + agenciaIndice);
        agente.setMatricula(matricula);
        agente.setAgencia(agencia);
        agenteRepository.save(agente);
    }

    private Cliente obterOuCriarCliente(int agenciaIndice, int clienteIndice) {
        String cpf = String.format("900%08d", agenciaIndice * 1000 + clienteIndice);
        return clienteRepository.findByCpf(cpf).orElseGet(() -> {
            Cliente cliente = new Cliente();
            cliente.setNome(String.format("Cliente %02d-%02d", agenciaIndice, clienteIndice));
            cliente.setCpf(cpf);
            cliente.setEmail(String.format("cliente-%02d-%02d@carga.local", agenciaIndice, clienteIndice));
            cliente.setTelefone(String.format("119%08d", agenciaIndice * 1000 + clienteIndice));
            return clienteRepository.save(cliente);
        });
    }

    private void criarContaSeNecessario(Agencia agencia, Cliente cliente, int agenciaIndice, int clienteIndice) {
        if (contaCorrenteRepository.existsByCliente_IdCustomerAndAgencia_IdAgency(
                cliente.getIdCustomer(), agencia.getIdAgency())) {
            return;
        }

        ContaCorrente conta = new ContaCorrente();
        conta.setAgencia(agencia);
        conta.setCliente(cliente);
        conta.setNumero(agenciaIndice * 1000 + clienteIndice);
        conta.setSaldo(BigDecimal.ZERO);
        contaCorrenteRepository.save(conta);
    }
}