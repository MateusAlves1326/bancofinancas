package acc.br.bancofinancas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import acc.br.bancofinancas.dto.AgenteResponse;
import acc.br.bancofinancas.dto.AtualizarBloqueioContaRequest;
import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateAgenteRequest;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.dto.CreditoManualRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoResponse;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.service.AgenteService;
import acc.br.bancofinancas.service.ContaCorrenteService;
import acc.br.bancofinancas.service.OperacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agentes")
@SecurityRequirement(name = "bearerAuth")
public class AgenteController {

    private final AgenteService agenteService;
    private final ContaCorrenteService contaCorrenteService;
    private final OperacaoService operacaoService;

    public AgenteController(
            AgenteService agenteService,
            ContaCorrenteService contaCorrenteService,
            OperacaoService operacaoService) {
        this.agenteService = agenteService;
        this.contaCorrenteService = contaCorrenteService;
        this.operacaoService = operacaoService;
    }

    @PostMapping
    public ResponseEntity<AgenteResponse> criar(@Valid @RequestBody CreateAgenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenteService.criarAgente(request));
    }

    @PostMapping("/contas")
    public ResponseEntity<ContaCorrenteResponse> criarConta(@Valid @RequestBody CreateContaCorrenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaCorrenteService.createContaCorrente(request));
    }

    @PostMapping("/reversoes/decisoes")
    public DecisaoReversaoResponse decidirReversao(@Valid @RequestBody DecisaoReversaoRequest request) {
        return operacaoService.decidirSolicitacaoReversao(
                request.getSolicitacaoId(), request.getClienteId(), request.getAprovar());
    }

    @PatchMapping("/contas/{contaId}/bloqueio")
    public ContaCorrenteResponse atualizarBloqueio(
            @PathVariable Long contaId,
            @Valid @RequestBody AtualizarBloqueioContaRequest request) {
        return contaCorrenteService.atualizarBloqueio(
            contaId, request.getClienteId(), request.getBloqueada(), request.getMotivo());
    }

    @PostMapping("/contas/{contaId}/saldo")
    public Extrato adicionarSaldoManual(
            @PathVariable Long contaId,
            @Valid @RequestBody CreditoManualRequest request) {
        return operacaoService.creditarSaldoManual(contaId, request.getClienteId(), request.getValor());
    }
}