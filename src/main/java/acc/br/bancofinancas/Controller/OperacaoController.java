package acc.br.bancofinancas.Controller;

import acc.br.bancofinancas.service.OperacaoService;
import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoResponse;
import acc.br.bancofinancas.dto.SolicitacaoReversaoResponse;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.model.Operacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/operacoes")
@Tag(name = "Operações", description = "Movimentações financeiras da conta corrente")
@SecurityRequirement(name = "bearerAuth")
public class OperacaoController {
    private final OperacaoService operacaoService;

    public OperacaoController(OperacaoService operacaoService) {
        this.operacaoService = operacaoService;
    }

    @Operation(summary = "Registrar operação", description = "Cria uma movimentação financeira na conta corrente, como depósito, saque, pagamento, compra ou transferência.")
    @PostMapping
    public Extrato criar(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.criarOperacao(request);
    }

    @Operation(summary = "Sacar valor da conta", description = "Efetua um saque do saldo da conta corrente.")
    @PostMapping("/saque")
    public Extrato sacar(
            @RequestParam Long contaCorrenteId,
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) Long clienteId) {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(contaCorrenteId);
        request.setValorOperacao(valor);
        request.setClienteId(clienteId);
        request.setOperacao(Operacao.SAQUE);
        return operacaoService.criarOperacao(request);
    }

    @Operation(summary = "Depositar valor na conta", description = "Efetua um depósito na conta corrente.")
    @PostMapping("/deposito")
    public Extrato depositar(
            @RequestParam Long contaCorrenteId,
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) Long clienteId) {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(contaCorrenteId);
        request.setValorOperacao(valor);
        request.setClienteId(clienteId);
        request.setOperacao(Operacao.DEPOSITO);
        return operacaoService.criarOperacao(request);
    }

    @Operation(summary = "Transferir valor entre contas", description = "Realiza transferência de um valor da conta de origem para a conta de destino.")
    @PostMapping("/transferencia")
    public Extrato transferir(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.transferir(request);
    }

    @Operation(summary = "Solicitar reversão de operação", description = "Cliente solicita reversão e a solicitação fica pendente para decisão da agência.")
    @PostMapping("/reverter/solicitar")
    public SolicitacaoReversaoResponse solicitarReversao(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.solicitarReversao(request);
    }

    @PostMapping("/reverter")
    public SolicitacaoReversaoResponse solicitarReversaoLegado(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.solicitarReversao(request);
    }

    @Operation(summary = "Decidir solicitação de reversão", description = "A agência aprova ou recusa a solicitação do cliente informando os dados do cliente.")
    @PostMapping("/reverter/decidir")
    public DecisaoReversaoResponse decidirReversao(@Valid @RequestBody DecisaoReversaoRequest request) {
        return operacaoService.decidirSolicitacaoReversao(
                request.getSolicitacaoId(),
                request.getClienteId(),
                request.getAprovar());
    }

    @Operation(summary = "Consultar extrato por período", description = "Retorna as movimentações da conta corrente, com opção de filtrar por data inicial e data final.")
    @GetMapping("/{contaCorrenteId}")
    public List<Extrato> obterExtrato(
            @PathVariable Long contaCorrenteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(required = false) Long clienteId) {
        if (dataInicial != null || dataFinal != null) {
            return operacaoService.obterExtratoPorPeriodo(contaCorrenteId, dataInicial, dataFinal, clienteId);
        }
        return operacaoService.obterExtrato(contaCorrenteId, clienteId);
    }
}
