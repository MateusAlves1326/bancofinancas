package acc.br.bancofinancas.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.AgenteResponse;
import acc.br.bancofinancas.dto.AtualizarBloqueioContaRequest;
import acc.br.bancofinancas.dto.ContaCorrenteResponse;
import acc.br.bancofinancas.dto.CreateAgenteRequest;
import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.dto.CreditoManualRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoResponse;
import acc.br.bancofinancas.dto.SolicitacaoReversaoResponse;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.model.StatusSolicitacaoReversao;
import acc.br.bancofinancas.service.AgenteService;
import acc.br.bancofinancas.service.ContaCorrenteService;
import acc.br.bancofinancas.service.OperacaoService;

@ExtendWith(MockitoExtension.class)
class AgenteControllerTest {

    @Mock
    private AgenteService agenteService;
    @Mock
    private ContaCorrenteService contaCorrenteService;
    @Mock
    private OperacaoService operacaoService;
    @InjectMocks
    private AgenteController agenteController;

    @Test
    void deveCriarAgenteEConta() {
        CreateAgenteRequest agenteRequest = new CreateAgenteRequest();
        CreateContaCorrenteRequest contaRequest = new CreateContaCorrenteRequest();
        when(agenteService.criarAgente(agenteRequest)).thenReturn(new AgenteResponse());
        when(contaCorrenteService.createContaCorrente(contaRequest)).thenReturn(new ContaCorrenteResponse());

        assertEquals(201, agenteController.criar(agenteRequest).getStatusCode().value());
        assertEquals(201, agenteController.criarConta(contaRequest).getStatusCode().value());
    }

    @Test
    void deveListarEDecidirReversoes() {
        when(operacaoService.listarReversoesPendentesDaAgencia()).thenReturn(List.of(new SolicitacaoReversaoResponse()));
        DecisaoReversaoRequest request = new DecisaoReversaoRequest();
        request.setSolicitacaoId(1L);
        request.setClienteId(2L);
        request.setAprovar(false);
        DecisaoReversaoResponse decisao = new DecisaoReversaoResponse();
        decisao.setStatus(StatusSolicitacaoReversao.RECUSADA);
        when(operacaoService.decidirSolicitacaoReversao(1L, 2L, false)).thenReturn(decisao);

        assertEquals(1, agenteController.listarReversoesPendentes().size());
        assertNotNull(agenteController.decidirReversao(request).getStatus());
    }

    @Test
    void deveAtualizarBloqueioEAdicionarSaldo() {
        AtualizarBloqueioContaRequest bloqueio = new AtualizarBloqueioContaRequest();
        bloqueio.setClienteId(2L);
        bloqueio.setBloqueada(true);
        bloqueio.setMotivo("Teste");
        CreditoManualRequest credito = new CreditoManualRequest();
        credito.setAgenciaId(1L);
        credito.setNumeroConta(12345);
        credito.setValor(new BigDecimal("50.00"));
        ContaCorrenteResponse conta = new ContaCorrenteResponse();
        conta.setId(1L);
        when(contaCorrenteService.atualizarBloqueio(1L, 2L, true, "Teste"))
            .thenReturn(conta);
        when(operacaoService.creditarSaldoManual(1L, 12345, credito.getValor())).thenReturn(new Extrato());

        assertEquals(1L, agenteController.atualizarBloqueio(1L, bloqueio).getId());
        assertNotNull(agenteController.adicionarSaldoManual(credito));
    }
}
