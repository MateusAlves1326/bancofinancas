import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './HomeAgente.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function HomeAgente() {
  const navigate = useNavigate();
  const [resumo, setResumo] = useState({ clientes: 0, contas: 0, operacoes: 0 });
  const [operacoesRecentes, setOperacoesRecentes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  // ALTERADO: controla a abertura e o fechamento da Sidebar somente nesta tela.
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // ALTERADO: o mesmo callback e usado pelo BF do Header e pelo BF da Sidebar.
  function handleMenuClick() {
    setSidebarOpen((previousValue) => !previousValue);
  }

  useEffect(() => {
    async function carregarPainel() {
      const token = localStorage.getItem('bancofinancas.token');

      if (!token) {
        navigate('/login', { replace: true });
        return;
      }

      try {
        const headers = { Authorization: `Bearer ${token}` };
        const [clientesResponse, contasResponse, operacoesResponse] = await Promise.all([
          fetch(`${API_URL}/clientes`, { headers }),
          fetch(`${API_URL}/contas`, { headers }),
          fetch(`${API_URL}/operacoes`, { headers })
        ]);

        if ([clientesResponse, contasResponse, operacoesResponse]
          .some((response) => response.status === 401 || response.status === 403)) {
          localStorage.removeItem('bancofinancas.token');
          navigate('/login', { replace: true });
          return;
        }

        if (![clientesResponse, contasResponse, operacoesResponse].every((response) => response.ok)) {
          throw new Error('Nao foi possivel carregar os dados da agencia.');
        }

        const [clientes, contas, operacoes] = await Promise.all([
          clientesResponse.json(),
          contasResponse.json(),
          operacoesResponse.json()
        ]);

        setResumo({
          clientes: clientes.length,
          contas: contas.filter((conta) => !conta.bloqueada).length,
          operacoes: operacoes.length
        });
        setOperacoesRecentes(operacoes.slice(0, 5));
      } catch (error) {
        setErrorMessage(error.message || 'Erro de conexao com o servidor.');
      } finally {
        setIsLoading(false);
      }
    }

    carregarPainel();
  }, [navigate]);

  function formatarValor(valor) {
    return Number(valor).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
  }

  function formatarData(data) {
    return new Date(data).toLocaleString('pt-BR');
  }

  return (
    <div className={`agency-layout ${sidebarOpen ? 'sidebar-open' : ''}`}> {/* ALTERADO */}
      <Header onMenuClick={handleMenuClick} />
      {sidebarOpen && <Sidebar onMenuClick={handleMenuClick} />}
      <div className="agency-content-area">
        <main className="agency-main">
          <div className="agency-heading">
            <p className="agency-eyebrow">Painel da agencia</p>
            <h1>Visao geral</h1>
            <p>Acompanhe os principais dados e atividades da sua agencia.</p>
          </div>

          <section className="agency-summary" aria-label="Resumo da agencia">
            <article className="summary-card">
              <span className="summary-label">Clientes</span>
              <strong>{isLoading ? '...' : resumo.clientes}</strong>
              <span className="summary-caption">clientes cadastrados</span>
            </article>
            <article className="summary-card">
              <span className="summary-label">Contas</span>
              <strong>{isLoading ? '...' : resumo.contas}</strong>
              <span className="summary-caption">contas ativas</span>
            </article>
            <article className="summary-card">
              <span className="summary-label">Operacoes</span>
              <strong>{isLoading ? '...' : resumo.operacoes}</strong>
              <span className="summary-caption">operacoes recentes</span>
            </article>
          </section>

          <section className="agency-activity">
            <div>
              <p className="agency-eyebrow">Atividade</p>
              <h2>Operacoes recentes</h2>
            </div>
            {isLoading && <p className="empty-activity">Carregando operacoes...</p>}
            {!isLoading && errorMessage && (
              <p className="empty-activity" role="alert">{errorMessage}</p>
            )}
            {!isLoading && !errorMessage && operacoesRecentes.length === 0 && (
              <p className="empty-activity">Nenhuma operacao registrada ainda.</p>
            )}
            {!isLoading && !errorMessage && operacoesRecentes.length > 0 && (
              <div className="recent-operations" aria-label="Operacoes recentes">
                {operacoesRecentes.map((operacao) => (
                  <article className="recent-operation" key={operacao.id}>
                    <div>
                      <strong>{operacao.tipo}</strong>
                      <span>Conta {operacao.numeroConta} - {operacao.clienteNome}</span>
                    </div>
                    <div className="recent-operation-details">
                      <strong>{formatarValor(operacao.valor)}</strong>
                      <span>{formatarData(operacao.dataHora)}</span>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>
        </main>
      </div>
    </div>
  );
}

export default HomeAgente;