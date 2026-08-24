package acc.br.bancofinancas.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DtoCoverageTest {

    @Test
    void deveCriarRespostaDeAutenticacaoComToken() {
        AuthResponse response = new AuthResponse();
        response.setToken("token-inicial");
        assertEquals("token-inicial", response.getToken());

        AuthResponse responseComConstrutor = new AuthResponse("token-final");
        assertEquals("token-final", responseComConstrutor.getToken());
    }

    @Test
    void deveLerDadosDeAtualizacaoDeBloqueio() {
        AtualizarBloqueioContaRequest request = new AtualizarBloqueioContaRequest();
        request.setClienteId(7L);
        request.setBloqueada(true);
        request.setMotivo("Fraude suspeita");

        assertEquals(7L, request.getClienteId());
        assertTrue(request.getBloqueada());
        assertEquals("Fraude suspeita", request.getMotivo());
    }

    @Test
    void deveLerDadosDeDecisaoDeReversao() {
        DecisaoReversaoRequest request = new DecisaoReversaoRequest();
        request.setSolicitacaoId(4L);
        request.setClienteId(7L);
        request.setAprovar(false);

        assertEquals(4L, request.getSolicitacaoId());
        assertEquals(7L, request.getClienteId());
        assertEquals(false, request.getAprovar());
    }
}
