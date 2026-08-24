package acc.br.bancofinancas.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.AtualizarStatusPedidoLojaRequest;
import acc.br.bancofinancas.dto.CreateLojaItemRequest;
import acc.br.bancofinancas.dto.CreatePedidoLojaRequest;
import acc.br.bancofinancas.dto.LojaItemResponse;
import acc.br.bancofinancas.dto.PagarPedidoCodigoRequest;
import acc.br.bancofinancas.dto.PedidoLojaResponse;
import acc.br.bancofinancas.service.LojaService;

@ExtendWith(MockitoExtension.class)
class LojaControllerTest {

    @Mock
    private LojaService lojaService;

    @InjectMocks
    private LojaController lojaController;

    @Test
    void deveListarItens() {
        List<LojaItemResponse> itens = List.of(new LojaItemResponse());
        when(lojaService.listarItensAtivos()).thenReturn(itens);
        assertEquals(itens, lojaController.listarItens());
    }

    @Test
    void deveCriarItemEPedido() {
        CreateLojaItemRequest itemRequest = new CreateLojaItemRequest();
        LojaItemResponse item = new LojaItemResponse();
        when(lojaService.criarItem(itemRequest)).thenReturn(item);
        assertEquals(201, lojaController.criarItem(itemRequest).getStatusCode().value());

        CreatePedidoLojaRequest pedidoRequest = new CreatePedidoLojaRequest();
        PedidoLojaResponse pedido = new PedidoLojaResponse();
        when(lojaService.criarPedido(pedidoRequest)).thenReturn(pedido);
        assertEquals(201, lojaController.criarPedido(pedidoRequest).getStatusCode().value());
    }

    @Test
    void devePagarEListarPedidos() {
        PagarPedidoCodigoRequest pagamento = new PagarPedidoCodigoRequest();
        PedidoLojaResponse pedido = new PedidoLojaResponse();
        when(lojaService.pagarPorCodigo(pagamento)).thenReturn(pedido);
        assertEquals(pedido, lojaController.pagarPedidoPorCodigo(pagamento));
        when(lojaService.listarPedidosClienteLogado()).thenReturn(List.of(pedido));
        assertEquals(1, lojaController.listarMeusPedidos().size());
        when(lojaService.listarTodosPedidos()).thenReturn(List.of(pedido));
        assertEquals(1, lojaController.listarPedidos().size());
    }

    @Test
    void deveAtualizarStatusEReembolsar() {
        AtualizarStatusPedidoLojaRequest request = new AtualizarStatusPedidoLojaRequest();
        PedidoLojaResponse pedido = new PedidoLojaResponse();
        when(lojaService.atualizarStatusPedido(1L, request)).thenReturn(pedido);
        when(lojaService.reembolsarPedido(1L)).thenReturn(pedido);

        assertEquals(pedido, lojaController.atualizarStatusPedido(1L, request));
        assertEquals(pedido, lojaController.reembolsarPedido(1L));
        verify(lojaService).reembolsarPedido(1L);
    }
}
