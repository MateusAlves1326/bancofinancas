import Header from '../../components/Header/Header';
import Sidebar from '../../components/SideBar/Sidebar';
import './HomeAgente.css';

function HomeAgente() {
  return (
    <div className="agency-layout">
      <Sidebar />
      <div className="agency-content-area">
        <Header />
        <main className="agency-main">
          <div className="agency-heading">
            <p className="agency-eyebrow">Painel da agencia</p>
            <h1>Visao geral</h1>
            <p>Acompanhe os principais dados e atividades da sua agencia.</p>
          </div>

          <section className="agency-summary" aria-label="Resumo da agencia">
            <article className="summary-card">
              <span className="summary-label">Clientes</span>
              <strong>0</strong>
              <span className="summary-caption">clientes cadastrados</span>
            </article>
            <article className="summary-card">
              <span className="summary-label">Contas</span>
              <strong>0</strong>
              <span className="summary-caption">contas ativas</span>
            </article>
            <article className="summary-card">
              <span className="summary-label">Operacoes</span>
              <strong>0</strong>
              <span className="summary-caption">operacoes recentes</span>
            </article>
          </section>

          <section className="agency-activity">
            <div>
              <p className="agency-eyebrow">Atividade</p>
              <h2>Operacoes recentes</h2>
            </div>
            <p className="empty-activity">Nenhuma operacao registrada ainda.</p>
          </section>
        </main>
      </div>
    </div>
  );
}

export default HomeAgente;