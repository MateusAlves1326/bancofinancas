import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './CriarConta.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function CriarConta() {
    const navigate = useNavigate();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    function handleMenuClick() {
        setSidebarOpen((prev) => !prev);
    }

    const [cliente, setCliente] = useState({
        nome: '',
        email: '',
        telefone: '',
        cpf: '',
        endereco: ''
    });

    const [endereco, setEndereco] = useState({
        rua: '',
        numero: '',
        bairro: '',
        cidade: '',
        uf: ''
    });

    const [senhaCliente, setSenhaCliente] = useState(() => {
        return String(Math.floor(1000 + Math.random() * 9000));
    });

    const [cep, setCep] = useState('');
    const [buscandoCep, setBuscandoCep] = useState(false);
    const [erroCep, setErroCep] = useState('');

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

    function handleEnderecoChange(event) {
        const { name, value } = event.target;

        setEndereco((prev) => ({
            ...prev,
            [name]: value
        }));
    }

    function handleContaChange(event) {
        const { name, value } = event.target;

        setConta({
            ...conta,
            [name]: value
        });
    }

    function handleCepChange(event) {
        const valor = event.target.value.replace(/\D/g, '').slice(0, 8);
        setCep(valor);
        setErroCep('');
    }

    async function handleBuscarCep() {
        if (cep.length !== 8) {
            setErroCep('CEP inválido');
            return;
        }

        setBuscandoCep(true);
        setErroCep('');

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();

            if (data.erro) {
                setErroCep('CEP não encontrado');
                return;
            }

            setEndereco((prev) => ({
                ...prev,
                rua: data.logradouro || prev.rua,
                bairro: data.bairro || prev.bairro,
                cidade: data.localidade || prev.cidade,
                uf: data.uf || prev.uf,
            }));
        } catch (error) {
            console.error(error);
            setErroCep('Erro ao buscar o CEP');
        } finally {
            setBuscandoCep(false);
        }
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

            const enderecoCompleto = [
                endereco.rua,
                endereco.numero,
                endereco.bairro,
                endereco.cidade,
                endereco.uf,
            ].filter(Boolean).join(', ');

            // 1. Criar o cliente
            const clienteResponse = await fetch(`${API_URL}/clientes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    ...cliente,
                    endereco: enderecoCompleto,
                })
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
                    saldo: Number(conta.saldo),
                    senha: senhaCliente
                })
            });

            const contaBody = await contaResponse.json().catch(() => ({}));

            if (!contaResponse.ok) {
                throw new Error(
                    contaBody.message || 'Cliente criado, mas ocorreu um erro ao criar a conta',
                );
            }

            {
                !loading && erro && (
                    <p className="clients-feedback error" role="alert">{erro}</p>
                )
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
        <div className={`criar-conta ${sidebarOpen ? 'sidebar-open' : ''}`}>
            <Header onMenuClick={handleMenuClick} />
            {sidebarOpen && <Sidebar onMenuClick={handleMenuClick} />}
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

                        <div>
                            <label>CEP</label>
                            <input
                                type="text"
                                name="cep"
                                value={cep}
                                onChange={handleCepChange}
                                maxLength={8}
                                placeholder="Somente números"
                            />
                            <button type="button" onClick={handleBuscarCep} disabled={buscandoCep}>
                                {buscandoCep ? 'Buscando...' : 'Buscar Endereço'}
                            </button>
                            {erroCep && <p className="erro">{erroCep}</p>}
                        </div>

<div style={{ display: 'grid', gridTemplateColumns: '2fr 120px 1fr', gap: '12px' }}>
                            <div>
                                <label>Rua</label>
                                <input
                                    type="text"
                                    name="rua"
                                    value={endereco.rua}
                                    onChange={handleEnderecoChange}
                                />
                            </div>
                            <div>
                                <label>Número</label>
                                <input
                                    type="text"
                                    name="numero"
                                    value={endereco.numero}
                                    onChange={handleEnderecoChange}
                                />
                            </div>
                            <div>
                                <label>Bairro</label>
                                <input
                                    type="text"
                                    name="bairro"
                                    value={endereco.bairro}
                                    onChange={handleEnderecoChange}
                                />
                            </div>
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 0.7fr', gap: '12px' }}>
                            <div>
                                <label>Cidade</label>
                                <input
                                    type="text"
                                    name="cidade"
                                    value={endereco.cidade}
                                    onChange={handleEnderecoChange}
                                />
                            </div>
                            <div>
                                <label>UF</label>
                                <input
                                    type="text"
                                    name="uf"
                                    value={endereco.uf}
                                    onChange={handleEnderecoChange}
                                    maxLength={2}
                                    style={{ textTransform: 'uppercase' }}
                                />
                            </div>
                        </div>

                        <div>
                            <label>Senha do cliente</label>
                            <input
                                type="text"
                                value={senhaCliente}
                                readOnly
                                style={{ background: '#f5f5f5' }}
                            />
                            <button type="button" onClick={() => setSenhaCliente(String(Math.floor(1000 + Math.random() * 9000)))}>
                                Gerar nova senha
                            </button>
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