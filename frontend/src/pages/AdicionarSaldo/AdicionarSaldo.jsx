import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './AdicionarSaldo.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function AdicionarSaldo() {
    const navigate = useNavigate();
    const [contaId, setContaId] = useState('');
    const [clienteId, setClienteId] = useState('');
    const [valor, setValor] = useState('');
    const [loading, setLoading] = useState(false);
    const [erro, setErro] = useState('');

    const [sidebarOpen, setSidebarOpen] = useState(false);

    function handleMenuClick() {
        setSidebarOpen((prev) => !prev);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setLoading(true);
        setErro('');

        try {
            const token = localStorage.getItem('bancofinancas.token');

            if (!token) {
                navigate('/login', { replace: true });
                return;
            }

            const response = await fetch(`${API_URL}/agentes/contas/${contaId}/saldo`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    clienteId: Number(clienteId),
                    valor: Number(valor)
                })
            });

            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('bancofinancas.token');
                navigate('/login', { replace: true });
                return;
            }

            const responseBody = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(responseBody.message || 'Erro ao adicionar saldo');
            }

            navigate('/agente/operacoes');
        } catch (error) {
            setErro(error.message || 'Erro ao adicionar saldo');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className={`adicionar-saldo ${sidebarOpen ? 'sidebar-open' : ''}`}>
            <Header onMenuClick={handleMenuClick} />
            {sidebarOpen && <Sidebar onMenuClick={handleMenuClick} />}
            <main>
                <h1>Adicionar Saldo</h1>
                {erro && <p role="alert">{erro}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="contaId">Número da Conta:</label>
                        <input
                            type="number"
                            id="contaId"
                            name="contaId"
                            value={contaId}
                            onChange={(event) => setContaId(event.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="clienteId">ID do Cliente:</label>
                        <input
                            type="number"
                            id="clienteId"
                            name="clienteId"
                            value={clienteId}
                            onChange={(event) => setClienteId(event.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="valor">Valor:</label>
                        <input
                            type="number"
                            id="valor"
                            name="valor"
                            min="0.01"
                            step="0.01"
                            value={valor}
                            onChange={(event) => setValor(event.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" disabled={loading}>
                        {loading ? 'Adicionando...' : 'Adicionar Saldo'}
                    </button>
                </form>
            </main>
        </div>
    );
}

export default AdicionarSaldo;