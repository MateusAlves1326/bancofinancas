package acc.br.bancofinancas.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;

@Service
public class ContaCorrenteService {

    private final AtomicInteger sequence = new AtomicInteger(1);
    private final Map<Integer, ContaCorrenteResponse> contasCorrentes = new ConcurrentHashMap<>();

    public ContaCorrenteResponse createContaCorrente(CreateContaCorrenteRequest request) {
        Integer newId = sequence.getAndIncrement();

        ContaCorrenteResponse response = new ContaCorrenteResponse();
        response.setIdContaCorrente(newId);
        response.setIdAgencia(request.getIdAgencia());
        response.setNumero(request.getNumero());
        response.setSaldo(request.getSaldo());
        response.setIdCliente(request.getIdCliente());

        contasCorrentes.put(newId, response);
        return response;
    }
}
