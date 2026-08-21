import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './Operacoes.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const ITENS_POR_PAGINA = 5;

function Operacoes() {
  const navigate = useNavigate();
  const [operacoes, setOperacoes] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  function handleMenuClick() {
    setSidebarOpen((previousValue) => !previousValue);
  }

  const filteredOperacoesMemo = operacoes.filter((operacao) => {
    const termo = searchTerm.trim().toLowerCase();
    if (!termo) {
      return true;
    }
    return [operacao.numeroConta, operacao.clienteNome, operacao.agenciaId, operacao.tipo]
      .some((valor) => String(valor).toLowerCase().includes(termo));
  });

  const totalPaginas = Math.max(1, Math.ceil(filteredOperacoesMemo.length / ITENS_POR_PAGINA));
  const paginaExibida = Math.min(paginaAtual, totalPaginas);
  const inicio = (paginaExibida - 1) * ITENS_POR_PAGINA;
  const operacoesPaginadas = filteredOperacoesMemo.slice(inicio, inicio + ITENS_POR_PAGINA);


  useEffect(() => {
    async function carregarOperacoes() {
      const token = localStorage.getItem('bancofinancas.token');
      if (!token) {
        navigate('/login', { replace: true });
        return;
      }

      try {
        const response = await fetch(`${API_URL}/operacoes`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.status === 401 || response.status === 403) {
          localStorage.removeItem('bancofinancas.token');
          navigate('/login', { replace: true });
          return;
        }

        if (!response.ok) {
          throw new Error('Erro ao carregar operações');
        }

        const dados = await response.json();
        setOperacoes(dados);
      } catch (error) {
        setErrorMessage(error.message || 'Erro de conexão com o servidor');
      } finally {
        setLoading(false);
      }
    }
    carregarOperacoes();
  }, [navigate]);
  return (
    <div className={`adicionar-saldo ${sidebarOpen ? 'sidebar-open' : ''}`}>
            <Header onMenuClick={handleMenuClick} />
            {sidebarOpen && <Sidebar onMenuClick={handleMenuClick} />}
      
        <main className="agency-main">
          <div className="agency-heading">
            <p className="agency-eyebrow">Operações</p>
            <h1 className="agency-title">Lista de Operações</h1>
            <label className="operacoes-search-label" htmlFor="operacoes-search">
              Pesquisar:
            </label>
            <input
              id="operacoes-search"
              type="search"
              placeholder="Conta, cliente, agência ou tipo"
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPaginaAtual(1);
              }}
            />
            <p>Acompanhe todas as movimentações.</p>
          </div>
          <section className="agency-section" aria-label='lista de operacoes'>
            {isLoading && <p>Carregando Operações...</p>}

            {!isLoading && errorMessage && (
              <p className='operacoes-feedback operacoes-error' role='alert'>{errorMessage}</p>
            )}

            {!isLoading && !errorMessage && operacoes.length === 0 && (
              <p className='operacoes-feedback operacoes-empty'>Nenhuma operação encontrada.</p>
            )}

            {!isLoading && !errorMessage && operacoes.length > 0 && filteredOperacoesMemo.length === 0 && (
              <p className='operacoes-feedback operacoes-empty'>Nenhuma operação encontrada para o termo pesquisado.</p>
            )}

            {!isLoading && !errorMessage && filteredOperacoesMemo.length > 0 && (
              <div className="operacoes-table-wrapper">
                <table className="operacoes-table">
                  <thead>
                    <tr>
                      <th className="operacoes-table-col-conta">Conta</th>
                      <th>Cliente</th>
                      <th>Agência</th>
                      <th>Tipo</th>
                      <th>Valor</th>
                      <th>Data</th>
                    </tr>
                  </thead>
                  <tbody>
                    {operacoesPaginadas.map((operacao) => (
                      <tr key={operacao.id}>
                        <td>{operacao.numeroConta}</td>
                        <td>{operacao.clienteNome}</td>
                        <td>{operacao.agenciaId}</td>
                        <td>{operacao.tipo}</td>
                        <td>{Number(operacao.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td>
                        <td>{new Date(operacao.dataHora).toLocaleString('pt-BR')}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {!isLoading && !errorMessage && filteredOperacoesMemo.length > 0 && (
              <nav aria-label="Paginação de operações" className="operacoes-pagination">
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
    
  );
}

export default Operacoes;