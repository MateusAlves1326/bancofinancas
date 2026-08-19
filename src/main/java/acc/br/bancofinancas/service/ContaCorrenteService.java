package acc.br.bancofinancas.service;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        validarPermissaoAgencia(agencia);

        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setCliente(cliente);
        contaCorrente.setAgencia(agencia);
        contaCorrente.setNumero(request.getNumero());
        contaCorrente.setSaldo(request.getSaldo());

        return toResponse(contaCorrenteRepository.save(contaCorrente));
    }

    public ContaCorrenteResponse atualizarBloqueio(Long contaCorrenteId, Long clienteId, boolean bloqueada) {
        ContaCorrente conta = contaCorrenteRepository.findById(contaCorrenteId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        validarPermissaoAgencia(conta.getAgencia());

        if (conta.getCliente().getIdCustomer() != clienteId.intValue()) {
            throw new IllegalArgumentException("clienteId informado não corresponde à conta");
        }

        conta.setBloqueada(bloqueada);
        return toResponse(contaCorrenteRepository.save(conta));
    }

    private ContaCorrenteResponse toResponse(ContaCorrente salvo) {
        ContaCorrenteResponse response = new ContaCorrenteResponse();
        response.setId((long) salvo.getIdContaCorrente());
        response.setNumero(salvo.getNumero());
        response.setSaldo(salvo.getSaldo());
        response.setClienteId((long) salvo.getCliente().getIdCustomer());
        response.setAgenciaId((long) salvo.getAgencia().getIdAgency());
        response.setBloqueada(salvo.isBloqueada());

        return response;
    }

    private void validarPermissaoAgencia(Agencia agencia) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return;
        }

        if (user.getRole() != acc.br.bancofinancas.model.Role.AGENCIA
                || user.getAgenciaId() == null
                || agencia.getIdAgency() != user.getAgenciaId()) {
            throw new IllegalArgumentException("Agência só pode operar contas da própria agência");
        }
    }
}
