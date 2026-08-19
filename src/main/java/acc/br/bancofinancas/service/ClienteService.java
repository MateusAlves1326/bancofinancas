package acc.br.bancofinancas.service;

import org.springframework.stereotype.Service;

import java.util.List;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse createCliente(CreateClienteRequest request) {
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());

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
        return response;
    }
}