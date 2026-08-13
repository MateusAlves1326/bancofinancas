package acc.br.bancofinancas.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ContaExtratoModelTest {

    @Test
    void contaCorrenteGettersESetters() {
        ContaCorrente conta = new ContaCorrente();
        Agencia agencia = new Agencia();
        Cliente cliente = new Cliente();
        List<Extrato> extratos = new ArrayList<>();

        conta.setIdContaCorrente(10);
        conta.setAgencia(agencia);
        conta.setNumero(123);
        conta.setSaldo(new BigDecimal("100.00"));
        conta.setCliente(cliente);
        conta.setExtratos(extratos);

        assertEquals(10, conta.getIdContaCorrente());
        assertEquals(agencia, conta.getAgencia());
        assertEquals(123, conta.getNumero());
        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
        assertEquals(cliente, conta.getCliente());
        assertEquals(extratos, conta.getExtratos());
    }

    @Test
    void extratoGettersESetters() {
        Extrato extrato = new Extrato();
        ContaCorrente conta = new ContaCorrente();
        LocalDateTime agora = LocalDateTime.now();

        extrato.setIdExtrato(1);
        extrato.setDataHoraMovimento(agora);
        extrato.setOperacao(Operacao.PAGAMENTO);
        extrato.setValorOperacao(new BigDecimal("20.00"));
        extrato.setContaCorrente(conta);

        assertEquals(1, extrato.getIdExtrato());
        assertEquals(agora, extrato.getDataHoraMovimento());
        assertEquals(Operacao.PAGAMENTO, extrato.getOperacao());
        assertEquals(new BigDecimal("20.00"), extrato.getValorOperacao());
        assertEquals(conta, extrato.getContaCorrente());
    }
}
