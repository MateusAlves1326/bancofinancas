import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ClienteHome.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function parseJwtPayload(token) {
  try {
    const base64Payload = token.split('.')[1]
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const jsonPayload = decodeURIComponent(
      atob(base64Payload)
        .split('')
        .map((char) => `%${(`00${char.charCodeAt(0).toString(16)}`).slice(-2)}`)
        .join(''),
    );

    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

function extrairClienteIdDoToken(token) {
  const payload = parseJwtPayload(token);
  const subject = payload?.sub;

  if (!subject) {
    return null;
  }

  const match = String(subject).match(/^cliente-(\d+)$/i);
  if (!match) {
    return null;
  }

  return Number(match[1]);
}

function ClienteArea() {
  const navigate = useNavigate();
  const [abaAtiva, setAbaAtiva] = useState('conta');
  const [clienteId, setClienteId] = useState(null);
  const [conta, setConta] = useState(null);
  const [extrato, setExtrato] = useState([]);
  const [itensLoja, setItensLoja] = useState([]);
  const [pedidos, setPedidos] = useState([]);
  const [valorSaque, setValorSaque] = useState('');
  const [valorPagamento, setValorPagamento] = useState('');
  const [codigoPagamentoLoja, setCodigoPagamentoLoja] = useState('');
  const [extratoSelecionado, setExtratoSelecionado] = useState('');
  const [motivoReversao, setMotivoReversao] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');

  const extratosReversiveis = useMemo(() => (
    extrato.filter((item) => ['SAQUE', 'DEPOSITO', 'PAGAMENTO', 'COMPRA', 'TRANSFERENCIA'].includes(item.operacao))
  ), [extrato]);

  function logout() {
    localStorage.removeItem('bancofinancas.token');
    navigate('/clientes/login', { replace: true });
  }

  async function carregarContaEExtrato() {
    const token = localStorage.getItem('bancofinancas.token');
    if (!token) {
      navigate('/clientes/login', { replace: true });
      return;
    }

    const idCliente = extrairClienteIdDoToken(token);
    if (!idCliente) {
      localStorage.removeItem('bancofinancas.token');
      navigate('/clientes/login', { replace: true });
      return;
    }

    setClienteId(idCliente);

    const contasResponse = await fetch(`${API_URL}/contas`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (contasResponse.status === 401 || contasResponse.status === 403) {
      logout();
      return;
    }

    if (!contasResponse.ok) {
      throw new Error('Não foi possível carregar os dados da conta.');
    }

    const contas = await contasResponse.json();
    const contaDoCliente = contas.find((item) => Number(item.clienteId) === idCliente);

    if (!contaDoCliente) {
      throw new Error('Conta do cliente não encontrada.');
    }

    setConta(contaDoCliente);

    const extratoResponse = await fetch(`${API_URL}/operacoes/${contaDoCliente.id}?clienteId=${idCliente}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (extratoResponse.status === 401 || extratoResponse.status === 403) {
      logout();
      return;
    }

    if (!extratoResponse.ok) {
      throw new Error('Não foi possível carregar o extrato.');
    }

    const extratoData = await extratoResponse.json();
    setExtrato(extratoData);

    const [itensResponse, pedidosResponse] = await Promise.all([
      fetch(`${API_URL}/loja/itens`, {
        headers: { Authorization: `Bearer ${token}` },
      }),
      fetch(`${API_URL}/loja/pedidos/me`, {
        headers: { Authorization: `Bearer ${token}` },
      }),
    ]);

    if (itensResponse.status === 401 || itensResponse.status === 403 || pedidosResponse.status === 401 || pedidosResponse.status === 403) {
      logout();
      return;
    }

    if (!itensResponse.ok) {
      throw new Error('Não foi possível carregar os itens da loja.');
    }
    if (!pedidosResponse.ok) {
      throw new Error('Não foi possível carregar os pedidos da loja.');
    }

    setItensLoja(await itensResponse.json());
    setPedidos(await pedidosResponse.json());
  }

  useEffect(() => {
    async function carregar() {
      setLoading(true);
      setErro('');

      try {
        await carregarContaEExtrato();
      } catch (error) {
        setErro(error.message || 'Erro ao carregar a área do cliente.');
      } finally {
        setLoading(false);
      }
    }

    carregar();
  }, [navigate]);

  async function registrarOperacao(operacao, valor) {
    const token = localStorage.getItem('bancofinancas.token');
    if (!token || !conta || !clienteId) {
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(`${API_URL}/operacoes`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          contaCorrenteId: conta.id,
          clienteId,
          operacao,
          valorOperacao: Number(valor),
        }),
      });

      if (response.status === 401 || response.status === 403) {
        logout();
        return;
      }

      const body = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(body.message || 'Não foi possível concluir a operação.');
      }

      setMensagem(`${operacao} realizado com sucesso.`);
      setValorSaque('');
      setValorPagamento('');
      await carregarContaEExtrato();
    } catch (error) {
      setErro(error.message || 'Erro ao processar a operação.');
    } finally {
      setSubmitting(false);
    }
  }

  async function comprarItem(itemId) {
    const token = localStorage.getItem('bancofinancas.token');
    if (!token || !conta) {
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(`${API_URL}/loja/pedidos`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          itemId,
          contaCorrenteId: conta.id,
        }),
      });

      if (response.status === 401 || response.status === 403) {
        logout();
        return;
      }

      const body = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(body.message || 'Não foi possível gerar o pedido.');
      }

      setMensagem(`Pedido criado. Código para pagamento: ${body.codigoPagamento}`);
      setCodigoPagamentoLoja(body.codigoPagamento || '');
      setAbaAtiva('conta');
      await carregarContaEExtrato();
    } catch (error) {
      setErro(error.message || 'Erro ao criar pedido da loja.');
    } finally {
      setSubmitting(false);
    }
  }

  async function pagarCodigoLoja() {
    const token = localStorage.getItem('bancofinancas.token');
    if (!token || !codigoPagamentoLoja.trim()) {
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(`${API_URL}/loja/pedidos/pagar`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          codigoPagamento: codigoPagamentoLoja.trim(),
        }),
      });

      if (response.status === 401 || response.status === 403) {
        logout();
        return;
      }

      const body = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(body.message || 'Não foi possível pagar o pedido da loja.');
      }

      setMensagem('Pagamento confirmado. Pedido aprovado e em preparo para envio.');
      setCodigoPagamentoLoja('');
      setAbaAtiva('pedidos');
      await carregarContaEExtrato();
    } catch (error) {
      setErro(error.message || 'Erro ao pagar pedido da loja.');
    } finally {
      setSubmitting(false);
    }
  }

  function formatarStatusPedido(status) {
    const mapa = {
      AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
      EM_PREPARO: 'Em preparo',
      ENVIADO: 'Enviado',
      ENTREGUE: 'Entregue',
      REEMBOLSADO: 'Reembolsado',
    };
    return mapa[status] || status;
  }

  async function solicitarReversao() {
    const token = localStorage.getItem('bancofinancas.token');
    if (!token || !conta || !clienteId || !extratoSelecionado) {
      return;
    }

    const extratoOrigem = extrato.find((item) => String(item.idExtrato) === String(extratoSelecionado));
    if (!extratoOrigem) {
      setErro('Selecione um item do extrato para solicitar reversão.');
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(`${API_URL}/operacoes/reverter/solicitar`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          contaCorrenteId: conta.id,
          clienteId,
          extratoOrigemId: extratoOrigem.idExtrato,
          operacao: extratoOrigem.operacao,
          valorOperacao: Number(extratoOrigem.valorOperacao),
          motivo: motivoReversao,
        }),
      });

      if (response.status === 401 || response.status === 403) {
        logout();
        return;
      }

      const body = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(body.message || 'Não foi possível solicitar a reversão.');
      }

      setMensagem('Solicitação de reversão enviada com sucesso.');
      setExtratoSelecionado('');
      setMotivoReversao('');
    } catch (error) {
      setErro(error.message || 'Erro ao solicitar reversão.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="cliente-home">
      <header className="cliente-home__header">
        <div>
          <p className="cliente-home__eyebrow">Área do Cliente</p>
          <h1>Conta Corrente</h1>
          <p>Faça saques, pagamentos, consulte o extrato e solicite reversões.</p>
        </div>
        <button type="button" onClick={logout}>Sair</button>
      </header>

      {loading && <p className="cliente-home__status">Carregando dados da conta...</p>}
      {!loading && erro && <p className="cliente-home__status cliente-home__status--error" role="alert">{erro}</p>}
      {!loading && mensagem && <p className="cliente-home__status cliente-home__status--ok" role="status">{mensagem}</p>}

      {!loading && !erro && conta && (
        <>
          <section className="cliente-home__tabs" aria-label="Abas da área do cliente">
            <button
              type="button"
              className={abaAtiva === 'conta' ? 'is-active' : ''}
              onClick={() => setAbaAtiva('conta')}
            >
              Conta
            </button>
            <button
              type="button"
              className={abaAtiva === 'loja' ? 'is-active' : ''}
              onClick={() => setAbaAtiva('loja')}
            >
              Loja
            </button>
            <button
              type="button"
              className={abaAtiva === 'pedidos' ? 'is-active' : ''}
              onClick={() => setAbaAtiva('pedidos')}
            >
              Status dos pedidos
            </button>
          </section>

          {abaAtiva === 'conta' && (
            <>
          <section className="cliente-home__cards">
            <article className="cliente-home__card">
              <h2>Dados da Conta</h2>
              <p><strong>Cliente:</strong> {conta.clienteNome}</p>
              <p><strong>Agência:</strong> {conta.agenciaId}</p>
              <p><strong>Número:</strong> {conta.numero}</p>
              <p><strong>Saldo:</strong> {Number(conta.saldo).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</p>
              <p><strong>Status:</strong> {conta.bloqueada ? 'Bloqueada' : 'Ativa'}</p>
            </article>

            <article className="cliente-home__card">
              <h2>Saque</h2>
              <input
                type="number"
                min="0.01"
                step="0.01"
                placeholder="Valor do saque"
                value={valorSaque}
                onChange={(event) => setValorSaque(event.target.value)}
              />
              <button
                type="button"
                disabled={submitting || !valorSaque}
                onClick={() => registrarOperacao('SAQUE', valorSaque)}
              >
                {submitting ? 'Processando...' : 'Realizar saque'}
              </button>
            </article>

            <article className="cliente-home__card">
              <h2>Pagamento</h2>
              <input
                type="number"
                min="0.01"
                step="0.01"
                placeholder="Valor do pagamento"
                value={valorPagamento}
                onChange={(event) => setValorPagamento(event.target.value)}
              />
              <button
                type="button"
                disabled={submitting || !valorPagamento}
                onClick={() => registrarOperacao('PAGAMENTO', valorPagamento)}
              >
                {submitting ? 'Processando...' : 'Realizar pagamento'}
              </button>

              <hr />

              <label htmlFor="codigo-pagamento-loja">Código de pagamento da loja</label>
              <input
                id="codigo-pagamento-loja"
                type="text"
                placeholder="Cole aqui o código gerado na loja"
                value={codigoPagamentoLoja}
                onChange={(event) => setCodigoPagamentoLoja(event.target.value)}
              />
              <button
                type="button"
                disabled={submitting || !codigoPagamentoLoja.trim()}
                onClick={pagarCodigoLoja}
              >
                {submitting ? 'Confirmando...' : 'Pagar código da loja'}
              </button>
            </article>
          </section>

          <section className="cliente-home__card cliente-home__card--full">
            <h2>Solicitar reversão</h2>
            <select value={extratoSelecionado} onChange={(event) => setExtratoSelecionado(event.target.value)}>
              <option value="">Selecione uma operação do extrato</option>
              {extratosReversiveis.map((item) => (
                <option key={item.idExtrato} value={item.idExtrato}>
                  #{item.idExtrato} - {item.operacao} - {Number(item.valorOperacao).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                </option>
              ))}
            </select>
            <textarea
              placeholder="Motivo da solicitação"
              value={motivoReversao}
              onChange={(event) => setMotivoReversao(event.target.value)}
              rows={3}
            />
            <button
              type="button"
              disabled={submitting || !extratoSelecionado || !motivoReversao.trim()}
              onClick={solicitarReversao}
            >
              {submitting ? 'Enviando...' : 'Solicitar reversão'}
            </button>
          </section>

          <section className="cliente-home__card cliente-home__card--full">
            <div className="cliente-home__table-header">
              <h2>Extrato</h2>
              <button type="button" onClick={carregarContaEExtrato} disabled={submitting}>Atualizar</button>
            </div>

            {extrato.length === 0 ? (
              <p>Nenhuma movimentação encontrada.</p>
            ) : (
              <div className="cliente-home__table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Data/Hora</th>
                      <th>Operação</th>
                      <th>Valor</th>
                    </tr>
                  </thead>
                  <tbody>
                    {extrato.map((item) => (
                      <tr key={item.idExtrato}>
                        <td>{item.idExtrato}</td>
                        <td>{new Date(item.dataHoraMovimento).toLocaleString('pt-BR')}</td>
                        <td>{item.operacao}</td>
                        <td>{Number(item.valorOperacao).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
            </>
          )}

          {abaAtiva === 'loja' && (
            <section className="cliente-home__card cliente-home__card--full">
              <div className="cliente-home__table-header">
                <h2>Loja</h2>
                <button type="button" onClick={carregarContaEExtrato} disabled={submitting}>Atualizar itens</button>
              </div>

              {itensLoja.length === 0 ? (
                <p>Não há itens disponíveis no momento.</p>
              ) : (
                <div className="cliente-home__items-grid">
                  {itensLoja.map((item) => (
                    <article key={item.id} className="cliente-home__item-card">
                      <h3>{item.nome}</h3>
                      <p>{item.descricao}</p>
                      <p><strong>Preço:</strong> {Number(item.preco).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</p>
                      <p><strong>Estoque:</strong> {item.estoque}</p>
                      <button
                        type="button"
                        disabled={submitting || !item.ativo || item.estoque <= 0}
                        onClick={() => comprarItem(item.id)}
                      >
                        {submitting ? 'Processando...' : 'Comprar item'}
                      </button>
                    </article>
                  ))}
                </div>
              )}
            </section>
          )}

          {abaAtiva === 'pedidos' && (
            <section className="cliente-home__card cliente-home__card--full">
              <div className="cliente-home__table-header">
                <h2>Status dos pedidos</h2>
                <button type="button" onClick={carregarContaEExtrato} disabled={submitting}>Atualizar pedidos</button>
              </div>

              {pedidos.length === 0 ? (
                <p>Você ainda não possui pedidos na loja.</p>
              ) : (
                <div className="cliente-home__table-wrapper">
                  <table>
                    <thead>
                      <tr>
                        <th>Pedido</th>
                        <th>Item</th>
                        <th>Valor</th>
                        <th>Status</th>
                        <th>Código pagamento</th>
                        <th>Entrega</th>
                      </tr>
                    </thead>
                    <tbody>
                      {pedidos.map((pedido) => (
                        <tr key={pedido.id}>
                          <td>#{pedido.id}</td>
                          <td>{pedido.itemNome}</td>
                          <td>{Number(pedido.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td>
                          <td>{formatarStatusPedido(pedido.status)}</td>
                          <td>{pedido.codigoPagamento}</td>
                          <td>{pedido.enderecoEntrega}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </section>
          )}
        </>
      )}
    </main>
  );
}

export default ClienteArea;
