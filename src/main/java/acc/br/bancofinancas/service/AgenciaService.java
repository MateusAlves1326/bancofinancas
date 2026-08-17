package acc.br.bancofinancas.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import acc.br.bancofinancas.dto.AgenciaResponse;
import acc.br.bancofinancas.dto.CreateAgenciaRequest;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.repository.AgenciaRepository;

@Service
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;

    public AgenciaService(AgenciaRepository agenciaRepository) {
        this.agenciaRepository = agenciaRepository;
    }

    public AgenciaResponse createAgencia(CreateAgenciaRequest request) {
        Agencia agencia = new Agencia();
        agencia.setName(request.getName());
        agencia.setAddress(request.getAddress());
        agencia.setPhone(request.getPhone());
        agencia.setIdCustomer(request.getIdCustomer());

        Agencia salva = agenciaRepository.save(agencia);

        AgenciaResponse response = new AgenciaResponse();
        response.setIdAgency(salva.getIdAgency());
        response.setName(salva.getName());
        response.setAddress(salva.getAddress());
        response.setPhone(salva.getPhone());
        response.setIdCustomer(salva.getIdCustomer());

        return response;
    }

    public List<AgenciaResponse> listarAgencias(String localizacao) {
        List<Agencia> agencias;

        if (localizacao == null || localizacao.isBlank()) {
            agencias = agenciaRepository.findAllByOrderByNameAsc();
        } else {
            agencias = agenciaRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrderByNameAsc(
                    localizacao, localizacao);
        }

        return agencias.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AgenciaResponse toResponse(Agencia agencia) {
        AgenciaResponse response = new AgenciaResponse();
        response.setIdAgency(agencia.getIdAgency());
        response.setName(agencia.getName());
        response.setAddress(agencia.getAddress());
        response.setPhone(agencia.getPhone());
        response.setIdCustomer(agencia.getIdCustomer());
        return response;
    }
}
