import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header/Header';
import Sidebar from '../components/SideBar/Sidebar';
import './HomeAgente.css';
import './Clientes.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function Clientes() {
  const navigate = useNavigate();
  const [clientes, setClientes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

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
    <div className="agency-layout">
      <Sidebar />
      <div className="agency-content-area">
        <Header />
        <main className="agency-main">
          <div className="agency-heading">
            <p className="agency-eyebrow">Clientes</p>
            <h1>Lista de clientes</h1>
            <p>Aqui você pode visualizar e gerenciar os clientes cadastrados na sua agência.</p>
          </div>
          <section className="agency-clients" aria-label="Lista de clientes">
            {isLoading && <p className="clients-feedback">Carregando clientes...</p>}

            {!isLoading && errorMessage && (
              <p className="clients-feedback error" role="alert">{errorMessage}</p>
            )}

            {!isLoading && !errorMessage && clientes.length === 0 && (
              <p className="clients-feedback">Nenhum cliente cadastrado ainda.</p>
            )}

            {!isLoading && !errorMessage && clientes.length > 0 && (
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
                    {clientes.map((cliente) => (
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
          </section>
        </main>
      </div>
    </div>
  );
}

export default Clientes;