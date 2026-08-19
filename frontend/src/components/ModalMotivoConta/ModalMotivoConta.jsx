import { useEffect, useState } from 'react';
import './ModalMotivoConta.css';

function ModalMotivoConta({ conta, acao, isSubmitting, onClose, onConfirm }) {
    const [motivo, setMotivo] = useState('');
    const bloqueando = acao === 'bloquear';

    useEffect(() => {
        if (conta) {
            setMotivo('');
        }
    }, [conta, acao]);

    if (!conta) {
        return null;
    }

    function handleSubmit(event) {
        event.preventDefault();
        const motivoLimpo = motivo.trim();

        if (motivoLimpo) {
            onConfirm(motivoLimpo);
        }
    }

    return (
        <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
            <section
                aria-labelledby="modal-conta-title"
                aria-modal="true"
                className="conta-modal"
                role="dialog"
                onMouseDown={(event) => event.stopPropagation()}
            >
                <div className="conta-modal-header">
                    <div>
                        <p className="conta-modal-eyebrow">Conta {conta.numero}</p>
                        <h2 id="modal-conta-title">
                            {bloqueando ? 'Bloquear conta' : 'Desbloquear conta'}
                        </h2>
                    </div>
                    <button className="modal-close-button" type="button" onClick={onClose} aria-label="Fechar modal">
                        &times;
                    </button>
                </div>

                <p className="conta-modal-description">
                    Informe o motivo para {bloqueando ? 'bloquear' : 'desbloquear'} esta conta.
                </p>

                <form onSubmit={handleSubmit}>
                    <label htmlFor="motivo-conta">Motivo</label>
                    <textarea
                        id="motivo-conta"
                        maxLength={255}
                        onChange={(event) => setMotivo(event.target.value)}
                        placeholder="Descreva o motivo da alteracao"
                        required
                        rows={4}
                        value={motivo}
                    />
                    <span className="motivo-counter">{motivo.length}/255</span>

                    <div className="conta-modal-actions">
                        <button className="modal-cancel-button" type="button" onClick={onClose}>
                            Cancelar
                        </button>
                        <button
                            className={`modal-confirm-button ${bloqueando ? 'danger' : 'success'}`}
                            disabled={isSubmitting || !motivo.trim()}
                            type="submit"
                        >
                            {isSubmitting ? 'Salvando...' : bloqueando ? 'Bloquear conta' : 'Desbloquear conta'}
                        </button>
                    </div>
                </form>
            </section>
        </div>
    );
}

export default ModalMotivoConta;