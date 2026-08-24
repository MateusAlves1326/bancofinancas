package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import acc.br.bancofinancas.dto.CreateLojaItemRequest;
import acc.br.bancofinancas.dto.CreatePedidoLojaRequest;
import acc.br.bancofinancas.dto.LojaItemResponse;
import acc.br.bancofinancas.dto.PagarPedidoCodigoRequest;
import acc.br.bancofinancas.dto.PedidoLojaResponse;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.LojaItem;
import acc.br.bancofinancas.model.PedidoLoja;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.StatusPedidoLoja;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.LojaItemRepository;
import acc.br.bancofinancas.repository.PedidoLojaRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;

@ExtendWith(MockitoExtension.class)
class LojaServiceTest {

    @Mock
    private LojaItemRepository lojaItemRepository;

    @Mock
    private PedidoLojaRepository pedidoLojaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private OperacaoService operacaoService;

    @InjectMocks
    private LojaService lojaService;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveCriarItemParaUsuarioDaLoja() {
        autenticar("loja", Role.LOJA, null);
        CreateLojaItemRequest request = new CreateLojaItemRequest();
        request.setNome("Café");
        request.setDescricao("Café em pó");
        request.setPreco(new BigDecimal("15.90"));
        request.setEstoque(4);
        LojaItem salvo = item(1, true, 4);
        when(lojaItemRepository.save(any(LojaItem.class))).thenReturn(salvo);

        LojaItemResponse response = lojaService.criarItem(request);

        assertEquals(1L, response.getId());
        assertEquals("Café", response.getNome());
        assertEquals(new BigDecimal("15.90"), response.getPreco());
        assertEquals(4, response.getEstoque());
        verify(lojaItemRepository).save(any(LojaItem.class));
    }

    @Test
    void deveListarSomenteItensAtivos() {
        LojaItem item = item(1, true, 2);
        when(lojaItemRepository.findByAtivoTrueOrderByNomeAsc()).thenReturn(List.of(item));

        List<LojaItemResponse> response = lojaService.listarItensAtivos();

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2, response.get(0).getEstoque());
    }

    @Test
    void deveCriarPedidoParaClienteAutenticado() {
        autenticar("cliente", Role.CLIENTE, 7);
        LojaItem item = item(2, true, 3);
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(7);
        cliente.setNome("Maria");
        cliente.setEndereco("");
        ContaCorrente conta = new ContaCorrente();
        conta.setIdContaCorrente(8);
        conta.setCliente(cliente);
        CreatePedidoLojaRequest request = new CreatePedidoLojaRequest();
        request.setItemId(2L);
        request.setContaCorrenteId(8L);
        PedidoLoja salvo = pedido(10, item, cliente, conta);

        when(lojaItemRepository.findById(2)).thenReturn(Optional.of(item));
        when(clienteRepository.findById(7)).thenReturn(Optional.of(cliente));
        when(contaCorrenteRepository.findById(8)).thenReturn(Optional.of(conta));
        when(pedidoLojaRepository.save(any(PedidoLoja.class))).thenReturn(salvo);

        PedidoLojaResponse response = lojaService.criarPedido(request);

        assertEquals(10L, response.getId());
        assertEquals(StatusPedidoLoja.AGUARDANDO_PAGAMENTO, response.getStatus());
        assertEquals("Endereço não informado", response.getEnderecoEntrega());
        assertEquals(2L, response.getItemId());
    }

    @Test
    void deveRecusarPedidoDeItemInativo() {
        autenticar("cliente", Role.CLIENTE, 7);
        LojaItem item = item(2, false, 3);
        CreatePedidoLojaRequest request = new CreatePedidoLojaRequest();
        request.setItemId(2L);
        when(lojaItemRepository.findById(2)).thenReturn(Optional.of(item));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> lojaService.criarPedido(request));

        assertEquals("Item indisponível para compra", exception.getMessage());
    }

    @Test
    void deveRecusarPedidoSemEstoque() {
        autenticar("cliente", Role.CLIENTE, 7);
        LojaItem item = item(2, true, 0);
        CreatePedidoLojaRequest request = new CreatePedidoLojaRequest();
        request.setItemId(2L);
        when(lojaItemRepository.findById(2)).thenReturn(Optional.of(item));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> lojaService.criarPedido(request));

        assertEquals("Item sem estoque", exception.getMessage());
    }

    @Test
    void devePagarPedidoEReduzirEstoque() {
        autenticar("cliente", Role.CLIENTE, 7);
        LojaItem item = item(2, true, 1);
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(7);
        ContaCorrente conta = new ContaCorrente();
        conta.setIdContaCorrente(8);
        conta.setCliente(cliente);
        PedidoLoja pedido = pedido(10, item, cliente, conta);
        PagarPedidoCodigoRequest request = new PagarPedidoCodigoRequest();
        request.setCodigoPagamento("PG-TESTE");
        pedido.setCodigoPagamento("PG-TESTE");
        pedido.setStatus(StatusPedidoLoja.AGUARDANDO_PAGAMENTO);
        when(pedidoLojaRepository.findByCodigoPagamento("PG-TESTE")).thenReturn(Optional.of(pedido));
        when(pedidoLojaRepository.save(any(PedidoLoja.class))).thenReturn(pedido);

        PedidoLojaResponse response = lojaService.pagarPorCodigo(request);

        verify(operacaoService).criarOperacao(any());
        verify(lojaItemRepository).save(item);
        assertEquals(0, item.getEstoque());
        assertEquals(false, item.isAtivo());
        assertEquals(StatusPedidoLoja.EM_PREPARO, response.getStatus());
    }

    @Test
    void deveRecusarOperacaoDeLojaSemAutenticacao() {
        CreateLojaItemRequest request = new CreateLojaItemRequest();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> lojaService.criarItem(request));

        assertEquals("Usuário não autenticado", exception.getMessage());
    }

    private void autenticar(String nome, Role role, Integer clienteId) {
        AuthenticatedUser usuario = new AuthenticatedUser(nome, "senha", role, clienteId, null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private LojaItem item(int id, boolean ativo, int estoque) {
        LojaItem item = new LojaItem();
        item.setId(id);
        item.setNome("Café");
        item.setDescricao("Café em pó");
        item.setPreco(new BigDecimal("15.90"));
        item.setEstoque(estoque);
        item.setAtivo(ativo);
        return item;
    }

    private PedidoLoja pedido(int id, LojaItem item, Cliente cliente, ContaCorrente conta) {
        PedidoLoja pedido = new PedidoLoja();
        pedido.setId(id);
        pedido.setItem(item);
        pedido.setCliente(cliente);
        pedido.setContaCorrente(conta);
        pedido.setValor(item.getPreco());
        pedido.setEnderecoEntrega("Endereço não informado");
        pedido.setCodigoPagamento("PG-TESTE");
        pedido.setStatus(StatusPedidoLoja.AGUARDANDO_PAGAMENTO);
        return pedido;
    }
}
