package acc.br.bancofinancas.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoResponse;
import acc.br.bancofinancas.dto.OperacaoResponse;
import acc.br.bancofinancas.dto.SolicitacaoReversaoResponse;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.service.OperacaoService;

@ExtendWith(MockitoExtension.class)
class OperacaoControllerTest {

    @Mock
    private OperacaoService operacaoService;

    @InjectMocks
    private OperacaoController operacaoController;

    @Test
    void deveListarOperacoes() {
        List<OperacaoResponse> esperadas = List.of(new OperacaoResponse());
        when(operacaoService.listarOperacoesDaAgencia()).thenReturn(esperadas);

        assertEquals(esperadas, operacaoController.listar());
    }

    @Test
    void deveCriarOperacao() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        Extrato esperado = new Extrato();
        when(operacaoService.criarOperacao(request)).thenReturn(esperado);

        assertEquals(esperado, operacaoController.criar(request));
        verify(operacaoService).criarOperacao(request);
    }

    @Test
    void deveCriarSaqueComDadosRecebidos() {
        Extrato esperado = new Extrato();
        when(operacaoService.criarOperacao(any(CreateOperacaoRequest.class))).thenReturn(esperado);

        assertEquals(esperado, operacaoController.sacar(1L, new BigDecimal("10.00"), 7L));
        verify(operacaoService).criarOperacao(any(CreateOperacaoRequest.class));
    }

    @Test
    void deveCriarDepositoComDadosRecebidos() {
        Extrato esperado = new Extrato();
        when(operacaoService.criarOperacao(any(CreateOperacaoRequest.class))).thenReturn(esperado);

        assertEquals(esperado, operacaoController.depositar(1L, new BigDecimal("10.00"), null));
        verify(operacaoService).criarOperacao(any(CreateOperacaoRequest.class));
    }

    @Test
    void deveDelegarTransferenciaEReversao() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        SolicitacaoReversaoResponse reversao = new SolicitacaoReversaoResponse();
        when(operacaoService.solicitarReversao(request)).thenReturn(reversao);

        assertEquals(reversao, operacaoController.solicitarReversao(request));
        assertEquals(reversao, operacaoController.solicitarReversaoLegado(request));
        verify(operacaoService, org.mockito.Mockito.times(2)).solicitarReversao(request);
    }

    @Test
    void deveDecidirReversao() {
        DecisaoReversaoRequest request = new DecisaoReversaoRequest();
        request.setSolicitacaoId(4L);
        request.setClienteId(7L);
        request.setAprovar(true);
        DecisaoReversaoResponse esperado = new DecisaoReversaoResponse();
        when(operacaoService.decidirSolicitacaoReversao(4L, 7L, true)).thenReturn(esperado);

        assertEquals(esperado, operacaoController.decidirReversao(request));
    }

    @Test
    void deveConsultarExtratoComEManterSemPeriodo() {
        Extrato extrato = new Extrato();
        when(operacaoService.obterExtrato(1L, 7L)).thenReturn(List.of(extrato));
        when(operacaoService.obterExtratoPorPeriodo(1L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), 7L))
                .thenReturn(List.of(extrato));

        assertEquals(1, operacaoController.obterExtrato(1L, null, null, 7L).size());
        assertEquals(1, operacaoController.obterExtrato(1L,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), 7L).size());
    }
}
