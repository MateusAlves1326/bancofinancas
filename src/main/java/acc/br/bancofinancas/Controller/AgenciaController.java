package acc.br.bancofinancas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import acc.br.bancofinancas.dto.AgenciaResponse;
import acc.br.bancofinancas.dto.CreateAgenciaRequest;
import acc.br.bancofinancas.service.AgenciaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agencias")
public class AgenciaController {

    private final AgenciaService agenciaService;

    public AgenciaController(AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    @PostMapping
    public ResponseEntity<AgenciaResponse> create(@Valid @RequestBody CreateAgenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenciaService.createAgencia(request));
    }
}
