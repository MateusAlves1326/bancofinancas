import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './CriarConta.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function CriarConta() {
    const navigate = useNavigate();

    const [cliente, setCliente] = useState({
        nome: '',
        email: '',
        telefone: '',
        cpf: ''
    });

    const [conta, setConta] = useState({
        agenciaId: '',
        numero: '',
        saldo: 0
    });

    const [loading, setLoading] = useState(false);
    const [erro, setErro] = useState('');

    function handleClienteChange(event) {
        const { name, value } = event.target;

        setCliente({
            ...cliente,
            [name]: value
        });
    }

    function handleContaChange(event) {
        const { name, value } = event.target;

        setConta({
            ...conta,
            [name]: value
        });
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

            // 1. Criar o cliente
            const clienteResponse = await fetch(`${API_URL}/clientes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(cliente)
            });

            if (clienteResponse.status === 401 || clienteResponse.status === 403) {
                localStorage.removeItem('bancofinancas.token');
                navigate('/login', { replace: true });
                return;
            }

            if (!clienteResponse.ok) {
                throw new Error('Erro ao cadastrar cliente');
            }

            const clienteCriado = await clienteResponse.json();

            // 2. O backend retorna o identificador como idCustomer
            const clienteId = clienteCriado.idCustomer;

            if (!clienteId) {
                throw new Error('A API não retornou o ID do cliente');
            }

            // 3. Criar a conta vinculada ao cliente
            const contaResponse = await fetch(`${API_URL}/agentes/contas`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    clienteId: clienteId,
                    agenciaId: Number(conta.agenciaId),
                    numero: Number(conta.numero),
                    saldo: Number(conta.saldo)
                })
            });

            const contaBody = await contaResponse.json().catch(() => ({}));

            if (!contaResponse.ok) {
                throw new Error(
                    contaBody.message || 'Cliente criado, mas ocorreu um erro ao criar a conta',
                );
            }

            navigate('/agente/contas');

        } catch (error) {
            console.error(error);
            setErro(error.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="criar-conta">
            <Header />
            <Sidebar />

            <main>
                <h1>Criar Conta</h1>

                {erro && (
                    <p className="erro">
                        {erro}
                    </p>
                )}

                <form onSubmit={handleSubmit}>

                    <h2>Dados do Cliente</h2>

                    <div>
                        <label>Nome</label>
                        <input
                            type="text"
                            name="nome"
                            value={cliente.nome}
                            onChange={handleClienteChange}
                            required
                        />
                    </div>

                    <div>
                        <label>Email</label>
                        <input
                            type="email"
                            name="email"
                            value={cliente.email}
                            onChange={handleClienteChange}
                            required
                        />
                    </div>

                    <div>
                        <label>Telefone</label>
                        <input
                            type="text"
                            name="telefone"
                            value={cliente.telefone}
                            onChange={handleClienteChange}
                            required
                        />
                    </div>

                    <div>
                        <label>CPF</label>
                        <input
                            type="text"
                            name="cpf"
                            value={cliente.cpf}
                            onChange={handleClienteChange}
                            required
                        />
                    </div>

                    <h2>Dados da Conta</h2>

                    <div>
                        <label>Agência</label>
                        <input
                            type="number"
                            name="agenciaId"
                            value={conta.agenciaId}
                            onChange={handleContaChange}
                            required
                        />
                    </div>

                    <div>
                        <label>Número da Conta</label>
                        <input
                            type="number"
                            name="numero"
                            value={conta.numero}
                            onChange={handleContaChange}
                            required
                        />
                    </div>

                    <div>
                        <label>Saldo Inicial</label>
                        <input
                            type="number"
                            name="saldo"
                            value={conta.saldo}
                            onChange={handleContaChange}
                            min="0"
                            step="0.01"
                        />
                    </div>

                    <button type="submit" disabled={loading}>
                        {loading ? 'Criando...' : 'Criar Cliente e Conta'}
                    </button>

                </form>
            </main>
        </div>
    );
}

export default CriarConta;