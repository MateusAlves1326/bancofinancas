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

import java.util.List;

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

        String senha = request.getSenha() == null || request.getSenha().isBlank()
                ? "0000"
                : request.getSenha();

        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setCliente(cliente);
        contaCorrente.setAgencia(agencia);
        contaCorrente.setNumero(request.getNumero());
        contaCorrente.setSaldo(request.getSaldo());
        contaCorrente.setSenha(senha);

        return toResponse(contaCorrenteRepository.save(contaCorrente));
    }

    public List<ContaCorrenteResponse> findAll() {
        return contaCorrenteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

        public ContaCorrenteResponse atualizarBloqueio(
            Long contaCorrenteId, Long clienteId, boolean bloqueada, String motivo) {
        ContaCorrente conta = contaCorrenteRepository.findById(contaCorrenteId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        validarPermissaoAgencia(conta.getAgencia());

        if (conta.getCliente().getIdCustomer() != clienteId.intValue()) {
            throw new IllegalArgumentException("clienteId informado não corresponde à conta");
        }

        conta.setBloqueada(bloqueada);
        if (bloqueada) {
            conta.setMotivoBloqueio(motivo.trim());
        } else {
            conta.setMotivoDesbloqueio(motivo.trim());
        }
        return toResponse(contaCorrenteRepository.save(conta));
    }

    private ContaCorrenteResponse toResponse(ContaCorrente salvo) {
        ContaCorrenteResponse response = new ContaCorrenteResponse();
        response.setId((long) salvo.getIdContaCorrente());
        response.setNumero(salvo.getNumero());
        response.setSaldo(salvo.getSaldo());
        response.setClienteId((long) salvo.getCliente().getIdCustomer());
        response.setClienteNome(salvo.getCliente().getNome());
        response.setAgenciaId((long) salvo.getAgencia().getIdAgency());
        response.setBloqueada(salvo.isBloqueada());
        response.setMotivoBloqueio(salvo.getMotivoBloqueio());
        response.setMotivoDesbloqueio(salvo.getMotivoDesbloqueio());

        return response;
    }

    private void validarPermissaoAgencia(Agencia agencia) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return;
        }

        boolean permitido = user.getRole() == acc.br.bancofinancas.model.Role.AGENCIA
                || user.getRole() == acc.br.bancofinancas.model.Role.ADMIN;

        if (!permitido) {
            throw new IllegalArgumentException("Usuário sem permissão para operar contas");
        }

        if (user.getRole() == acc.br.bancofinancas.model.Role.AGENCIA
                && (user.getAgenciaId() == null || agencia.getIdAgency() != user.getAgenciaId())) {
            throw new IllegalArgumentException("Agência só pode operar contas da própria agência");
        }
    }
}
