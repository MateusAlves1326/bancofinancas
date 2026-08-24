package acc.br.bancofinancas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import acc.br.bancofinancas.dto.ClienteResponse;
import acc.br.bancofinancas.dto.CreateClienteComContaRequest;
import acc.br.bancofinancas.dto.CreateClienteRequest;
import acc.br.bancofinancas.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Cadastro e gerenciamento de clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Listar clientes", description = "Retorna os clientes cadastrados no sistema.")
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @Operation(summary = "Cadastrar cliente", description = "Cria um novo cliente no sistema com nome, CPF, telefone e e-mail.")
    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody CreateClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.createCliente(request));
    }

    @Operation(summary = "Cadastrar cliente e conta em uma operação única", description = "Cria o cliente e a conta corrente juntos, garantindo rollback em caso de falha.")
    @PostMapping("/com-conta")
    public ResponseEntity<ClienteResponse> createComConta(@Valid @RequestBody CreateClienteComContaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.createClienteComConta(request));
    }
}