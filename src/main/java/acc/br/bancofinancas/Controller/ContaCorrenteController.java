package acc.br.bancofinancas.Controller;


import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.service.ContaCorrenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/contas")
@Tag(name = "Contas correntes", description = "Criação e gestão das contas correntes")
public class ContaCorrenteController {
    
    private final ContaCorrenteService contaCorrenteService;

    public ContaCorrenteController(ContaCorrenteService contaCorrenteService) {
        this.contaCorrenteService = contaCorrenteService;
    }

    @Operation(summary = "Abrir conta corrente", description = "Cria uma nova conta corrente vinculada a um cliente e a uma agência específica.")
    @PostMapping
    public ResponseEntity<ContaCorrenteResponse> create(@Valid @RequestBody CreateContaCorrenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contaCorrenteService.createContaCorrente(request));
    }

}
