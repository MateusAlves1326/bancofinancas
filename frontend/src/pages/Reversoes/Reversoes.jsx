import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './Reversoes.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const ITENS_POR_PAGINA = 5;

function Reversoes() {
    const navigate = useNavigate();
    const [reversoes, setReversoes] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isUpdating, setIsUpdating] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [reversaoSelecionada, setReversaoSelecionada] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [paginaAtual, setPaginaAtual] = useState(1);

    const reversoesFiltradas = reversoes.filter((reversao) => {
        const termo = searchTerm.trim().toLowerCase();
        if (!termo) {
            return true;
        }

        return [
            reversao.solicitacaoId,
            reversao.clienteNome,
            reversao.operacaoReversa,
            reversao.valor,
        ].some((valor) => String(valor || '').toLowerCase().includes(termo));
    });
    const totalPaginas = Math.max(1, Math.ceil(reversoesFiltradas.length / ITENS_POR_PAGINA));
    const paginaExibida = Math.min(paginaAtual, totalPaginas);
    const inicio = (paginaExibida - 1) * ITENS_POR_PAGINA;
    const reversoesPaginadas = reversoesFiltradas.slice(inicio, inicio + ITENS_POR_PAGINA);

    useEffect(() => {
        async function carregarReversoes() {
            const token = localStorage.getItem('bancofinancas.token');
            if (!token) {
                navigate('/login', { replace: true });
                return;
            }

            try {
                const response = await fetch(`${API_URL}/agentes/reversoes`, {
                    headers: { Authorization: `Bearer ${token}` },
                });

                if (response.status === 401 || response.status === 403) {
                    localStorage.removeItem('bancofinancas.token');
                    navigate('/login', { replace: true });
                    return;
                }

                if (!response.ok) {
                    throw new Error('Nao foi possivel carregar as reversoes.');
                }

                setReversoes(await response.json());
            } catch (error) {
                setErrorMessage(error.message || 'Erro de conexao com o servidor.');
            } finally {
                setIsLoading(false);
            }
        }

        carregarReversoes();
    }, [navigate]);

    async function decidirReversao(reversao, aprovar) {
        const token = localStorage.getItem('bancofinancas.token');
        setErrorMessage('');
        setSuccessMessage('');
        setIsUpdating(reversao.solicitacaoId);

        try {
            const response = await fetch(`${API_URL}/agentes/reversoes/decisoes`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    solicitacaoId: reversao.solicitacaoId,
                    clienteId: reversao.clienteId,
                    aprovar,
                }),
            });

            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('bancofinancas.token');
                navigate('/login', { replace: true });
                return;
            }

            const body = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(body.message || 'Nao foi possivel registrar a decisao.');
            }

            setReversoes((reversoesAtuais) => reversoesAtuais.filter(
                ({ solicitacaoId }) => solicitacaoId !== reversao.solicitacaoId,
            ));
            setReversaoSelecionada(null);
            setSuccessMessage(body.mensagem || 'Decisao registrada com sucesso.');
        } catch (error) {
            setErrorMessage(error.message || 'Erro de conexao com o servidor.');
        } finally {
            setIsUpdating(null);
        }
    }

    return (
        <div className="agency-layout">
            <Sidebar />
            <div className="agency-content-area">
                <Header />
                <main className="agency-main">
                    <div className="agency-heading">
                        <p className="agency-eyebrow">Operacoes</p>
                        <h1>Solicitacoes de reversao</h1>
                        <label className="reversoes-search-label" htmlFor="reversoes-search">Pesquisar solicitacoes</label>
                        <input
                            className="reversoes-search"
                            id="reversoes-search"
                            onChange={(event) => {
                                setSearchTerm(event.target.value);
                                setPaginaAtual(1);
                            }}
                            placeholder="Cliente, operacao, valor ou solicitacao"
                            type="search"
                            value={searchTerm}
                        />
                        <p>Analise as solicitacoes pendentes e registre a decisao da agencia.</p>
                    </div>

                    <section className="reversoes-section" aria-label="Solicitacoes de reversao pendentes">
                        {isLoading && <p className="reversoes-feedback">Carregando solicitacoes...</p>}

                        {!isLoading && errorMessage && (
                            <p className="reversoes-feedback reversoes-error" role="alert">{errorMessage}</p>
                        )}

                        {!isLoading && successMessage && (
                            <p className="reversoes-feedback reversoes-success" role="status">{successMessage}</p>
                        )}

                        {!isLoading && !errorMessage && reversoes.length === 0 && (
                            <p className="reversoes-feedback">Nenhuma solicitacao pendente.</p>
                        )}

                        {!isLoading && !errorMessage && reversoes.length > 0 && reversoesFiltradas.length === 0 && (
                            <p className="reversoes-feedback">Nenhuma solicitacao encontrada para esta pesquisa.</p>
                        )}

                        {!isLoading && reversoesFiltradas.length > 0 && (
                            <div className="reversoes-table-wrapper">
                                <table className="reversoes-table">
                                    <thead>
                                        <tr>
                                            <th>Solicitacao</th>
                                            <th>Cliente</th>
                                            <th>Operacao</th>
                                            <th>Valor</th>
                                            <th>Acoes</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {reversoesPaginadas.map((reversao) => {
                                            return (
                                                <tr key={reversao.solicitacaoId}>
                                                    <td>#{reversao.solicitacaoId}</td>
                                                    <td>{reversao.clienteNome || `Cliente #${reversao.clienteId}`}</td>
                                                    <td>{reversao.operacaoReversa}</td>
                                                    <td>{Number(reversao.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td>
                                                    <td className="reversoes-actions">
                                                        <button
                                                            className="reversoes-action-button"
                                                            onClick={() => setReversaoSelecionada(reversao)}
                                                            type="button"
                                                        >
                                                            Acoes
                                                        </button>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        )}

                        {!isLoading && reversoesFiltradas.length > 0 && (
                            <nav aria-label="Paginacao de solicitacoes" className="reversoes-pagination">
                                <button
                                    disabled={paginaExibida === 1}
                                    onClick={() => setPaginaAtual(paginaExibida - 1)}
                                    type="button"
                                >
                                    Anterior
                                </button>
                                <span>Pagina {paginaExibida} de {totalPaginas}</span>
                                <button
                                    disabled={paginaExibida === totalPaginas}
                                    onClick={() => setPaginaAtual(paginaExibida + 1)}
                                    type="button"
                                >
                                    Proxima
                                </button>
                            </nav>
                        )}
                    </section>
                </main>
            </div>
            {reversaoSelecionada && (
                <div className="reversao-modal-backdrop" role="presentation" onMouseDown={() => !isUpdating && setReversaoSelecionada(null)}>
                    <section
                        aria-labelledby="modal-reversao-title"
                        aria-modal="true"
                        className="reversao-modal"
                        role="dialog"
                        onMouseDown={(event) => event.stopPropagation()}
                    >
                        <div className="reversao-modal-header">
                            <div>
                                <p>Solicitacao #{reversaoSelecionada.solicitacaoId}</p>
                                <h2 id="modal-reversao-title">Revisar reversao</h2>
                            </div>
                            <button aria-label="Fechar modal" disabled={Boolean(isUpdating)} onClick={() => setReversaoSelecionada(null)} type="button">
                                &times;
                            </button>
                        </div>
                        <dl className="reversao-modal-details">
                            <div><dt>Cliente</dt><dd>{reversaoSelecionada.clienteNome || `Cliente #${reversaoSelecionada.clienteId}`}</dd></div>
                            <div><dt>Operacao</dt><dd>{reversaoSelecionada.operacaoReversa}</dd></div>
                            <div><dt>Valor</dt><dd>{Number(reversaoSelecionada.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</dd></div>
                        </dl>
                        <div className="reversao-modal-reason">
                            <span>Motivo da solicitacao</span>
                            <p>{reversaoSelecionada.motivo || 'Nenhum motivo informado.'}</p>
                        </div>
                        <div className="reversao-modal-actions">
                            <button
                                className="reversoes-action-button approve"
                                disabled={Boolean(isUpdating)}
                                onClick={() => decidirReversao(reversaoSelecionada, true)}
                                type="button"
                            >
                                {isUpdating ? 'Salvando...' : 'Aceitar'}
                            </button>
                            <button
                                className="reversoes-action-button reject"
                                disabled={Boolean(isUpdating)}
                                onClick={() => decidirReversao(reversaoSelecionada, false)}
                                type="button"
                            >
                                Recusar
                            </button>
                        </div>
                    </section>
                </div>
            )}
        </div>
    );
}

export default Reversoes;