package acc.br.bancofinancas.service;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import org.springframework.stereotype.Service;

@Service
public class ContaCorrenteService {
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;

    public ContaCorrenteService(
            ContaCorrenteRepository contaCorrenteRepository,
            ClienteRepository clienteRepository,
            AgenciaRepository agenciaRepository) {
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
    }

    public ContaCorrenteResponse createContaCorrente(CreateContaCorrenteRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Agencia agencia = agenciaRepository.findById(request.getAgenciaId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Agência não encontrada"));

        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setCliente(cliente);
        contaCorrente.setAgencia(agencia);
        contaCorrente.setNumero(request.getNumero());
        contaCorrente.setSaldo(request.getSaldo());

        ContaCorrente salvo = contaCorrenteRepository.save(contaCorrente);

        ContaCorrenteResponse response = new ContaCorrenteResponse();
        response.setId((long) salvo.getIdContaCorrente());
        response.setNumero(salvo.getNumero());
        response.setSaldo(salvo.getSaldo());
        response.setClienteId((long) salvo.getCliente().getIdCustomer());
        response.setAgenciaId((long) salvo.getAgencia().getIdAgency());

        return response;
    }
}