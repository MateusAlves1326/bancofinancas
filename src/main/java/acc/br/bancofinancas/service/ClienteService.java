package acc.br.bancofinancas.service;

import acc.br.bancofinancas.dto.CreateClienteComContaRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            AgenciaRepository agenciaRepository,
            ContaCorrenteRepository contaCorrenteRepository) {
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
    }

    @Transactional
    public ClienteResponse createClienteComConta(CreateClienteComContaRequest request) {
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        Agencia agencia = agenciaRepository.findById(request.getAgenciaId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Agência não encontrada"));

        if (contaCorrenteRepository.existsByAgencia_IdAgencyAndNumero(agencia.getIdAgency(), request.getNumero())) {
            throw new IllegalArgumentException("Número de conta já cadastrado para esta agência");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setEndereco(request.getEndereco());

        Cliente salvo = clienteRepository.save(cliente);

        ContaCorrente conta = new ContaCorrente();
        conta.setCliente(salvo);
        conta.setAgencia(agencia);
        conta.setNumero(request.getNumero());
        conta.setSaldo(request.getSaldo());
        conta.setSenha(request.getSenha() == null || request.getSenha().isBlank() ? "0000" : request.getSenha());

        contaCorrenteRepository.save(conta);

        return toResponse(salvo);
    }

    public ClienteResponse createCliente(CreateClienteRequest request) {
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setEndereco(request.getEndereco());

        Cliente salvo = clienteRepository.save(cliente);

        return toResponse(salvo);
    }

    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setIdCustomer(cliente.getIdCustomer());
        response.setNome(cliente.getNome());
        response.setCpf(cliente.getCpf());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        response.setEndereco(cliente.getEndereco());
        return response;
    }
}