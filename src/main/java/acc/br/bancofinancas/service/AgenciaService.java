package acc.br.bancofinancas.service;

/**
 * Essa classe é responsável por gerenciar as operações relacionadas às agências bancárias.
 * Ela utiliza um AtomicInteger para gerar IDs únicos para cada agência criada e um ConcurrentHashMap
 * para armazenar as agências criadas.
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import acc.br.bancofinancas.dto.AgenciaResponse;
import acc.br.bancofinancas.dto.CreateAgenciaRequest;

@Service
public class AgenciaService {

    private final AtomicInteger sequence = new AtomicInteger(1);
    private final Map<Integer, AgenciaResponse> agencias = new ConcurrentHashMap<>();

    public AgenciaResponse createAgencia(CreateAgenciaRequest request) {
        Integer newId = sequence.getAndIncrement();

        AgenciaResponse response = new AgenciaResponse();
        response.setIdAgency(newId);
        response.setName(request.getName());
        response.setAddress(request.getAddress());
        response.setPhone(request.getPhone());
        response.setIdCustomer(request.getIdCustomer());

        agencias.put(newId, response);
        return response;

    }
    /*
     * Ajuste conforme seu request atual:
     * se o request tiver idCustomer, use setIdCustomer(request.getIdCustomer())
     * se tiver idAgencia no request, revise depois porque não é o ideal para
     * "criar"
     * response.setIdCustomer(request.getIdCustomer());
     */
}
