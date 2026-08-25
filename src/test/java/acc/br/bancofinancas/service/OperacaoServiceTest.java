package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoResponse;
import acc.br.bancofinancas.dto.OperacaoResponse;
import acc.br.bancofinancas.dto.SolicitacaoReversaoResponse;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.model.Operacao;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.SolicitacaoReversao;
import acc.br.bancofinancas.model.StatusSolicitacaoReversao;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.ExtratoRepository;
import acc.br.bancofinancas.repository.SolicitacaoReversaoRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;

@ExtendWith(MockitoExtension.class)
class OperacaoServiceTest {

    @Mock
    private ExtratoRepository extratoRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private SolicitacaoReversaoRepository solicitacaoReversaoRepository;

    @InjectMocks
    private OperacaoService operacaoService;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRecusarOperacaoQuandoContaNaoEncontrada() {
        CreateOperacaoRequest request = request(1L, Operacao.DEPOSITO, "10.00");
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.criarOperacao(request));

        assertEquals("Conta corrente não encontrada", exception.getMessage());
    }

    @Test
    void deveDepositarEAtualizarSaldo() {
        ContaCorrente conta = conta(100, false);
        CreateOperacaoRequest request = request(1L, Operacao.DEPOSITO, "50.00");
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Extrato extrato = operacaoService.criarOperacao(request);

        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
        assertEquals(Operacao.DEPOSITO, extrato.getOperacao());
        verify(contaCorrenteRepository).save(conta);
    }

    @Test
    void deveSacarEAtualizarSaldo() {
        ContaCorrente conta = conta(100, false);
        CreateOperacaoRequest request = request(1L, Operacao.SAQUE, "30.00");
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        operacaoService.criarOperacao(request);

        assertEquals(new BigDecimal("70.00"), conta.getSaldo());
    }

    @Test
    void deveRecusarSaqueSemSaldoSuficiente() {
        ContaCorrente conta = conta(100, false);
        CreateOperacaoRequest request = request(1L, Operacao.PAGAMENTO, "150.00");
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.criarOperacao(request));

        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    void deveRecusarOperacaoEmContaBloqueada() {
        ContaCorrente conta = conta(100, true);
        CreateOperacaoRequest request = request(1L, Operacao.DEPOSITO, "50.00");
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.criarOperacao(request));

        assertEquals("Conta corrente bloqueada", exception.getMessage());
    }

    @Test
    void deveConsiderarSaldoNuloComoZero() {
        ContaCorrente conta = conta(null, false);
        CreateOperacaoRequest request = request(1L, Operacao.DEPOSITO, "10.00");
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        operacaoService.criarOperacao(request);

        assertEquals(new BigDecimal("10.00"), conta.getSaldo());
    }

    @Test
    void deveCreditarSaldoManualParaUsuarioDeAgencia() {
        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(1);
        ContaCorrente conta = conta(100, false);
        conta.setIdContaCorrente(1);
        conta.setAgencia(agencia);
        conta.setCliente(cliente);
        conta.setNumero(12345);
        AuthenticatedUser usuario = new AuthenticatedUser("agente", "senha", Role.AGENCIA, null, 2);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
        when(contaCorrenteRepository.findByAgencia_IdAgencyAndNumero(2, 12345)).thenReturn(Optional.of(conta));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Extrato extrato = operacaoService.creditarSaldoManual(2L, 12345, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
        assertEquals(Operacao.CREDITO_MANUAL, extrato.getOperacao());
    }

    @Test
    void deveRecusarCreditoManualParaUsuarioComOutraRole() {
        AuthenticatedUser usuario = new AuthenticatedUser("cliente", "senha", Role.CLIENTE, 1, null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> operacaoService.creditarSaldoManual(2L, 12345, new BigDecimal("50.00")));

        assertEquals("Somente usuários da agência podem adicionar saldo manualmente", exception.getMessage());
    }

    @Test
    void deveRecusarCreditoManualComValorInvalido() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.creditarSaldoManual(2L, 12345, BigDecimal.ZERO));

        assertEquals("O valor do crédito manual deve ser maior que zero", exception.getMessage());
    }

    @Test
    void deveObterExtratoDaConta() {
        ContaCorrente conta = conta(100, false);
        Extrato extrato = new Extrato();
        extrato.setContaCorrente(conta);
        extrato.setOperacao(Operacao.DEPOSITO);
        extrato.setValorOperacao(new BigDecimal("25.00"));
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));
        when(extratoRepository.findByContaCorrente_IdContaCorrenteOrderByDataHoraMovimentoDesc(1))
                .thenReturn(List.of(extrato));

        List<Extrato> retorno = operacaoService.obterExtrato(1L);

        assertEquals(1, retorno.size());
        assertEquals(extrato, retorno.get(0));
    }

    @Test
    void deveTransferirEntreContasDiferentes() {
        ContaCorrente origem = conta(100, false);
        origem.setIdContaCorrente(1);
        ContaCorrente destino = conta(20, false);
        destino.setIdContaCorrente(2);
        CreateOperacaoRequest request = request(1L, Operacao.TRANSFERENCIA, "30.00");
        request.setContaDestinoId(2L);
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(origem));
        when(contaCorrenteRepository.findById(2)).thenReturn(Optional.of(destino));
        when(extratoRepository.save(any(Extrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Extrato extrato = operacaoService.transferir(request);

        assertEquals(new BigDecimal("70.00"), origem.getSaldo());
        assertEquals(new BigDecimal("50.00"), destino.getSaldo());
        assertEquals(Operacao.TRANSFERENCIA, extrato.getOperacao());
    }

    @Test
    void deveRecusarTransferenciaParaAPropriaConta() {
        ContaCorrente conta = conta(100, false);
        conta.setIdContaCorrente(1);
        CreateOperacaoRequest request = request(1L, Operacao.TRANSFERENCIA, "30.00");
        request.setContaDestinoId(1L);
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.transferir(request));

        assertEquals("A conta de origem e destino devem ser diferentes", exception.getMessage());
    }

    @Test
    void deveSolicitarReversaoDeDeposito() {
        ContaCorrente conta = conta(100, false);
        conta.setIdContaCorrente(1);
        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);
        conta.setAgencia(agencia);
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(7);
        cliente.setNome("Maria");
        conta.setCliente(cliente);
        Extrato original = new Extrato();
        original.setIdExtrato(4);
        original.setContaCorrente(conta);
        original.setOperacao(Operacao.DEPOSITO);
        original.setValorOperacao(new BigDecimal("25.00"));
        CreateOperacaoRequest request = request(1L, Operacao.DEPOSITO, "25.00");
        request.setClienteId(7L);
        request.setExtratoOrigemId(4L);
        request.setMotivo("Lançamento incorreto");
        SolicitacaoReversao salva = new SolicitacaoReversao();
        salva.setId(9);
        salva.setContaCorrente(conta);
        salva.setValor(original.getValorOperacao());
        salva.setOperacaoReversa(Operacao.ESTORNO_DEPOSITO);
        salva.setStatus(StatusSolicitacaoReversao.PENDENTE);
        salva.setMotivo(request.getMotivo());
        when(extratoRepository.findById(4)).thenReturn(Optional.of(original));
        when(solicitacaoReversaoRepository.save(any(SolicitacaoReversao.class))).thenReturn(salva);

        SolicitacaoReversaoResponse response = operacaoService.solicitarReversao(request);

        assertEquals(9L, response.getSolicitacaoId());
        assertEquals(StatusSolicitacaoReversao.PENDENTE, response.getStatus());
        assertEquals(Operacao.ESTORNO_DEPOSITO, response.getOperacaoReversa());
    }

    @Test
    void deveRecusarReversaoSemMotivo() {
        CreateOperacaoRequest request = request(1L, Operacao.DEPOSITO, "25.00");
        request.setClienteId(7L);
        request.setExtratoOrigemId(4L);
        request.setMotivo(" ");
        ContaCorrente conta = conta(100, false);
        conta.setIdContaCorrente(1);
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(7);
        conta.setCliente(cliente);
        Extrato original = new Extrato();
        original.setContaCorrente(conta);
        original.setOperacao(Operacao.DEPOSITO);
        when(extratoRepository.findById(4)).thenReturn(Optional.of(original));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.solicitarReversao(request));

        assertEquals("É necessário informar o motivo da reversão", exception.getMessage());
    }

    @Test
    void deveRecusarSolicitacaoPendenteNaAgencia() {
        autenticarAgencia();
        SolicitacaoReversao solicitacao = solicitacaoPendente();
        when(solicitacaoReversaoRepository.findById(9)).thenReturn(Optional.of(solicitacao));

        DecisaoReversaoResponse response = operacaoService.decidirSolicitacaoReversao(9L, 7L, false);

        assertEquals(StatusSolicitacaoReversao.RECUSADA, response.getStatus());
        verify(solicitacaoReversaoRepository).save(solicitacao);
    }

    @Test
    void deveListarOperacoesDaAgenciaAutenticada() {
        autenticarAgencia();
        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");
        ContaCorrente conta = conta(100, false);
        conta.setNumero(123);
        conta.setAgencia(agencia);
        conta.setCliente(cliente);
        Extrato extrato = new Extrato();
        extrato.setContaCorrente(conta);
        extrato.setOperacao(Operacao.DEPOSITO);
        extrato.setValorOperacao(new BigDecimal("10.00"));
        extrato.setDataHoraMovimento(LocalDateTime.now());
        when(extratoRepository.findByContaCorrente_Agencia_IdAgencyOrderByDataHoraMovimentoDesc(2))
                .thenReturn(List.of(extrato));

        List<OperacaoResponse> response = operacaoService.listarOperacoesDaAgencia();

        assertEquals(1, response.size());
        assertEquals("DEPOSITO", response.get(0).getTipo());
        assertEquals(123, response.get(0).getNumeroConta());
    }

    @Test
    void deveRecusarPeriodoComDataFinalAnterior() {
        ContaCorrente conta = conta(100, false);
        when(contaCorrenteRepository.findById(1)).thenReturn(Optional.of(conta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> operacaoService.obterExtratoPorPeriodo(1L,
                        LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1)));

        assertEquals("A data final deve ser maior ou igual à data inicial", exception.getMessage());
    }

    private void autenticarAgencia() {
        AuthenticatedUser usuario = new AuthenticatedUser("agencia", "senha", Role.AGENCIA, null, 2);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private SolicitacaoReversao solicitacaoPendente() {
        ContaCorrente conta = conta(100, false);
        conta.setIdContaCorrente(1);
        Agencia agencia = new Agencia();
        agencia.setIdAgency(2);
        conta.setAgencia(agencia);
        Cliente cliente = new Cliente();
        cliente.setIdCustomer(7);
        conta.setCliente(cliente);
        SolicitacaoReversao solicitacao = new SolicitacaoReversao();
        solicitacao.setId(9);
        solicitacao.setContaCorrente(conta);
        solicitacao.setValor(new BigDecimal("20.00"));
        solicitacao.setOperacaoReversa(Operacao.ESTORNO_DEPOSITO);
        solicitacao.setStatus(StatusSolicitacaoReversao.PENDENTE);
        return solicitacao;
    }

    private CreateOperacaoRequest request(Long contaId, Operacao operacao, String valor) {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(contaId);
        request.setOperacao(operacao);
        request.setValorOperacao(new BigDecimal(valor));
        return request;
    }

    private ContaCorrente conta(Integer saldo, boolean bloqueada) {
        ContaCorrente conta = new ContaCorrente();
        conta.setSaldo(saldo == null ? null : new BigDecimal(saldo.toString()));
        conta.setBloqueada(bloqueada);
        return conta;
    }
}
