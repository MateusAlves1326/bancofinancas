package acc.br.bancofinancas.service;

import acc.br.bancofinancas.dto.AtualizarStatusPedidoLojaRequest;
import acc.br.bancofinancas.dto.CreateLojaItemRequest;
import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.dto.CreatePedidoLojaRequest;
import acc.br.bancofinancas.dto.LojaItemResponse;
import acc.br.bancofinancas.dto.PagarPedidoCodigoRequest;
import acc.br.bancofinancas.dto.PedidoLojaResponse;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.LojaItem;
import acc.br.bancofinancas.model.Operacao;
import acc.br.bancofinancas.model.PedidoLoja;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.StatusPedidoLoja;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.LojaItemRepository;
import acc.br.bancofinancas.repository.PedidoLojaRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LojaService {

    private final LojaItemRepository lojaItemRepository;
    private final PedidoLojaRepository pedidoLojaRepository;
    private final ClienteRepository clienteRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final OperacaoService operacaoService;

    public LojaService(
            LojaItemRepository lojaItemRepository,
            PedidoLojaRepository pedidoLojaRepository,
            ClienteRepository clienteRepository,
            ContaCorrenteRepository contaCorrenteRepository,
            OperacaoService operacaoService) {
        this.lojaItemRepository = lojaItemRepository;
        this.pedidoLojaRepository = pedidoLojaRepository;
        this.clienteRepository = clienteRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.operacaoService = operacaoService;
    }

    public List<LojaItemResponse> listarItensAtivos() {
        return lojaItemRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .map(this::toItemResponse)
                .toList();
    }

    @Transactional
    public LojaItemResponse criarItem(CreateLojaItemRequest request) {
        garantirRole(Role.LOJA, Role.ADMIN);

        LojaItem item = new LojaItem();
        item.setNome(request.getNome());
        item.setDescricao(request.getDescricao());
        item.setPreco(request.getPreco());
        item.setEstoque(request.getEstoque());
        item.setAtivo(true);

        return toItemResponse(lojaItemRepository.save(item));
    }

    @Transactional
    public PedidoLojaResponse criarPedido(CreatePedidoLojaRequest request) {
        AuthenticatedUser user = garantirRole(Role.CLIENTE);

        LojaItem item = lojaItemRepository.findById(request.getItemId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));
        if (!item.isAtivo()) {
            throw new IllegalArgumentException("Item indisponível para compra");
        }
        if (item.getEstoque() == null || item.getEstoque() <= 0) {
            throw new IllegalArgumentException("Item sem estoque");
        }

        Cliente cliente = clienteRepository.findById(user.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        ContaCorrente contaCorrente = contaCorrenteRepository.findById(request.getContaCorrenteId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        if (contaCorrente.getCliente().getIdCustomer() != cliente.getIdCustomer()) {
            throw new IllegalArgumentException("A conta informada não pertence ao cliente logado");
        }

        PedidoLoja pedido = new PedidoLoja();
        pedido.setItem(item);
        pedido.setCliente(cliente);
        pedido.setContaCorrente(contaCorrente);
        pedido.setCodigoPagamento(gerarCodigoPagamento());
        pedido.setValor(item.getPreco());
        pedido.setEnderecoEntrega(cliente.getEndereco() == null || cliente.getEndereco().isBlank()
                ? "Endereço não informado"
                : cliente.getEndereco());
        pedido.setStatus(StatusPedidoLoja.AGUARDANDO_PAGAMENTO);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());

        return toPedidoResponse(pedidoLojaRepository.save(pedido));
    }

    @Transactional
    public PedidoLojaResponse pagarPorCodigo(PagarPedidoCodigoRequest request) {
        AuthenticatedUser user = garantirRole(Role.CLIENTE);

        PedidoLoja pedido = pedidoLojaRepository.findByCodigoPagamento(request.getCodigoPagamento())
                .orElseThrow(() -> new IllegalArgumentException("Código de pagamento não encontrado"));

        if (pedido.getCliente().getIdCustomer() != user.getClienteId()) {
            throw new IllegalArgumentException("Pedido não pertence ao cliente logado");
        }

        if (pedido.getStatus() != StatusPedidoLoja.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido já foi pago ou finalizado");
        }

        CreateOperacaoRequest operacaoRequest = new CreateOperacaoRequest();
        operacaoRequest.setContaCorrenteId((long) pedido.getContaCorrente().getIdContaCorrente());
        operacaoRequest.setClienteId((long) pedido.getCliente().getIdCustomer());
        operacaoRequest.setOperacao(Operacao.PAGAMENTO);
        operacaoRequest.setValorOperacao(pedido.getValor());
        operacaoService.criarOperacao(operacaoRequest);

        LojaItem item = pedido.getItem();
        item.setEstoque(item.getEstoque() - 1);
        if (item.getEstoque() <= 0) {
            item.setEstoque(0);
            item.setAtivo(false);
        }
        lojaItemRepository.save(item);

        pedido.setStatus(StatusPedidoLoja.EM_PREPARO);
        pedido.setDataPagamento(LocalDateTime.now());
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());

        return toPedidoResponse(pedidoLojaRepository.save(pedido));
    }

    public List<PedidoLojaResponse> listarPedidosClienteLogado() {
        AuthenticatedUser user = garantirRole(Role.CLIENTE);
        return pedidoLojaRepository.findByCliente_IdCustomerOrderByDataCriacaoDesc(user.getClienteId())
                .stream()
                .map(this::toPedidoResponse)
                .toList();
    }

    public List<PedidoLojaResponse> listarTodosPedidos() {
        garantirRole(Role.LOJA, Role.ADMIN);
        return pedidoLojaRepository.findAll().stream()
                .map(this::toPedidoResponse)
                .toList();
    }

    @Transactional
    public PedidoLojaResponse atualizarStatusPedido(Long pedidoId, AtualizarStatusPedidoLojaRequest request) {
        garantirRole(Role.LOJA, Role.ADMIN);

        PedidoLoja pedido = pedidoLojaRepository.findById(pedidoId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        StatusPedidoLoja novoStatus = request.getStatus();
        if (novoStatus == StatusPedidoLoja.AGUARDANDO_PAGAMENTO || novoStatus == StatusPedidoLoja.REEMBOLSADO) {
            throw new IllegalArgumentException("Status inválido para atualização manual");
        }
        if (pedido.getStatus() == StatusPedidoLoja.REEMBOLSADO) {
            throw new IllegalArgumentException("Pedido já reembolsado não pode mudar de status");
        }
        if (pedido.getStatus() == StatusPedidoLoja.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido ainda não pago");
        }

        pedido.setStatus(novoStatus);
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());
        return toPedidoResponse(pedidoLojaRepository.save(pedido));
    }

    @Transactional
    public PedidoLojaResponse reembolsarPedido(Long pedidoId) {
        garantirRole(Role.LOJA, Role.ADMIN);

        PedidoLoja pedido = pedidoLojaRepository.findById(pedidoId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedidoLoja.REEMBOLSADO) {
            throw new IllegalArgumentException("Pedido já reembolsado");
        }
        if (pedido.getStatus() == StatusPedidoLoja.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido ainda não foi pago");
        }

        CreateOperacaoRequest operacaoRequest = new CreateOperacaoRequest();
        operacaoRequest.setContaCorrenteId((long) pedido.getContaCorrente().getIdContaCorrente());
        operacaoRequest.setClienteId((long) pedido.getCliente().getIdCustomer());
        operacaoRequest.setOperacao(Operacao.DEPOSITO);
        operacaoRequest.setValorOperacao(pedido.getValor());
        operacaoService.criarOperacao(operacaoRequest);

        pedido.setStatus(StatusPedidoLoja.REEMBOLSADO);
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());
        return toPedidoResponse(pedidoLojaRepository.save(pedido));
    }

    private String gerarCodigoPagamento() {
        return "PG-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private LojaItemResponse toItemResponse(LojaItem item) {
        LojaItemResponse response = new LojaItemResponse();
        response.setId((long) item.getId());
        response.setNome(item.getNome());
        response.setDescricao(item.getDescricao());
        response.setPreco(item.getPreco());
        response.setEstoque(item.getEstoque());
        response.setAtivo(item.isAtivo());
        return response;
    }

    private PedidoLojaResponse toPedidoResponse(PedidoLoja pedido) {
        PedidoLojaResponse response = new PedidoLojaResponse();
        response.setId((long) pedido.getId());
        response.setItemId((long) pedido.getItem().getId());
        response.setItemNome(pedido.getItem().getNome());
        response.setClienteId((long) pedido.getCliente().getIdCustomer());
        response.setClienteNome(pedido.getCliente().getNome());
        response.setContaCorrenteId((long) pedido.getContaCorrente().getIdContaCorrente());
        response.setCodigoPagamento(pedido.getCodigoPagamento());
        response.setValor(pedido.getValor());
        response.setEnderecoEntrega(pedido.getEnderecoEntrega());
        response.setStatus(pedido.getStatus());
        response.setDataCriacao(pedido.getDataCriacao());
        response.setDataPagamento(pedido.getDataPagamento());
        response.setDataUltimaAtualizacao(pedido.getDataUltimaAtualizacao());
        return response;
    }

    private AuthenticatedUser garantirRole(Role... rolesPermitidas) {
        AuthenticatedUser user = getAuthenticatedUser();
        if (user == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }

        for (Role rolePermitida : rolesPermitidas) {
            if (user.getRole() == rolePermitida) {
                return user;
            }
        }

        throw new IllegalArgumentException("Usuário sem permissão para esta operação");
    }

    private AuthenticatedUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return null;
        }
        return user;
    }
}