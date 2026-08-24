package acc.br.bancofinancas.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import acc.br.bancofinancas.dto.AgenteResponse;
import acc.br.bancofinancas.dto.CreateAgenteRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Agente;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.AgenteRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;

@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;
    private final AgenciaRepository agenciaRepository;

    public AgenteService(AgenteRepository agenteRepository, AgenciaRepository agenciaRepository) {
        this.agenteRepository = agenteRepository;
        this.agenciaRepository = agenciaRepository;
    }

    public AgenteResponse criarAgente(CreateAgenteRequest request) {
        if (agenteRepository.existsByMatricula(request.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já cadastrada");
        }

        Agencia agencia = agenciaRepository.findById(request.getAgenciaId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Agência não encontrada"));

        validarPermissaoAgencia(agencia);

        Agente agente = new Agente();
        agente.setNome(request.getNome());
        agente.setMatricula(request.getMatricula());
        agente.setAgencia(agencia);

        return toResponse(agenteRepository.save(agente));
    }

    private AgenteResponse toResponse(Agente agente) {
        AgenteResponse response = new AgenteResponse();
        response.setId(agente.getId().longValue());
        response.setNome(agente.getNome());
        response.setMatricula(agente.getMatricula());
        response.setAgenciaId(agente.getAgencia().getIdAgency().longValue());
        return response;
    }

    private void validarPermissaoAgencia(Agencia agencia) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return;
        }

        boolean permitido = user.getRole() == Role.AGENCIA || user.getRole() == Role.ADMIN;

        if (!permitido) {
            throw new IllegalArgumentException("Usuário sem permissão para cadastrar agentes");
        }

        if (user.getRole() == Role.AGENCIA
                && (user.getAgenciaId() == null || agencia.getIdAgency() != user.getAgenciaId())) {
            throw new IllegalArgumentException("Agência só pode cadastrar agentes da própria agência");
        }
    }
}