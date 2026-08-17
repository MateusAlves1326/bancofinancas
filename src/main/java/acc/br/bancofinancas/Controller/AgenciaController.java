package acc.br.bancofinancas.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acc.br.bancofinancas.dto.AgenciaResponse;
import acc.br.bancofinancas.dto.CreateAgenciaRequest;
import acc.br.bancofinancas.service.AgenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agencias")
@Tag(name = "Agências", description = "Cadastro e consulta de agências disponíveis")
public class AgenciaController {

    private final AgenciaService agenciaService;

    public AgenciaController(AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    @Operation(summary = "Cadastrar agência", description = "Cria uma nova agência bancária com dados básicos e identificação do cliente responsável.")
    @PostMapping
    public ResponseEntity<AgenciaResponse> create(@Valid @RequestBody CreateAgenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenciaService.createAgencia(request));
    }

    @Operation(summary = "Listar agências", description = "Lista as agências disponíveis e permite filtrar por nome ou localização para localizar uma unidade mais próxima.")
    @GetMapping
    public List<AgenciaResponse> listar(@RequestParam(required = false) String localizacao) {
        return agenciaService.listarAgencias(localizacao);
    }
}
