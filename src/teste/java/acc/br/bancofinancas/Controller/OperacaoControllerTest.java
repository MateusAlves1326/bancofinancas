package acc.br.bancofinancas.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.service.OperacaoService;

@ExtendWith(MockitoExtension.class)
class OperacaoControllerTest {

    @Mock
    private OperacaoService operacaoService;

    @InjectMocks
    private OperacaoController operacaoController;

    @Test
    void criarDeveRetornarExtratoDoService() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        Extrato extrato = new Extrato();

        when(operacaoService.criarOperacao(request)).thenReturn(extrato);

        Extrato retorno = operacaoController.criar(request);

        assertEquals(extrato, retorno);
        verify(operacaoService).criarOperacao(request);
    }
}
