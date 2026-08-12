package acc.br.bancofinancas.service;

import org.springframework.stereotype.Service;

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

        ClienteResponse response = new ClienteResponse();
        response.setIdCustomer(salvo.getIdCustomer());
        response.setNome(salvo.getNome());
        response.setCpf(salvo.getCpf());
        response.setEmail(salvo.getEmail());
        response.setTelefone(salvo.getTelefone());

        return response;
    }
}