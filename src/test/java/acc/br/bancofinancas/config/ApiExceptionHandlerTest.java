package acc.br.bancofinancas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void deveRetornarBadRequestComMensagemDaExcecao() {
        ResponseEntity<Map<String, String>> response = handler
                .handleIllegalArgument(new IllegalArgumentException("CPF inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CPF inválido", response.getBody().get("message"));
    }

    @Test
    void deveUsarMensagemPadraoQuandoExcecaoNaoTemMensagem() {
        ResponseEntity<Map<String, String>> response = handler
                .handleIllegalArgument(new IllegalArgumentException());

        assertEquals("Dados inválidos para a solicitação.", response.getBody().get("message"));
    }
}
