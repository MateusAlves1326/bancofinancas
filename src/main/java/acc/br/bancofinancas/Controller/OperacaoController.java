package acc.br.bancofinancas.Controller;

import acc.br.bancofinancas.service.OperacaoService;
import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.model.Extrato;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/operacoes")
public class OperacaoController {
    private final OperacaoService operacaoService;

    public OperacaoController(OperacaoService operacaoService) {
        this.operacaoService = operacaoService;
    }

    @PostMapping
    public Extrato criar(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.criarOperacao(request);
    }

    @GetMapping("/{contaCorrenteId}")
    public List<Extrato> obterExtrato(@PathVariable Long contaCorrenteId) {
        return operacaoService.obterExtrato(contaCorrenteId);
    }
}
