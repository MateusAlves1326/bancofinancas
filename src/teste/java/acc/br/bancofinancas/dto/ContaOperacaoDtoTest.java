package acc.br.bancofinancas.dto;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import acc.br.bancofinancas.model.Operacao;

class ContaOperacaoDtoTest {

    @Test
    void contaCorrenteResponseGettersESetters() {
        ContaCorrenteResponse dto = new ContaCorrenteResponse();

        dto.setId(3L);
        dto.setNumero(1234);
        dto.setSaldo(new BigDecimal("100.00"));
        dto.setClienteId(11L);
        dto.setAgenciaId(22L);
        dto.setBloqueada(true);

        assertEquals(3L, dto.getId());
        assertEquals(1234, dto.getNumero());
        assertEquals(new BigDecimal("100.00"), dto.getSaldo());
        assertEquals(11L, dto.getClienteId());
        assertEquals(22L, dto.getAgenciaId());
        assertEquals(true, dto.isBloqueada());
    }

    @Test
    void createContaCorrenteRequestGettersESetters() {
        CreateContaCorrenteRequest dto = new CreateContaCorrenteRequest();

        dto.setClienteId(11L);
        dto.setAgenciaId(22L);
        dto.setNumero(1234);
        dto.setSaldo(new BigDecimal("100.00"));

        assertEquals(11L, dto.getClienteId());
        assertEquals(22L, dto.getAgenciaId());
        assertEquals(1234, dto.getNumero());
        assertEquals(new BigDecimal("100.00"), dto.getSaldo());
    }

    @Test
    void createOperacaoRequestGettersESetters() {
        CreateOperacaoRequest dto = new CreateOperacaoRequest();

        dto.setContaCorrenteId(33L);
        dto.setOperacao(Operacao.DEPOSITO);
        dto.setValorOperacao(new BigDecimal("50.00"));

        assertEquals(33L, dto.getContaCorrenteId());
        assertEquals(Operacao.DEPOSITO, dto.getOperacao());
        assertEquals(new BigDecimal("50.00"), dto.getValorOperacao());
    }
}
