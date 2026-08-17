package acc.br.bancofinancas.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class OperacaoEnumTest {

    @Test
    void valueOfDeveRetornarValorCorreto() {
        assertEquals(Operacao.SAQUE, Operacao.valueOf("SAQUE"));
    }

    @Test
    void valuesDeveConterTodosOsItensEsperados() {
        Operacao[] valores = Operacao.values();

        assertEquals(7, valores.length);
        assertTrue(Arrays.asList(valores).contains(Operacao.DEPOSITO));
        assertTrue(Arrays.asList(valores).contains(Operacao.TRANSFERENCIA));
        assertTrue(Arrays.asList(valores).contains(Operacao.PAGAMENTO));
        assertTrue(Arrays.asList(valores).contains(Operacao.ESTORNO_SAQUE));
        assertTrue(Arrays.asList(valores).contains(Operacao.ESTORNO_PAGAMENTO));
        assertTrue(Arrays.asList(valores).contains(Operacao.ESTORNO_TRANSF));
    }
}
