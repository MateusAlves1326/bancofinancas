package acc.br.bancofinancas.Controller;


import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.service.ContaCorrenteService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/contas")
public class ContaCorrenteController {
    
    private final ContaCorrenteService contaCorrenteService;

    public ContaCorrenteController(ContaCorrenteService contaCorrenteService) {
        this.contaCorrenteService = contaCorrenteService;
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateContaCorrenteRequest request) {
        contaCorrenteService.createContaCorrente(request);
    }

    

}
