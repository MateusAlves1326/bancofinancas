package acc.br.bancofinancas;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class BancofinancasApplicationMainTest {

    @Test
    void mainDeveIniciarSemLancarExcecao() {
        assertDoesNotThrow(() -> BancofinancasApplication.main(new String[] {"--spring.main.web-application-type=none"}));
    }
}
