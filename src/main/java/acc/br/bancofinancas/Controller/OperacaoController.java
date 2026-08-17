package acc.br.bancofinancas.Controller;

import acc.br.bancofinancas.service.OperacaoService;
import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.model.Extrato;
import io.swagger.v3.oas.annotations.Operation;
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
    public Extrato sacar(@RequestParam Long contaCorrenteId, @RequestParam BigDecimal valor) {
        return operacaoService.sacar(contaCorrenteId, valor);
    }

    @Operation(summary = "Depositar valor na conta", description = "Efetua um depósito na conta corrente.")
    @PostMapping("/deposito")
    public Extrato depositar(@RequestParam Long contaCorrenteId, @RequestParam BigDecimal valor) {
        return operacaoService.depositar(contaCorrenteId, valor);
    }

    @Operation(summary = "Transferir valor entre contas", description = "Realiza transferência de um valor da conta de origem para a conta de destino.")
    @PostMapping("/transferencia")
    public Extrato transferir(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.transferir(request);
    }

    @Operation(summary = "Solicitar reversão de operação", description = "Solicita a reversão de uma operação anterior, como compra, depósito ou pagamento.")
    @PostMapping("/reverter")
    public Extrato solicitarReversao(@Valid @RequestBody CreateOperacaoRequest request) {
        return operacaoService.solicitarReversao(request);
    }

    @Operation(summary = "Consultar extrato por período", description = "Retorna as movimentações da conta corrente, com opção de filtrar por data inicial e data final.")
    @GetMapping("/{contaCorrenteId}")
    public List<Extrato> obterExtrato(
            @PathVariable Long contaCorrenteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        if (dataInicial != null || dataFinal != null) {
            return operacaoService.obterExtratoPorPeriodo(contaCorrenteId, dataInicial, dataFinal);
        }
        return operacaoService.obterExtrato(contaCorrenteId);
    }
}
