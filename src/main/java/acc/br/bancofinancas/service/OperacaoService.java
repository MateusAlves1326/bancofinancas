package acc.br.bancofinancas.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private boolean deveDebitar(Operacao operacao) {
        return operacao == Operacao.SAQUE
                || operacao == Operacao.PAGAMENTO
                || operacao == Operacao.TRANSFERENCIA;
    }
}