package acc.br.bancofinancas.dto;

import acc.br.bancofinancas.model.StatusSolicitacaoReversao;

public class DecisaoReversaoResponse {

    private Long solicitacaoId;
    private StatusSolicitacaoReversao status;
    private Long extratoReversaoId;
    private String mensagem;

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public StatusSolicitacaoReversao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoReversao status) {
        this.status = status;
    }

    public Long getExtratoReversaoId() {
        return extratoReversaoId;
    }

    public void setExtratoReversaoId(Long extratoReversaoId) {
        this.extratoReversaoId = extratoReversaoId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
