package acc.br.bancofinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.model.Operacao;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.ExtratoRepository;

@ExtendWith(MockitoExtension.class)
class OperacaoServiceTest {

    @Mock
    private ExtratoRepository extratoRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @InjectMocks
    private OperacaoService operacaoService;

    @Test
    void criarOperacaoDeveLancarExcecaoQuandoContaNaoEncontrada() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(1L);

        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.criarOperacao(request));

        assertEquals("Conta corrente não encontrada", ex.getMessage());
    }

    @Test
    void criarOperacaoDeveCreditarQuandoOperacaoNaoDebita() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(1L);
        request.setOperacao(Operacao.DEPOSITO);
        request.setValorOperacao(new BigDecimal("50.00"));

        ContaCorrente conta = new ContaCorrente();
        conta.setSaldo(new BigDecimal("100.00"));

        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Extrato extrato = operacaoService.criarOperacao(request);

        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
        assertEquals(Operacao.DEPOSITO, extrato.getOperacao());
        assertEquals(new BigDecimal("50.00"), extrato.getValorOperacao());
        verify(contaCorrenteRepository).save(conta);
    }

    @Test
    void criarOperacaoDeveDebitarQuandoOperacaoDebita() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(1L);
        request.setOperacao(Operacao.SAQUE);
        request.setValorOperacao(new BigDecimal("30.00"));

        ContaCorrente conta = new ContaCorrente();
        conta.setSaldo(new BigDecimal("100.00"));

        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        operacaoService.criarOperacao(request);

        assertEquals(new BigDecimal("70.00"), conta.getSaldo());
    }

    @Test
    void criarOperacaoDeveLancarExcecaoQuandoSaldoInsuficiente() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(1L);
        request.setOperacao(Operacao.PAGAMENTO);
        request.setValorOperacao(new BigDecimal("150.00"));

        ContaCorrente conta = new ContaCorrente();
        conta.setSaldo(new BigDecimal("100.00"));

        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.criarOperacao(request));

        assertEquals("Saldo insuficiente", ex.getMessage());
    }

    @Test
    void criarOperacaoDeveAssumirSaldoZeroQuandoNulo() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(1L);
        request.setOperacao(Operacao.DEPOSITO);
        request.setValorOperacao(new BigDecimal("10.00"));

        ContaCorrente conta = new ContaCorrente();
        conta.setSaldo(null);

        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        operacaoService.criarOperacao(request);

        assertEquals(new BigDecimal("10.00"), conta.getSaldo());
    }

    @Test
    void criarOperacaoDevePreencherExtratoComContaOperacaoEValor() {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(1L);
        request.setOperacao(Operacao.TRANSFERENCIA);
        request.setValorOperacao(new BigDecimal("20.00"));

        ContaCorrente conta = new ContaCorrente();
        conta.setSaldo(new BigDecimal("100.00"));

        ArgumentCaptor<Extrato> captor = ArgumentCaptor.forClass(Extrato.class);

        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        operacaoService.criarOperacao(request);

        verify(extratoRepository).save(captor.capture());
        Extrato salvo = captor.getValue();
        assertEquals(conta, salvo.getContaCorrente());
        assertEquals(Operacao.TRANSFERENCIA, salvo.getOperacao());
        assertEquals(new BigDecimal("20.00"), salvo.getValorOperacao());
    }

    @Test
    void obterExtratoDeveRetornarMovimentacoesDaConta() {
        ContaCorrente conta = new ContaCorrente();
        conta.setIdContaCorrente(1);

        Extrato extrato = new Extrato();
        extrato.setContaCorrente(conta);
        extrato.setOperacao(Operacao.DEPOSITO);
        extrato.setValorOperacao(new BigDecimal("25.00"));

        when(extratoRepository.findByContaCorrente_IdContaCorrenteOrderByDataHoraMovimentoDesc(1)).thenReturn(List.of(extrato));

        List<Extrato> retorno = operacaoService.obterExtrato(1L);

        assertEquals(1, retorno.size());
        assertEquals(extrato, retorno.get(0));
    }
}
