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

        BigDecimal saldoAtual = conta.getSaldo();
        BigDecimal valor = request.getValorOperacao();

        if (saldoAtual == null) {
            saldoAtual = BigDecimal.ZERO;
        }

        if (deveDebitar(request.getOperacao())) {
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
        extrato.setOperacao(request.getOperacao());
        extrato.setValorOperacao(valor);
        extrato.setDataHoraMovimento(LocalDateTime.now());

        return extratoRepository.save(extrato);
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

    private boolean deveDebitar(Operacao operacao) {
        return operacao == Operacao.SAQUE
                || operacao == Operacao.PAGAMENTO
                || operacao == Operacao.TRANSFERENCIA;
    }
}