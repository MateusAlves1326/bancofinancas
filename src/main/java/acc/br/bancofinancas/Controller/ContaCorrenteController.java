package acc.br.bancofinancas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.service.ContaCorrenteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    public ContaCorrenteController(ContaCorrenteService contaCorrenteService) {
        this.contaCorrenteService = contaCorrenteService;
    }

    @PostMapping("/cadastrocontacorrente")
    public ResponseEntity<ContaCorrenteResponse> createContaCorrente(
            @Valid @RequestBody CreateContaCorrenteRequest request) {
        ContaCorrenteResponse response = contaCorrenteService.createContaCorrente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public void create(CreateContaCorrenteRequest request) {
        contaCorrenteService.createContaCorrente(request);
    }
}
