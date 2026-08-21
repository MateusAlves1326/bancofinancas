import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import ModalMotivoConta from '../../components/ModalMotivoConta/ModalMotivoConta';
import './Contas.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const ITENS_POR_PAGINA = 5;

function Contas() {
    const navigate = useNavigate();
    const [contas, setContas] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [paginaAtual, setPaginaAtual] = useState(1);
    const [contaSelecionada, setContaSelecionada] = useState(null);
    const [acaoModal, setAcaoModal] = useState(null);
    const [isUpdating, setIsUpdating] = useState(false);
     const [sidebarOpen, setSidebarOpen] = useState(false);

      function handleMenuClick(){
    setSidebarOpen((previousValue) => !previousValue);
  }

    const contasFiltradas = contas.filter((conta) => {
        const termo = searchTerm.trim().toLowerCase();
        if (!termo) {
            return true;
        }

        return [conta.numero, conta.clienteNome, conta.agenciaId]
            .some((valor) => String(valor).toLowerCase().includes(termo));
    });
            const totalPaginas = Math.max(1, Math.ceil(contasFiltradas.length / ITENS_POR_PAGINA));
            const paginaExibida = Math.min(paginaAtual, totalPaginas);
            const inicio = (paginaExibida - 1) * ITENS_POR_PAGINA;
            const contasPaginadas = contasFiltradas.slice(inicio, inicio + ITENS_POR_PAGINA);

    function abrirModal(conta, acao) {
        setContaSelecionada(conta);
        setAcaoModal(acao);
    }

    function fecharModal() {
        if (!isUpdating) {
            setContaSelecionada(null);
            setAcaoModal(null);
        }
    }

    async function atualizarBloqueio(motivo) {
        const token = localStorage.getItem('bancofinancas.token');
        const bloqueada = acaoModal === 'bloquear';

        setIsUpdating(true);
        try {
            const response = await fetch(`${API_URL}/agentes/contas/${contaSelecionada.id}/bloqueio`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    clienteId: contaSelecionada.clienteId,
                    bloqueada,
                    motivo,
                }),
            });

            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('bancofinancas.token');
                navigate('/login', { replace: true });
                return;
            }

            const body = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(body.message || 'Nao foi possivel atualizar o bloqueio da conta.');
            }

            setContas((contasAtuais) => contasAtuais.map((conta) => (
                conta.id === body.id ? body : conta
            )));
            fecharModal();
        } catch (error) {
            setErrorMessage(error.message || 'Erro de conexao com o servidor.');
        } finally {
            setIsUpdating(false);
        }
    }

    useEffect(() => {
        async function carregarContas() {
            const token = localStorage.getItem('bancofinancas.token');
            if (!token) {
                navigate('/login', { replace: true });
                return;
            }

            try {
                const response = await fetch(`${API_URL}/contas`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.status === 401 || response.status === 403) {
                    localStorage.removeItem('bancofinancas.token');
                    navigate('/login', { replace: true });
                    return;
                }

                if (!response.ok) {
                    throw new Error('Nao foi possivel carregar as contas.');
                }

                const dados = await response.json();
                setContas(dados);
            } catch (error) {
                setErrorMessage(error.message || 'Erro de conexao com o servidor.');
            } finally {
                setIsLoading(false);
            }
        }

        carregarContas();
    }, [navigate]);
    return (
        <div className={`contas-layout${sidebarOpen ? ' sidebar-open' : ''}`}> {/* ALTERADO */}
      {/* ALTERADO: o Header fica no grid principal e abre a Sidebar. */}
      <Header onMenuClick={handleMenuClick} />
      {/* ALTERADO: a Sidebar so existe quando esta aberta; o BF interno fecha. */}
      {sidebarOpen && <Sidebar onMenuClick={handleMenuClick} />}
            <div className="agency-content-area">
                <main className="agency-main">
                    <div className="agency-heading">
                        <p className="agency-eyebrow">Contas</p>
                        <h1>Lista de contas</h1>
                        <label className="contas-search-label" htmlFor="contas-search">
                            Pesquisar contas
                        </label>
                        <input
                            id="contas-search"
                            type="search"
                            placeholder="Numero, cliente ou agencia"
                            className="contas-search"
                            value={searchTerm}
                            onChange={(event) => {
                                setSearchTerm(event.target.value);
                                setPaginaAtual(1);
                            }}
                        />
                        <p>Acompanhe as contas cadastradas na sua agencia.</p>
                    </div>
                    <section className="contas-section" aria-label="Lista de contas">
                        {isLoading && <p className="contas-feedback">Carregando contas...</p>}

                        {!isLoading && errorMessage && (
                            <p className="contas-feedback contas-error" role="alert">{errorMessage}</p>
                        )}

                        {!isLoading && !errorMessage && contas.length === 0 && (
                            <p className="contas-feedback">Nenhuma conta cadastrada.</p>
                        )}

                        {!isLoading && !errorMessage && contas.length > 0 && contasFiltradas.length === 0 && (
                            <p className="contas-feedback">Nenhuma conta encontrada para essa pesquisa.</p>
                        )}

                        {!isLoading && !errorMessage && contasFiltradas.length > 0 && (
                            <div className="contas-table-wrapper">
                                <table className="contas-table">
                                <thead>
                                    <tr>
                                        <th>Conta</th>
                                        <th>Cliente</th>
                                        <th>Saldo</th>
                                        <th>Agencia</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {contasPaginadas.map((conta) => (
                                        <tr key={conta.id}>
                                            <td>{conta.numero}</td>
                                            <td>{conta.clienteNome}</td>
                                            <td>{conta.saldo}</td>
                                            <td>{conta.agenciaId}</td>
                                            <td className="contas-actions">
                                                {conta.bloqueada ? (
                                                    <button className="contas-action-button unblock" onClick={() => abrirModal(conta, 'desbloquear')} type="button">
                                                        Desbloquear
                                                    </button>
                                                ) : (
                                                    <button className="contas-action-button block" onClick={() => abrirModal(conta, 'bloquear')} type="button">
                                                        Bloquear
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                                </table>
                            </div>
                        )}
                        {!isLoading && !errorMessage && contasFiltradas.length > 0 && (
                            <nav aria-label="Paginação de contas" className="contas-pagination">
                                <button
                                    disabled={paginaExibida === 1}
                                    onClick={() => setPaginaAtual(paginaExibida - 1)}
                                    type="button"
                                >
                                    Anterior
                                </button>
                                <span>Página {paginaExibida} de {totalPaginas}</span>
                                <button
                                    disabled={paginaExibida === totalPaginas}
                                    onClick={() => setPaginaAtual(paginaExibida + 1)}
                                    type="button"
                                >
                                    Próxima
                                </button>
                            </nav>
                        )}
                    </section>
                </main>
            </div>
            <ModalMotivoConta
                acao={acaoModal}
                conta={contaSelecionada}
                isSubmitting={isUpdating}
                onClose={fecharModal}
                onConfirm={atualizarBloqueio}
            />
        </div>
    );
}

export default Contas;
