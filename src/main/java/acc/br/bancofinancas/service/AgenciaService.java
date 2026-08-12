package acc.br.bancofinancas.service;

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
}
