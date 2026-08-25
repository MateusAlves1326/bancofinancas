import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Loja.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function Loja() {
  const navigate = useNavigate();

  // =========================
  // ESTADOS
  // =========================

  const [itens, setItens] = useState([]);
  const [pedidos, setPedidos] = useState([]);
  const [statusSelecionado, setStatusSelecionado] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [erro, setErro] = useState('');
  const [mensagem, setMensagem] = useState('');

  const [novoItem, setNovoItem] = useState({
    nome: '',
    descricao: '',
    preco: '',
    estoque: '',
  });

  // Pedido atualmente selecionado no modal
  const [pedidoSelecionado, setPedidoSelecionado] = useState(null);

  // =========================
  // PEDIDOS ORDENADOS
  // =========================

  const pedidosOrdenados = useMemo(
    () =>
      [...pedidos].sort(
        (a, b) =>
          new Date(b.dataCriacao) - new Date(a.dataCriacao)
      ),
    [pedidos],
  );

  // =========================
  // LOGOUT
  // =========================

  function logout() {
    localStorage.removeItem('bancofinancas.token');
    navigate('/login', { replace: true });
  }

  // =========================
  // FORMATAR STATUS
  // =========================

  function formatarStatus(status) {
    const mapa = {
      AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
      EM_PREPARO: 'Em preparo',
      ENVIADO: 'Enviado',
      ENTREGUE: 'Entregue',
      REEMBOLSADO: 'Reembolsado',
    };

    return mapa[status] || status;
  }

  // =========================
  // CARREGAR DADOS DA LOJA
  // =========================

  async function carregarLoja() {
    const token = localStorage.getItem('bancofinancas.token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    const [itensResponse, pedidosResponse] = await Promise.all([
      fetch(`${API_URL}/loja/itens`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }),

      fetch(`${API_URL}/loja/pedidos`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }),
    ]);

    if (
      itensResponse.status === 401 ||
      itensResponse.status === 403 ||
      pedidosResponse.status === 401 ||
      pedidosResponse.status === 403
    ) {
      logout();
      return;
    }

    if (!itensResponse.ok) {
      throw new Error(
        'Não foi possível carregar itens da loja.'
      );
    }

    if (!pedidosResponse.ok) {
      throw new Error(
        'Não foi possível carregar pedidos da loja.'
      );
    }

    const itensData = await itensResponse.json();
    const pedidosData = await pedidosResponse.json();

    setItens(itensData);
    setPedidos(pedidosData);

    setStatusSelecionado(
      pedidosData.reduce((acc, pedido) => {
        acc[pedido.id] = pedido.status;
        return acc;
      }, {}),
    );
  }

  // =========================
  // CARREGAMENTO INICIAL
  // =========================

  useEffect(() => {
    async function carregar() {
      setLoading(true);
      setErro('');

      try {
        await carregarLoja();
      } catch (error) {
        setErro(
          error.message ||
            'Erro ao carregar a página da loja.'
        );
      } finally {
        setLoading(false);
      }
    }

    carregar();
  }, [navigate]);

  // =========================
  // CRIAR ITEM
  // =========================

  async function criarItem(event) {
    event.preventDefault();

    const token = localStorage.getItem('bancofinancas.token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(
        `${API_URL}/loja/itens`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            nome: novoItem.nome,
            descricao: novoItem.descricao,
            preco: Number(novoItem.preco),
            estoque: Number(novoItem.estoque),
          }),
        }
      );

      if (
        response.status === 401 ||
        response.status === 403
      ) {
        logout();
        return;
      }

      const body = await response
        .json()
        .catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          body.message ||
            'Não foi possível criar o item.'
        );
      }

      setMensagem('Item criado com sucesso.');

      setNovoItem({
        nome: '',
        descricao: '',
        preco: '',
        estoque: '',
      });

      await carregarLoja();
    } catch (error) {
      setErro(
        error.message ||
          'Erro ao criar item.'
      );
    } finally {
      setSubmitting(false);
    }
  }

  // =========================
  // ATUALIZAR STATUS
  // =========================

  async function atualizarStatusPedido(pedidoId) {
    const token = localStorage.getItem('bancofinancas.token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    const status = statusSelecionado[pedidoId];

    if (!status) {
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(
        `${API_URL}/loja/pedidos/${pedidoId}/status`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            status,
          }),
        }
      );

      if (
        response.status === 401 ||
        response.status === 403
      ) {
        logout();
        return;
      }

      const body = await response
        .json()
        .catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          body.message ||
            'Não foi possível atualizar status do pedido.'
        );
      }

      setMensagem(
        'Status do pedido atualizado com sucesso.'
      );

      // Fecha o modal
      setPedidoSelecionado(null);

      await carregarLoja();
    } catch (error) {
      setErro(
        error.message ||
          'Erro ao atualizar status do pedido.'
      );
    } finally {
      setSubmitting(false);
    }
  }

  // =========================
  // REEMBOLSAR PEDIDO
  // =========================

  async function reembolsarPedido(pedidoId) {
    const token = localStorage.getItem('bancofinancas.token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    setSubmitting(true);
    setErro('');
    setMensagem('');

    try {
      const response = await fetch(
        `${API_URL}/loja/pedidos/${pedidoId}/reembolso`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (
        response.status === 401 ||
        response.status === 403
      ) {
        logout();
        return;
      }

      const body = await response
        .json()
        .catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          body.message ||
            'Não foi possível reembolsar o pedido.'
        );
      }

      setMensagem(
        'Reembolso realizado com sucesso.'
      );

      // Fecha o modal
      setPedidoSelecionado(null);

      await carregarLoja();
    } catch (error) {
      setErro(
        error.message ||
          'Erro ao reembolsar pedido.'
      );
    } finally {
      setSubmitting(false);
    }
  }

  // =========================
  // JSX
  // =========================

  return (
    <main className="loja-page">

      {/* ================= HEADER ================= */}

      <header className="loja-page__header">
        <div>
          <p className="loja-page__eyebrow">
            Painel da Loja
          </p>

          <h1>
            Gestão de Itens e Pedidos
          </h1>

          <p>
            Cadastre itens, acompanhe pedidos,
            atualize status e realize reembolsos.
          </p>
        </div>

        <button
          type="button"
          onClick={logout}
        >
          Sair
        </button>
      </header>

      {/* ================= MENSAGENS ================= */}

      {loading && (
        <p className="loja-page__status">
          Carregando dados da loja...
        </p>
      )}

      {!loading && erro && (
        <p
          className="loja-page__status loja-page__status--error"
          role="alert"
        >
          {erro}
        </p>
      )}

      {!loading && mensagem && (
        <p
          className="loja-page__status loja-page__status--ok"
          role="status"
        >
          {mensagem}
        </p>
      )}

      {/* ================= CONTEÚDO ================= */}

      {!loading && (
        <>

          {/* ================= NOVO ITEM ================= */}

          <section className="loja-page__card">
            <h2>Novo item</h2>

            <form
              className="loja-page__form"
              onSubmit={criarItem}
            >
              <input
                type="text"
                placeholder="Nome"
                value={novoItem.nome}
                onChange={(event) =>
                  setNovoItem((prev) => ({
                    ...prev,
                    nome: event.target.value,
                  }))
                }
                required
              />

              <input
                type="text"
                placeholder="Descrição"
                value={novoItem.descricao}
                onChange={(event) =>
                  setNovoItem((prev) => ({
                    ...prev,
                    descricao: event.target.value,
                  }))
                }
                required
              />

              <input
                type="number"
                min="0.01"
                step="0.01"
                placeholder="Preço"
                value={novoItem.preco}
                onChange={(event) =>
                  setNovoItem((prev) => ({
                    ...prev,
                    preco: event.target.value,
                  }))
                }
                required
              />

              <input
                type="number"
                min="0"
                step="1"
                placeholder="Estoque"
                value={novoItem.estoque}
                onChange={(event) =>
                  setNovoItem((prev) => ({
                    ...prev,
                    estoque: event.target.value,
                  }))
                }
                required
              />

              <button
                type="submit"
                disabled={submitting}
              >
                {submitting
                  ? 'Salvando...'
                  : 'Criar item'}
              </button>
            </form>
          </section>

          {/* ================= ITENS ================= */}

          <section className="loja-page__card">
            <h2>Itens da loja</h2>

            {itens.length === 0 ? (
              <p>
                Não há itens cadastrados.
              </p>
            ) : (
              <div className="loja-page__table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nome</th>
                      <th>Descrição</th>
                      <th>Preço</th>
                      <th>Estoque</th>
                    </tr>
                  </thead>

                  <tbody>
                    {itens.map((item) => (
                      <tr key={item.id}>
                        <td>{item.id}</td>

                        <td>{item.nome}</td>

                        <td>{item.descricao}</td>

                        <td>
                          {Number(
                            item.preco
                          ).toLocaleString(
                            'pt-BR',
                            {
                              style: 'currency',
                              currency: 'BRL',
                            }
                          )}
                        </td>

                        <td>
                          {item.estoque}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>

          {/* ================= PEDIDOS ================= */}

          <section className="loja-page__card">
            <h2>Pedidos</h2>

            {pedidosOrdenados.length === 0 ? (
              <p>
                Nenhum pedido registrado.
              </p>
            ) : (
              <div className="loja-page__table-wrapper">
                <table>

                  <thead>
                    <tr>
                      <th>Pedido</th>
                      <th>Cliente</th>
                      <th>Item</th>
                      <th>Valor</th>
                      <th>Status</th>
                      <th>Código pagamento</th>
                      <th>Endereço</th>
                      <th>Ações</th>
                    </tr>
                  </thead>

                  <tbody>
                    {pedidosOrdenados.map(
                      (pedido) => (
                        <tr key={pedido.id}>

                          <td>
                            #{pedido.id}
                          </td>

                          <td>
                            {pedido.clienteNome}
                          </td>

                          <td>
                            {pedido.itemNome}
                          </td>

                          <td>
                            {Number(
                              pedido.valor
                            ).toLocaleString(
                              'pt-BR',
                              {
                                style:
                                  'currency',
                                currency:
                                  'BRL',
                              }
                            )}
                          </td>

                          <td>
                            {formatarStatus(
                              pedido.status
                            )}
                          </td>

                          <td>
                            {pedido.codigoPagamento}
                          </td>

                          <td>
                            {pedido.enderecoEntrega}
                          </td>

                          {/* BOTÃO AÇÕES */}

                          <td>
                            <div className="loja-page__actions">

                              <button
                                type="button"
                                onClick={() =>
                                  setPedidoSelecionado(
                                    pedido
                                  )
                                }
                              >
                                Ações
                              </button>

                            </div>
                          </td>

                        </tr>
                      )
                    )}
                  </tbody>

                </table>
              </div>
            )}
          </section>

        </>
      )}

      {/* ================================================= */}
      {/* MODAL - FORA DA TABELA                           */}
      {/* ================================================= */}

      {pedidoSelecionado && (
        <div
          className="modal-overlay"
          onClick={() =>
            setPedidoSelecionado(null)
          }
        >

          <div
            className="modal"
            onClick={(event) =>
              event.stopPropagation()
            }
          >

            {/* BOTÃO FECHAR */}

            <button
              type="button"
              className="modal-close"
              onClick={() =>
                setPedidoSelecionado(null)
              }
              aria-label="Fechar"
            >
              &times;
            </button>

            {/* TÍTULO */}

            <h2>
              Ações do pedido #
              {pedidoSelecionado.id}
            </h2>

            <p>
              Cliente:{' '}
              {pedidoSelecionado.clienteNome}
            </p>

            <p>
              Item:{' '}
              {pedidoSelecionado.itemNome}
            </p>

            {/* STATUS */}

            <select
            className='select-status'
              value={
                statusSelecionado[
                  pedidoSelecionado.id
                ] ||
                pedidoSelecionado.status
              }
              onChange={(event) =>
                setStatusSelecionado(
                  (prev) => ({
                    ...prev,
                    [pedidoSelecionado.id]:
                      event.target.value,
                  })
                )
              }
              disabled={
                submitting ||
                pedidoSelecionado.status ===
                  'REEMBOLSADO' ||
                pedidoSelecionado.status ===
                  'AGUARDANDO_PAGAMENTO'
              }
            >

              <option value="EM_PREPARO">
                Em preparo
              </option>

              <option value="ENVIADO">
                Enviado
              </option>

              <option value="ENTREGUE">
                Entregue
              </option>

            </select>

            {/* ATUALIZAR */}

            <button
            className="refresh-button"
              type="button"
              disabled={
                submitting ||
                pedidoSelecionado.status ===
                  'REEMBOLSADO' ||
                pedidoSelecionado.status ===
                  'AGUARDANDO_PAGAMENTO'
              }
              onClick={() =>
                atualizarStatusPedido(
                  pedidoSelecionado.id
                )
              }
            >
              {submitting
                ? 'Processando...'
                : 'Atualizar'}
            </button>

            {/* REEMBOLSAR */}

            <button
              type="button"
              className="is-danger"
              disabled={
                submitting ||
                pedidoSelecionado.status ===
                  'REEMBOLSADO' ||
                pedidoSelecionado.status ===
                  'AGUARDANDO_PAGAMENTO'
              }
              onClick={() =>
                reembolsarPedido(
                  pedidoSelecionado.id
                )
              }
            >
              Reembolsar
            </button>

          </div>
        </div>
      )}

    </main>
  );
}

export default Loja;