package acc.br.bancofinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acc.br.bancofinancas.dto.CreateOperacaoRequest;
import acc.br.bancofinancas.dto.DecisaoReversaoResponse;
import acc.br.bancofinancas.dto.OperacaoResponse;
import acc.br.bancofinancas.dto.SolicitacaoReversaoResponse;
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

@Service
public class OperacaoService {
    private final ExtratoRepository extratoRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final SolicitacaoReversaoRepository solicitacaoReversaoRepository;
    
    public OperacaoService(
        ExtratoRepository extratoRepository,
        ContaCorrenteRepository contaCorrenteRepository,
        SolicitacaoReversaoRepository solicitacaoReversaoRepository) {
        this.extratoRepository = extratoRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.solicitacaoReversaoRepository = solicitacaoReversaoRepository;
    }
    
    @Transactional
    public Extrato criarOperacao(CreateOperacaoRequest request) {
        Integer contaId = request.getContaCorrenteId().intValue();

        ContaCorrente conta = contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta corrente não encontrada"));

        validarPermissaoConta(conta, request.getClienteId());
        validarContaDesbloqueada(conta);

        if (request.getOperacao() == Operacao.TRANSFERENCIA && request.getContaDestinoId() != null) {
            return transferir(request);
        }

        if (request.getOperacao() == Operacao.ESTORNO_DEPOSITO
                || request.getOperacao() == Operacao.ESTORNO_PAGAMENTO
                || request.getOperacao() == Operacao.ESTORNO_COMPRA
                || request.getOperacao() == Operacao.ESTORNO_SAQUE
                || request.getOperacao() == Operacao.ESTORNO_TRANSF) {
            throw new IllegalArgumentException("Use o endpoint de solicitação de reversão para operações de estorno");
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
    public Extrato creditarSaldoManual(Long agenciaId, Integer numeroConta, BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor do crédito manual deve ser maior que zero");
        }

        AuthenticatedUser user = getAuthenticatedUser();
        if (user == null || user.getRole() != Role.AGENCIA) {
            throw new IllegalArgumentException("Somente usuários da agência podem adicionar saldo manualmente");
        }

        ContaCorrente conta = contaCorrenteRepository.findByAgencia_IdAgencyAndNumero(
                agenciaId.intValue(), numeroConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        validarPermissaoConta(conta, (long) conta.getCliente().getIdCustomer());
        return processarMovimentacaoConta(conta, valor, Operacao.CREDITO_MANUAL, null);
    }

    @Transactional
    public Extrato transferir(CreateOperacaoRequest request) {
        ContaCorrente origem = contaCorrenteRepository.findById(request.getContaCorrenteId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente de origem não encontrada"));

        validarPermissaoConta(origem, request.getClienteId());
        validarContaDesbloqueada(origem);

        ContaCorrente destino = contaCorrenteRepository.findById(request.getContaDestinoId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente de destino não encontrada"));

        validarContaDesbloqueada(destino);

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
    public SolicitacaoReversaoResponse solicitarReversao(CreateOperacaoRequest request) {
        if (request.getContaCorrenteId() == null) {
            throw new IllegalArgumentException("É necessário informar a conta corrente");
        }
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("É necessário informar o cliente");
        }
        if (request.getExtratoOrigemId() == null) {
            throw new IllegalArgumentException("É necessário informar o extrato original para solicitar reversão");
        }

        Extrato extratoOriginal = extratoRepository.findById(request.getExtratoOrigemId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Extrato original não encontrado"));

        ContaCorrente conta = extratoOriginal.getContaCorrente();

        validarPermissaoConta(conta, request.getClienteId());

        if (conta.getIdContaCorrente() != request.getContaCorrenteId().intValue()) {
            throw new IllegalArgumentException("O extrato não pertence à conta informada");
        }

        if (request.getMotivo() == null || request.getMotivo().isBlank()) {
            throw new IllegalArgumentException("É necessário informar o motivo da reversão");
        }

        BigDecimal valor = extratoOriginal.getValorOperacao();
        Operacao operacaoReversa = definirOperacaoReversa(extratoOriginal.getOperacao());

        if (operacaoReversa == null) {
            throw new IllegalArgumentException("Essa operação não pode ser revertida");
        }

        SolicitacaoReversao solicitacao = new SolicitacaoReversao();
        solicitacao.setExtratoOrigem(extratoOriginal);
        solicitacao.setContaCorrente(conta);
        solicitacao.setValor(valor);
        solicitacao.setOperacaoReversa(operacaoReversa);
        solicitacao.setStatus(StatusSolicitacaoReversao.PENDENTE);
        solicitacao.setMotivo(request.getMotivo());
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        SolicitacaoReversao salva = solicitacaoReversaoRepository.save(solicitacao);
        return toSolicitacaoResponse(salva);
    }

    @Transactional
    public DecisaoReversaoResponse decidirSolicitacaoReversao(Long solicitacaoId, Long clienteId, boolean aprovar) {
        AuthenticatedUser user = getAuthenticatedUser();
        if (user == null || user.getRole() != Role.AGENCIA) {
            throw new IllegalArgumentException("Somente usuários da agência podem decidir solicitações de reversão");
        }

        SolicitacaoReversao solicitacao = solicitacaoReversaoRepository.findById(solicitacaoId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Solicitação de reversão não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacaoReversao.PENDENTE) {
            throw new IllegalArgumentException("Solicitação de reversão já foi decidida");
        }

        ContaCorrente conta = solicitacao.getContaCorrente();
        validarPermissaoConta(conta, clienteId);

        if (!clienteId.equals(conta.getCliente().getIdCustomer() * 1L)) {
            throw new IllegalArgumentException("Cliente informado não corresponde ao titular da conta");
        }

        solicitacao.setDataDecisao(LocalDateTime.now());

        DecisaoReversaoResponse response = new DecisaoReversaoResponse();
        response.setSolicitacaoId(solicitacaoId);

        if (!aprovar) {
            solicitacao.setStatus(StatusSolicitacaoReversao.RECUSADA);
            solicitacaoReversaoRepository.save(solicitacao);

            response.setStatus(StatusSolicitacaoReversao.RECUSADA);
            response.setMensagem("Solicitação de reversão recusada pela agência");
            return response;
        }

        Extrato reversao = processarMovimentacaoConta(conta, solicitacao.getValor(), solicitacao.getOperacaoReversa(), null);
        solicitacao.setStatus(StatusSolicitacaoReversao.APROVADA);
        solicitacaoReversaoRepository.save(solicitacao);

        response.setStatus(StatusSolicitacaoReversao.APROVADA);
        response.setExtratoReversaoId(reversao.getIdExtrato() == null ? null : reversao.getIdExtrato().longValue());
        response.setMensagem("Solicitação de reversão aprovada");
        return response;
    }

    public List<Extrato> obterExtrato(Long contaCorrenteId) {
        return obterExtrato(contaCorrenteId, null);
    }

    public List<OperacaoResponse> listarOperacoesDaAgencia() {
        AuthenticatedUser user = getAuthenticatedUser();
        if (user == null || user.getRole() != Role.AGENCIA || user.getAgenciaId() == null) {
            throw new IllegalArgumentException("Somente usuários da agência podem listar operações");
        }

        return extratoRepository
                .findByContaCorrente_Agencia_IdAgencyOrderByDataHoraMovimentoDesc(user.getAgenciaId())
                .stream()
                .map(this::toOperacaoResponse)
                .toList();
    }

    public List<SolicitacaoReversaoResponse> listarReversoesPendentesDaAgencia() {
        AuthenticatedUser user = getAuthenticatedUser();
        if (user == null || user.getRole() != Role.AGENCIA || user.getAgenciaId() == null) {
            throw new IllegalArgumentException("Somente usuários da agência podem listar reversões");
        }

        return solicitacaoReversaoRepository
                .findByContaCorrente_Agencia_IdAgencyAndStatusOrderByDataSolicitacaoAsc(
                        user.getAgenciaId(), StatusSolicitacaoReversao.PENDENTE)
                .stream()
                .map(this::toSolicitacaoResponse)
                .toList();
    }

    public List<Extrato> obterExtrato(Long contaCorrenteId, Long clienteId) {
        Integer contaId = contaCorrenteId.intValue();

        ContaCorrente conta = contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        validarPermissaoConta(conta, clienteId);

        return extratoRepository.findByContaCorrente_IdContaCorrenteOrderByDataHoraMovimentoDesc(contaId);
    }

    public List<Extrato> obterExtratoPorPeriodo(Long contaCorrenteId, LocalDate dataInicial, LocalDate dataFinal) {
        return obterExtratoPorPeriodo(contaCorrenteId, dataInicial, dataFinal, null);
    }

    public List<Extrato> obterExtratoPorPeriodo(Long contaCorrenteId, LocalDate dataInicial, LocalDate dataFinal,
            Long clienteId) {
        Integer contaId = contaCorrenteId.intValue();

        ContaCorrente conta = contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta corrente não encontrada"));

        validarPermissaoConta(conta, clienteId);

        if (dataInicial == null || dataFinal == null) {
            return obterExtrato(contaCorrenteId, clienteId);
        }

        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("A data final deve ser maior ou igual à data inicial");
        }

        LocalDateTime inicio = dataInicial.atStartOfDay();
        LocalDateTime fim = dataFinal.plusDays(1).atStartOfDay().minusNanos(1);

        return extratoRepository.findByContaCorrente_IdContaCorrenteAndDataHoraMovimentoBetweenOrderByDataHoraMovimentoDesc(
                contaId, inicio, fim);
    }

    private OperacaoResponse toOperacaoResponse(Extrato extrato) {
        ContaCorrente conta = extrato.getContaCorrente();
        OperacaoResponse response = new OperacaoResponse();
        response.setId(extrato.getIdExtrato());
        response.setNumeroConta(conta.getNumero());
        response.setClienteNome(conta.getCliente().getNome());
        response.setAgenciaId(conta.getAgencia().getIdAgency());
        response.setTipo(extrato.getOperacao().name());
        response.setValor(extrato.getValorOperacao());
        response.setDataHora(extrato.getDataHoraMovimento());
        return response;
    }

    private SolicitacaoReversaoResponse toSolicitacaoResponse(SolicitacaoReversao solicitacao) {
        SolicitacaoReversaoResponse response = new SolicitacaoReversaoResponse();
        response.setSolicitacaoId(solicitacao.getId().longValue());
        response.setContaCorrenteId((long) solicitacao.getContaCorrente().getIdContaCorrente());
        response.setClienteId((long) solicitacao.getContaCorrente().getCliente().getIdCustomer());
        response.setClienteNome(solicitacao.getContaCorrente().getCliente().getNome());
        response.setValor(solicitacao.getValor());
        response.setOperacaoReversa(solicitacao.getOperacaoReversa());
        response.setStatus(solicitacao.getStatus());
        response.setMotivo(solicitacao.getMotivo());
        response.setDataSolicitacao(solicitacao.getDataSolicitacao());
        return response;
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

    private void validarPermissaoConta(ContaCorrente conta, Long clienteIdInformado) {
        AuthenticatedUser user = getAuthenticatedUser();

        // Permite chamadas internas/testes sem contexto de segurança.
        if (user == null) {
            return;
        }

        if (user.getRole() == Role.CLIENTE) {
            if (user.getClienteId() == null) {
                throw new IllegalArgumentException("Usuário cliente sem vínculo de cliente");
            }
            if (conta.getCliente().getIdCustomer() != user.getClienteId()) {
                throw new IllegalArgumentException("Cliente só pode operar a própria conta");
            }
            return;
        }

        if (user.getRole() == Role.AGENCIA) {
            if (user.getAgenciaId() == null) {
                throw new IllegalArgumentException("Usuário agência sem vínculo de agência");
            }
            if (conta.getAgencia().getIdAgency() != user.getAgenciaId()) {
                throw new IllegalArgumentException("Agência só pode operar contas da própria agência");
            }
            if (clienteIdInformado == null) {
                throw new IllegalArgumentException("Agência deve informar clienteId");
            }
            if (conta.getCliente().getIdCustomer() != clienteIdInformado.intValue()) {
                throw new IllegalArgumentException("clienteId informado não corresponde à conta");
            }
        }
    }

    private void validarContaDesbloqueada(ContaCorrente conta) {
        if (conta.isBloqueada()) {
            throw new IllegalArgumentException("Conta corrente bloqueada");
        }
    }

    private AuthenticatedUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return null;
        }
        return user;
    }
}