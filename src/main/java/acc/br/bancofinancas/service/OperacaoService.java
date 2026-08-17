package acc.br.bancofinancas.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.model.Operacao;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.ExtratoRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperacaoService {
    private final ExtratoRepository extratoRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    
    public OperacaoService(
        ExtratoRepository extratoRepository, 
        ContaCorrenteRepository contaCorrenteRepository) {
    this.extratoRepository = extratoRepository;
    this.contaCorrenteRepository = contaCorrenteRepository;
    }
    
    @Transactional
    public Extrato criarOperacao(CreateOperacaoRequest request) {
        Integer contaId = request.getContaCorrenteId().intValue();

        ContaCorrente conta = contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta corrente não encontrada"));

        if (request.getOperacao() == Operacao.TRANSFERENCIA && request.getContaDestinoId() != null) {
            return transferir(request);
        }

        if (request.getOperacao() == Operacao.ESTORNO_DEPOSITO
                || request.getOperacao() == Operacao.ESTORNO_PAGAMENTO
                || request.getOperacao() == Operacao.ESTORNO_COMPRA
                || request.getOperacao() == Operacao.ESTORNO_SAQUE
                || request.getOperacao() == Operacao.ESTORNO_TRANSF) {
            return solicitarReversao(request);
        }

        return processarMovimentacaoConta(conta, request.getValorOperacao(), request.getOperacao(), null);
    }

    @Transactional
    public Extrato sacar(Long contaCorrenteId, BigDecimal valor) {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(contaCorrenteId);
        request.setOperacao(Operacao.SAQUE);
        request.setValorOperacao(valor);
        return criarOperacao(request);
    }

    @Transactional
    public Extrato depositar(Long contaCorrenteId, BigDecimal valor) {
        CreateOperacaoRequest request = new CreateOperacaoRequest();
        request.setContaCorrenteId(contaCorrenteId);
        request.setOperacao(Operacao.DEPOSITO);
        request.setValorOperacao(valor);
        return criarOperacao(request);
    }

    @Transactional
    public Extrato transferir(CreateOperacaoRequest request) {
        ContaCorrente origem = contaCorrenteRepository.findById(request.getContaCorrenteId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente de origem não encontrada"));

        ContaCorrente destino = contaCorrenteRepository.findById(request.getContaDestinoId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente de destino não encontrada"));

        if (origem.getIdContaCorrente() == destino.getIdContaCorrente()) {
            throw new IllegalArgumentException("A conta de origem e destino devem ser diferentes");
        }

        processarMovimentacaoConta(origem, request.getValorOperacao(), Operacao.TRANSFERENCIA, null);
        processarMovimentacaoConta(destino, request.getValorOperacao(), Operacao.DEPOSITO, null);

        Extrato extratoOrigem = new Extrato();
        extratoOrigem.setContaCorrente(origem);
        extratoOrigem.setOperacao(Operacao.TRANSFERENCIA);
        extratoOrigem.setValorOperacao(request.getValorOperacao());
        extratoOrigem.setDataHoraMovimento(LocalDateTime.now());

        return extratoRepository.save(extratoOrigem);
    }

    @Transactional
    public Extrato solicitarReversao(CreateOperacaoRequest request) {
        if (request.getExtratoOrigemId() == null) {
            throw new IllegalArgumentException("É necessário informar o extrato original para solicitar reversão");
        }

        Extrato extratoOriginal = extratoRepository.findById(request.getExtratoOrigemId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Extrato original não encontrado"));

        ContaCorrente conta = extratoOriginal.getContaCorrente();

        if (conta.getIdContaCorrente() != request.getContaCorrenteId().intValue()) {
            throw new IllegalArgumentException("O extrato não pertence à conta informada");
        }

        BigDecimal valor = extratoOriginal.getValorOperacao();
        Operacao operacaoReversa = definirOperacaoReversa(extratoOriginal.getOperacao());

        if (operacaoReversa == null) {
            throw new IllegalArgumentException("Essa operação não pode ser revertida");
        }

        processarMovimentacaoConta(conta, valor, operacaoReversa, null);

        Extrato reversao = new Extrato();
        reversao.setContaCorrente(conta);
        reversao.setOperacao(operacaoReversa);
        reversao.setValorOperacao(valor);
        reversao.setDataHoraMovimento(LocalDateTime.now());

        return extratoRepository.save(reversao);
    }

    public List<Extrato> obterExtrato(Long contaCorrenteId) {
        Integer contaId = contaCorrenteId.intValue();

        contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        return extratoRepository.findByContaCorrente_IdContaCorrenteOrderByDataHoraMovimentoDesc(contaId);
    }

    public List<Extrato> obterExtratoPorPeriodo(Long contaCorrenteId, LocalDate dataInicial, LocalDate dataFinal) {
        Integer contaId = contaCorrenteId.intValue();

        contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        if (dataInicial == null || dataFinal == null) {
            return obterExtrato(contaCorrenteId);
        }

        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("A data final deve ser maior ou igual à data inicial");
        }

        LocalDateTime inicio = dataInicial.atStartOfDay();
        LocalDateTime fim = dataFinal.plusDays(1).atStartOfDay().minusNanos(1);

        return extratoRepository.findByContaCorrente_IdContaCorrenteAndDataHoraMovimentoBetweenOrderByDataHoraMovimentoDesc(
                contaId, inicio, fim);
    }

    private Extrato processarMovimentacaoConta(ContaCorrente conta, BigDecimal valor, Operacao operacao, ContaCorrente contaDestino) {
        BigDecimal saldoAtual = conta.getSaldo() == null ? BigDecimal.ZERO : conta.getSaldo();

        if (deveDebitar(operacao)) {
            if (saldoAtual.compareTo(valor) < 0) {
                throw new IllegalArgumentException("Saldo insuficiente");
            }
            conta.setSaldo(saldoAtual.subtract(valor));
        } else {
            conta.setSaldo(saldoAtual.add(valor));
        }

        contaCorrenteRepository.save(conta);

        Extrato extrato = new Extrato();
        extrato.setContaCorrente(conta);
        extrato.setOperacao(operacao);
        extrato.setValorOperacao(valor);
        extrato.setDataHoraMovimento(LocalDateTime.now());

        if (contaDestino != null && contaDestino.getIdContaCorrente() != conta.getIdContaCorrente()) {
            extrato.setContaCorrente(conta);
        }

        return extratoRepository.save(extrato);
    }

    private boolean deveDebitar(Operacao operacao) {
        return operacao == Operacao.SAQUE
                || operacao == Operacao.PAGAMENTO
                || operacao == Operacao.TRANSFERENCIA
                || operacao == Operacao.COMPRA
                || operacao == Operacao.ESTORNO_DEPOSITO;
    }

    private Operacao definirOperacaoReversa(Operacao operacaoOriginal) {
        if (operacaoOriginal == Operacao.DEPOSITO) {
            return Operacao.ESTORNO_DEPOSITO;
        }
        if (operacaoOriginal == Operacao.PAGAMENTO) {
            return Operacao.ESTORNO_PAGAMENTO;
        }
        if (operacaoOriginal == Operacao.COMPRA) {
            return Operacao.ESTORNO_COMPRA;
        }
        if (operacaoOriginal == Operacao.SAQUE) {
            return Operacao.ESTORNO_SAQUE;
        }
        if (operacaoOriginal == Operacao.TRANSFERENCIA) {
            return Operacao.ESTORNO_TRANSF;
        }
        return null;
    }
}