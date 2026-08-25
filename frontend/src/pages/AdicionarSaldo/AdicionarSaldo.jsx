import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './AdicionarSaldo.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function AdicionarSaldo() {
    const navigate = useNavigate();
    const [agenciaId, setAgenciaId] = useState('');
    const [numeroConta, setNumeroConta] = useState('');
    const [valor, setValor] = useState('');
    const [loading, setLoading] = useState(false);
    const [erro, setErro] = useState('');

    const [sidebarOpen, setSidebarOpen] = useState(false);

    function handleMenuClick() {
        setSidebarOpen((prev) => !prev);
    }

    function impedirAlteracaoPorScroll(event) {
        event.currentTarget.blur();
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

            const response = await fetch(`${API_URL}/agentes/contas/saldo`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    agenciaId: Number(agenciaId),
                    numeroConta: Number(numeroConta),
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
                        <label htmlFor="agenciaId">Agência:</label>
                        <input
                            type="number"
                            id="agenciaId"
                            name="agenciaId"
                            value={agenciaId}
                            onWheel={impedirAlteracaoPorScroll}
                            onChange={(event) => setAgenciaId(event.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="numeroConta">Número da Conta:</label>
                        <input
                            type="number"
                            id="numeroConta"
                            name="numeroConta"
                            value={numeroConta}
                            onWheel={impedirAlteracaoPorScroll}
                            onChange={(event) => setNumeroConta(event.target.value)}
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
                            onWheel={impedirAlteracaoPorScroll}
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