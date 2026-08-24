import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import '../HomeAgente/HomeAgente.css';
import './Clientes.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const ITENS_POR_PAGINA = 10;

function Clientes() {
  const navigate = useNavigate();
  const [clientes, setClientes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  function handleMenuClick(){
    setSidebarOpen((previousValue) => !previousValue);
  }

  const clientesFiltrados = clientes.filter((cliente) => {
    const termo = searchTerm.trim().toLowerCase();
    if (!termo) return true;
    return [cliente.nome, cliente.cpf, cliente.email, cliente.telefone]
      .some((valor) => String(valor || '').toLowerCase().includes(termo));
  });
  const totalPaginas = Math.max(1, Math.ceil(clientesFiltrados.length / ITENS_POR_PAGINA));
  const paginaExibida = Math.min(paginaAtual, totalPaginas);
  const inicio = (paginaExibida - 1) * ITENS_POR_PAGINA;
  const clientesPaginados = clientesFiltrados.slice(inicio, inicio + ITENS_POR_PAGINA);

  useEffect(() => {
    async function carregarClientes() {
      const token = localStorage.getItem('bancofinancas.token');

      if (!token) {
        navigate('/login', { replace: true });
        return;
      }

      try {
        const response = await fetch(`${API_URL}/clientes`, {
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
          throw new Error('Nao foi possivel carregar os clientes.');
        }

        const dados = await response.json();
        setClientes(dados);
      } catch (error) {
        setErrorMessage(error.message || 'Erro de conexao com o servidor.');
      } finally {
        setIsLoading(false);
      }
    }

    carregarClientes();
  }, [navigate]);

  return (
    <div className={`clients-layout${sidebarOpen ? ' sidebar-open' : ''}`}> {/* ALTERADO */}
      {/* ALTERADO: o Header fica no grid principal e abre a Sidebar. */}
      <Header onMenuClick={handleMenuClick} />
      {/* ALTERADO: a Sidebar so existe quando esta aberta; o BF interno fecha. */}
      {sidebarOpen && <Sidebar onMenuClick={handleMenuClick} />}
      <div className="clients-content-area">
        <main className="clients-main">
          <div className="clients-heading">
            <p className="agency-eyebrow">Clientes</p>
            <h1>Lista de clientes</h1>
            <label className="clients-search-label" htmlFor="clientes-search">
              Pesquisar clientes
            </label>
            <input
              className="clients-search"
              id="clientes-search"
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPaginaAtual(1);
              }}
              placeholder="Nome, CPF, e-mail ou telefone"
              type="search"
              value={searchTerm}
            />
            <p>Aqui você pode visualizar e gerenciar os clientes cadastrados na sua agência.</p>
          </div>
          <section className="clients-clients" aria-label="Lista de clientes">
            {isLoading && <p className="clients-feedback">Carregando clientes...</p>}

            {!isLoading && errorMessage && (
              <p className="clients-feedback error" role="alert">{errorMessage}</p>
            )}

            {!isLoading && !errorMessage && clientes.length === 0 && (
              <p className="clients-feedback">Nenhum cliente cadastrado ainda.</p>
            )}

            {!isLoading && !errorMessage && clientes.length > 0 && clientesFiltrados.length === 0 && (
              <p className="clients-feedback">Nenhum cliente encontrado para essa pesquisa.</p>
            )}

            {!isLoading && !errorMessage && clientesFiltrados.length > 0 && (
              <div className="clients-table-wrapper">
                <table className="clients-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nome</th>
                      <th>CPF</th>
                      <th>Telefone</th>
                      <th>E-mail</th>
                    </tr>
                  </thead>
                  <tbody>
                    {clientesPaginados.map((cliente) => (
                      <tr key={cliente.idCustomer}>
                        <td>{cliente.idCustomer}</td>
                        <td>{cliente.nome}</td>
                        <td>{cliente.cpf}</td>
                        <td>{cliente.telefone}</td>
                        <td>{cliente.email}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            {!isLoading && !errorMessage && clientesFiltrados.length > 0 && (
              <nav aria-label="Paginação de clientes" className="clients-pagination">
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
    </div>
  );
}

export default Clientes;