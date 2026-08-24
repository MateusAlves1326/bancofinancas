package acc.br.bancofinancas.Controller;

import acc.br.bancofinancas.dto.AtualizarStatusPedidoLojaRequest;
import acc.br.bancofinancas.dto.CreateLojaItemRequest;
import acc.br.bancofinancas.dto.CreatePedidoLojaRequest;
import acc.br.bancofinancas.dto.LojaItemResponse;
import acc.br.bancofinancas.dto.PagarPedidoCodigoRequest;
import acc.br.bancofinancas.dto.PedidoLojaResponse;
import acc.br.bancofinancas.service.LojaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loja")
@SecurityRequirement(name = "bearerAuth")
public class LojaController {

    private final LojaService lojaService;

    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping("/itens")
    public List<LojaItemResponse> listarItens() {
        return lojaService.listarItensAtivos();
    }

    @PostMapping("/itens")
    public ResponseEntity<LojaItemResponse> criarItem(@Valid @RequestBody CreateLojaItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lojaService.criarItem(request));
    }

    @PostMapping("/pedidos")
    public ResponseEntity<PedidoLojaResponse> criarPedido(@Valid @RequestBody CreatePedidoLojaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lojaService.criarPedido(request));
    }

    @PostMapping("/pedidos/pagar")
    public PedidoLojaResponse pagarPedidoPorCodigo(@Valid @RequestBody PagarPedidoCodigoRequest request) {
        return lojaService.pagarPorCodigo(request);
    }

    @GetMapping("/pedidos/me")
    public List<PedidoLojaResponse> listarMeusPedidos() {
        return lojaService.listarPedidosClienteLogado();
    }

    @GetMapping("/pedidos")
    public List<PedidoLojaResponse> listarPedidos() {
        return lojaService.listarTodosPedidos();
    }

    @PatchMapping("/pedidos/{pedidoId}/status")
    public PedidoLojaResponse atualizarStatusPedido(
            @PathVariable Long pedidoId,
            @Valid @RequestBody AtualizarStatusPedidoLojaRequest request) {
        return lojaService.atualizarStatusPedido(pedidoId, request);
    }

    @PostMapping("/pedidos/{pedidoId}/reembolso")
    public PedidoLojaResponse reembolsarPedido(@PathVariable Long pedidoId) {
        return lojaService.reembolsarPedido(pedidoId);
    }
}