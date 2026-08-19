package acc.br.bancofinancas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitacao_reversao")
public class SolicitacaoReversao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_extrato_origem", nullable = false)
    private Extrato extratoOrigem;

    @ManyToOne
    @JoinColumn(name = "id_conta_corrente", nullable = false)
    private ContaCorrente contaCorrente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Operacao operacaoReversa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacaoReversao status;

    @Column(length = 255)
    private String motivo;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_decisao")
    private LocalDateTime dataDecisao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Extrato getExtratoOrigem() {
        return extratoOrigem;
    }

    public void setExtratoOrigem(Extrato extratoOrigem) {
        this.extratoOrigem = extratoOrigem;
    }

    public ContaCorrente getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(ContaCorrente contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Operacao getOperacaoReversa() {
        return operacaoReversa;
    }

    public void setOperacaoReversa(Operacao operacaoReversa) {
        this.operacaoReversa = operacaoReversa;
    }

    public StatusSolicitacaoReversao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoReversao status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(LocalDateTime dataDecisao) {
        this.dataDecisao = dataDecisao;
    }
}
